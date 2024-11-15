package org.example.task8;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class InfinitePi {
    private static double pi = 0.0;
    private static final Object mainLock = new Object();
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bad parameter");
            return;
        }

        int numberOfThreads = Integer.parseInt(args[0]);
        TaskManager piManager = new TaskManager(numberOfThreads);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("SIGINT received!");
            piManager.finishWork();

            System.out.println("Pi: " + pi * 4.0);
        }));
    }


    private static class TaskManager {
        private final int totalThreads;
        private final PiTask[] workers;
        private final Thread[] workerThreads;
        private final CountDownLatch latch;

        public TaskManager(int totalThreads) {
            this.totalThreads = totalThreads;
            this.workers = new PiTask[this.totalThreads];
            this.workerThreads = new Thread[this.totalThreads];
            this.latch = new CountDownLatch(this.totalThreads);
            for (int i = 0; i < this.totalThreads; i++) {
                this.workers[i] = new PiTask(i, this.totalThreads, latch);
                this.workerThreads[i] = new Thread(this.workers[i]);
                this.workerThreads[i].start();
            }
        }

        private long findMaxIterations() {
            long maxIterations = 0;
            for (PiTask worker : this.workers) {
                if (worker.getIterations() > maxIterations) {
                    maxIterations = worker.getIterations();
                }
            }
            return maxIterations;
        }

        private void joinWorkers() {
            for (int workerId = 0; workerId < this.totalThreads; workerId++) {
                try {
                    this.workerThreads[workerId].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void finishWork() {
            isRunning.set(false);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long maxIterations = this.findMaxIterations();
            for (int workerId = 0; workerId < this.totalThreads; workerId++) {
                long iterationDifference = maxIterations - this.workers[workerId].getIterations();
                if (iterationDifference > 0) {
                    this.workers[workerId].setRestLocalIterations(iterationDifference);
                } else {
                    this.workerThreads[workerId].interrupt();
                }
            }
            this.joinWorkers();
        }
    }


    private static class PiTask implements Runnable {
        private final int totalThreads;
        private double localSum;
        private long nextElementId;
        private long localIters;
        private final CountDownLatch latch;
        private final Object taskLock = new Object();

        public PiTask(int threadId, int totalThreads, CountDownLatch latch) {
            this.totalThreads = totalThreads;
            this.localSum = 0.0;
            this.nextElementId = threadId;
            this.localIters = 0;
            this.latch = latch;
        }

        private void addNewElement() {
            double element = 1.0 / (2 * this.nextElementId + 1);
            if (this.nextElementId % 2 != 0) {
                element *= -1;
            }
            this.localSum += element;
            this.nextElementId += this.totalThreads;
        }

        public long getIterations() {
            return this.localIters;
        }

        protected void setRestLocalIterations(long restIterations) {
            synchronized (taskLock) {
                this.localIters = restIterations;
                taskLock.notify();
            }
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                addNewElement();
                this.localIters++;
            }

            synchronized (taskLock) {
                try {
                    System.out.println(Thread.currentThread().threadId() + ": " + this.localIters);
                    latch.countDown();
                    taskLock.wait();
                    while (this.localIters > 0) {
                        addNewElement();
                        this.localIters--;
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    synchronized (mainLock) {
                        pi += localSum;
                    }
                }
            }
        }
    }
}

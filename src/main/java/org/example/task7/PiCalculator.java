package org.example.task7;

public class PiCalculator {
    private static final int TOTAL_ITERATIONS = 1000000;
    private static double pi = 0.0;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bad parameter");
            return;
        }

        int numberOfThreads = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new PiTask(i, numberOfThreads));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pi *= 4.0;
        System.out.println("Pi: " + pi);
    }

    static class PiTask implements Runnable {
        private final int threadId;
        private final int totalThreads;

        public PiTask(int threadId, int totalThreads) {
            this.threadId = threadId;
            this.totalThreads = totalThreads;
        }

        @Override
        public void run() {
            double localSum = 0.0;
            for (int j = threadId; j < TOTAL_ITERATIONS; j += totalThreads) {
                // Вычисляем текущий член ряда
                double elem = 1.0 / (2 * j + 1);
                if (j % 2 != 0) {
                    elem *= -1;
                }
                localSum += elem;
            }

            synchronized (lock) {
                pi += localSum;
            }
        }
    }
}

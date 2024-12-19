package org.example.task10;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public class ReaderWriter {
    private static final CountDownLatch parentStart = new CountDownLatch(1);
    private static final Semaphore mutex = new Semaphore(1);
    private static final int cycles = 10;

    private static final Runnable child = () -> {
        try {
            parentStart.await();
        } catch (InterruptedException e) {
            System.out.println("Failed to wait main thread start");
            return;
        }
        for (int i = 1; i <= cycles; ++i) {
            try {
                mutex.acquire();
            } catch (InterruptedException e) {}
            synchronized (mutex) {
                mutex.release();
                System.out.println("Message from CHILD " + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
        }
    };

    private static void parentExecFlow() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {}
        parentStart.countDown();
        for (int i = 1; i <= cycles; ++i) {
            if (i != 1) {
                try {
                    mutex.acquire();
                } catch (InterruptedException e) {}
            }
            synchronized (mutex) {
                mutex.release();
                System.out.println("Message from PARENT " + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
        }
    }

    public static void main(String[] args) {
        new Thread(child).start();
        parentExecFlow();
    }
}
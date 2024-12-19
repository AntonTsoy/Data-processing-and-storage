package org.example.task11;

import java.util.concurrent.Semaphore;

public class SemaphoreCounters {
    private static final int NUM_LINES = 10;
    private static final Semaphore parentSemaphore = new Semaphore(1);
    private static final Semaphore childSemaphore = new Semaphore(0);

    public static void main(String[] args) {
        Thread parentThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                try {
                    parentSemaphore.acquire();
                    System.out.println("PARENT: Line " + (i + 1));
                    childSemaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread childThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                try {
                    childSemaphore.acquire();
                    System.out.println("CHILD: Line " + (i + 1));
                    parentSemaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        parentThread.start();
        childThread.start();
    }
}

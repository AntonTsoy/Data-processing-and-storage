package org.example.task10;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedThreads {
    private static final int NUM_LINES = 9;
    private static final Lock lock = new ReentrantLock();
    private static final Condition parentCondition = lock.newCondition();
    private static final Condition childCondition = lock.newCondition();
    private static boolean parentTurn = true;

    public static void main(String[] args) {
        Thread parentThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                lock.lock();
                try {
                    if (!parentTurn) {
                        parentCondition.await();
                    }
                    System.out.println("Parent Thread: Line " + (i + 1));
                    parentTurn = false;
                    childCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread childThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                lock.lock();
                try {
                    if (parentTurn) {
                        childCondition.await();
                    }
                    System.out.println("Child Thread: Line " + (i + 1));
                    parentTurn = true;
                    parentCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        parentThread.start();
        childThread.start();
    }
}

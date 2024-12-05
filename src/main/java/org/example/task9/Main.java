package org.example.task9;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;


public class Main {
    private static final int PHILOSOPH_COUNT = 5;
    private static final long MIN_MEAL_PORTION = 500;
    private static final long MAX_MEAL_PORTION = 1_000;

    public static void main(String[] args) {
        ArrayList<Fork> forks = new ArrayList<>();
        for (int i = 0; i < PHILOSOPH_COUNT; ++i) {
            forks.add(new Fork());
        }
        ArrayList<Thread> philosophersThreads = getThreads(forks);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            philosophersThreads.forEach(Thread::interrupt);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Interruption was interrupted");
            }
        }));
    }

    private static ArrayList<Thread> getThreads(ArrayList<Fork> forks) {
        CyclicBarrier startBarrier = new CyclicBarrier(PHILOSOPH_COUNT);
        ArrayList<Thread> philosopherThreads = new ArrayList<>();
        for (int leftIndex = 0; leftIndex < PHILOSOPH_COUNT; ++leftIndex) {
            long mealPortion = Random.randlong(MIN_MEAL_PORTION, MAX_MEAL_PORTION);
            int rightIndex = leftIndex < PHILOSOPH_COUNT - 1 ? leftIndex + 1 : 0;
            Thread philosopherThread = new Thread(new Philosopher(
                    mealPortion, startBarrier, forks.get(leftIndex), forks.get(rightIndex)
            ));
            philosopherThread.start();
            philosopherThreads.add(philosopherThread);
        }
        return philosopherThreads;
    }
}

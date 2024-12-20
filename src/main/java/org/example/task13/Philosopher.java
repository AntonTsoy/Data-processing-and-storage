package org.example.task13;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Philosopher implements Runnable {
    private static final long MIN_WAIT = 100;
    private static final long MAX_WAIT = 200;
    private static final long MIN_FORK_DELAY = 20;
    private static final long MAX_FORK_DELAY = 40;

    private long mealPortion;
    protected final CyclicBarrier startBarrier;

    protected Fork leftFork;
    protected Fork rightFork;

    public Philosopher(long mealPortion, CyclicBarrier startBarrier, Fork leftFork, Fork rightFork) {
        this.mealPortion = mealPortion;
        this.startBarrier = startBarrier;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    @Override
    public void run() {
        try {
            startBarrier.await();
            while (mealPortion > 0) {
                smartEating();
            }
        } catch (BrokenBarrierException e) {
            System.out.println(Thread.currentThread() + " failed to start");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + " was interrupted");
        }
    }

    private void smartEating() throws InterruptedException {
        long thinkingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
        Thread.sleep(thinkingTime);
        if (leftFork.tryTake()) {
            Thread.sleep(Random.randlong(MIN_FORK_DELAY, MAX_FORK_DELAY));
            if (rightFork.tryTake()) {
                long eatingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
                Thread.sleep(eatingTime);
                rightFork.put();
                mealPortion -= eatingTime;
                if (mealPortion <= 0) {
                    System.out.println(Thread.currentThread() + " end of eating");
                }
                leftFork.put();
            } else {
                leftFork.put();
            }
        }
        while (true) {
            Fork switcher = leftFork;
            leftFork = rightFork;
            rightFork = switcher;

            leftFork.waitAndPick();
            Thread.sleep(Random.randlong(MIN_FORK_DELAY, MAX_FORK_DELAY));
            if (rightFork.tryTake()) {
                long eatingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
                Thread.sleep(eatingTime);
                rightFork.put();
                mealPortion -= eatingTime;
                if (mealPortion <= 0) {
                    System.out.println(Thread.currentThread() + " end of eating");
                }
                leftFork.put();
                break;
            } else {
                leftFork.put();
            }
        }
    }

    private void usualEating() throws InterruptedException {
        long thinkingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
        Thread.sleep(thinkingTime);
        if (leftFork.tryTake()) {
            Thread.sleep(Random.randlong(MIN_FORK_DELAY, MAX_FORK_DELAY));
            if (!rightFork.take(MIN_WAIT)) {
                leftFork.put();
                return;
            }
            long eatingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
            Thread.sleep(eatingTime);
            mealPortion -= eatingTime;
            rightFork.put();
            if (mealPortion <= 0) {
                System.out.println(Thread.currentThread() + " end of eating");
            }
            leftFork.put();
        }
    }
}

package org.example.task9;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Philosopher implements Runnable {
    private static final long MIN_WAIT = 100;
    private static final long MAX_WAIT = 200;
    private static final long MIN_FORK_DELAY = 20;
    private static final long MAX_FORK_DELAY = 40;

    private long mealPortion;
    protected final CyclicBarrier startBarrier;

    protected final Fork leftFork;
    protected final Fork rightFork;

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
                stupidEating();
            }
        } catch (BrokenBarrierException e) {
            System.out.println(Thread.currentThread() + " failed to start");
        } catch (DeadlockException e) {
            System.out.println(Thread.currentThread() + " has caught deadlock");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + " was interrupted");
        }
    }

    private void stupidEating() throws DeadlockException, InterruptedException {
        long thinkingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
        Thread.sleep(thinkingTime);
        if (!leftFork.take(MAX_WAIT * 20)) {
            throw new DeadlockException();
        }
        Thread.sleep(Random.randlong(MIN_FORK_DELAY, MAX_FORK_DELAY));
        if (!rightFork.take(MAX_WAIT * 20)) {
            throw new DeadlockException();
        }
        long eatingTime = Random.randlong(MIN_WAIT, MAX_WAIT);
        Thread.sleep(eatingTime);
        mealPortion -= eatingTime;
        rightFork.put();
        leftFork.put();
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

package org.example.task13;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


// Получилась реализация паттерна адаптер
public class Fork {
    private final Semaphore mutex = new Semaphore(1);

    public boolean take(long waitTime) throws InterruptedException {
        return mutex.tryAcquire(waitTime, TimeUnit.MILLISECONDS);
    }

    public boolean tryTake() {
        return mutex.tryAcquire();
    }

    public void put() {
        mutex.release();
    }

    public void waitAndPick() throws InterruptedException {
        mutex.acquire();
    }
}

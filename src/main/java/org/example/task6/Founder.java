package org.example.task6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final List<Runnable> workers;
    private final CyclicBarrier barrier;

    class Worker implements Runnable {
        final Department myDepartment;

        public Worker (final Department department) {
            this.myDepartment = department;
        }

        @Override
        public void run() {
            System.out.println(this.myDepartment.getIdentifier() + " " + (this.myDepartment.getWorkingSeconds() - 1));
            this.myDepartment.performCalculations();

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                return;
            }
        }
    }

    public Founder(final Company company) {
        this.workers = new ArrayList<>(company.getDepartmentsCount());
        this.barrier = new CyclicBarrier(company.getDepartmentsCount(), company::showCollaborativeResult);
        for (int i = 0; i < company.getDepartmentsCount(); i++) {
            this.workers.add(new Worker(company.getFreeDepartment(i)));
        }
    }

    public void start() {
        for (final Runnable worker : workers) {
            new Thread(worker).start();
        }
    }
}

package org.example.task14;

import java.util.concurrent.Semaphore;


public class DetailLine implements Runnable {

    private final Semaphore detail;
    private final int msForDetail;
    private final String detailName;

    public DetailLine(Semaphore detail, int msForDetail, String detailName) {
        this.detail = detail;
        this.msForDetail = msForDetail;
        this.detailName = detailName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(msForDetail);
            } catch (InterruptedException e) {
                System.out.println("Line with details " + detailName + " was interrupted");
                break;
            }
            System.out.println(detailName + ": " + (detail.availablePermits() + 1));
            detail.release();
        }
    }
}

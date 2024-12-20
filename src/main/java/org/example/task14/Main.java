package org.example.task14;

import java.util.concurrent.Semaphore;


public class Main {

    public static void main(String[] args) {
        int widgetsCnt = 0;
        final Semaphore detailA = new Semaphore(0);
        final Semaphore detailB = new Semaphore(0);
        final Semaphore detailC = new Semaphore(0);
        final Semaphore submodule = new Semaphore(0);

        new Thread(new DetailLine(detailA, 1000, "A")).start();
        new Thread(new DetailLine(detailB, 2000, "B")).start();
        new Thread(new DetailLine(detailC, 3000, "C")).start();
        new Thread(() -> {
            while (true) {
                try {
                    detailA.acquire();
                    detailB.acquire();
                } catch (InterruptedException e) {
                    System.out.println("Line with submodules was interrupted");
                    break;
                }
                System.out.println("Submodules: " + (submodule.availablePermits() + 1));
                submodule.release();
            }
        }).start();

        while (true) {
            try {
                submodule.acquire();
                detailC.acquire();
            } catch (InterruptedException e) {
                System.out.println("Line with widgets was interrupted");
                break;
            }
            widgetsCnt++;
            System.out.println("Widgets: " + widgetsCnt);
        }
    }
}

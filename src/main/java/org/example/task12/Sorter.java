package org.example.task12;

import java.util.ArrayList;


public class Sorter implements Runnable {

    private static final int DELAY = 5000;
    private final ArrayList<String> list;

    public Sorter(ArrayList<String> list) {
        this.list = list;
    }

    private void syncSort() {
        synchronized (list) {
            String tmp;
            for (int i = 0; i < list.size(); ++i) {
                for (int j = 0; j < list.size() - i - 1; ++j) {
                    if (list.get(j).compareTo(list.get(j+1)) > 0) {
                        tmp = list.get(j);
                        list.set(j, list.get(j+1));
                        list.set(j+1, tmp);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                System.out.println("Sorter was interrupted");
                break;
            }
            syncSort();
        }
    }
}

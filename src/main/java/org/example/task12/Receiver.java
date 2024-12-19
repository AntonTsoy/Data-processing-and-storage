package org.example.task12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Receiver implements Runnable {

    private final ArrayList<String> list;

    public Receiver(ArrayList<String> list) {
        this.list = list;
    }

    private void printList() {
        synchronized (list) {
            System.out.println("{-");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
            System.out.println("-}");
        }
    }

    private void addToList(String s) {
        synchronized (list) {
            list.add(s);
        }
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );
        String input = null;
        do {
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (input.isBlank()) {
                printList();
            } else {
                addToList(input);
            }
        } while (true);
    }
}

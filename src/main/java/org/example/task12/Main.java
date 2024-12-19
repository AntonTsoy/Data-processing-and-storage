package org.example.task12;

import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        new Thread(new Sorter(list)).start();
        new Thread(new Receiver(list)).start();
    }
}

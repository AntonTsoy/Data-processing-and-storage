package org.example.task9;

public class Random {
    public static long randlong(long leftBoard, long rightBoard) {
        return (long)(Math.random() * (rightBoard - leftBoard) + leftBoard);
    }
}

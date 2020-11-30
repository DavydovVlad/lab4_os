package com.company;

import java.awt.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Frame window = new Frame();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}

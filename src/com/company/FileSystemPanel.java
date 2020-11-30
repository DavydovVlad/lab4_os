package com.company;

import javax.swing.*;
import java.awt.*;

public class FileSystemPanel extends JPanel {
    private final FileSystem fileSystem;

    public FileSystemPanel(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        showDisk(g);
    }

    public void showDisk(Graphics g) {
        HDD hdd = fileSystem.getHdd();
        int len = 10;
        int p = 10;
        for (int i = 0; i <= hdd.getSectorsArray().length / 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (i * 25 + j >= hdd.getSectorsArray().length) {
                    return;
                }
                switch (hdd.getSectorsArray()[i * 25 + j].getSectorState()) {
                    case EMPTY:
                        g.setColor(new Color(228, 219, 217));
                        break;
                    case FILLED:
                        g.setColor(new Color(110, 147, 214));
                        break;
                    case SELECTED:
                        g.setColor(new Color(222, 60, 60));
                        break;
                }
                g.fillRect(p + j * len, p + i * len, len, len);
                g.setColor(Color.BLACK);
                g.drawRect(p + j * len, p + i * len, len, len);
            }
        }
    }
}

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
        fileSystem.showDisk(g);
    }
}

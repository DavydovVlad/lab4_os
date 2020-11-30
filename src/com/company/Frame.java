package com.company;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class Frame {
    public JFrame frame;
    private FileSystem fileSystem;
    private FileSystemPanel panel;
    private JTree fileManagerTree;
    private FileManager fileManager;
    private HDD hdd;
    /**
     * Launch the application.
     */
    Frame() {
        initialize();
    }

    private void initialize() {
        int width = 300;
        int height = 600;

        frame = new JFrame();
        frame.setName("Файловый менеджер");
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        int size;
        while (true) {
            try {
                size = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите размер диска (не более 1200 и не менее 4)"));
                if (size > 1200 || size < 4) {
                    throw new Exception();
                } else {
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Были введены некорректные данные", "Создание диска", JOptionPane.ERROR_MESSAGE);
            }
        }

        int sizeSector;
        while (true) {
            try {
                sizeSector = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите размер сектора диска (не более 1/4 от размера диска и не менее 1)"));
                if (sizeSector > size / 4 || sizeSector < 1) {
                    throw new Exception();
                } else {
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Были введены некорректные данные", "Создание диска", JOptionPane.ERROR_MESSAGE);
            }
        }

        hdd = new HDD(size, sizeSector);
        fileSystem = new FileSystem(hdd);
        fileManager = new FileManager(this, hdd, fileSystem);

        panel = new FileSystemPanel(fileSystem);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED,
                null, null, null, null));
        frame.getContentPane().add(panel);
        panel.setBounds(10, 11, width, height);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fileSystem.getRoot(), true);
        fileManagerTree = new JTree(root);
        root.add(new DefaultMutableTreeNode(new File("", 0)));

        JScrollPane scrollTree = new JScrollPane(fileManagerTree);
        frame.getContentPane().add(scrollTree);
        scrollTree.setBounds(500, 30, 240, 300);
        fileManagerTree.addTreeSelectionListener(e -> {
            fileManager.elementChosen();
            panel.repaint();
        });

        JButton addFileButton = new JButton("Add File");
        addFileButton.addActionListener(e -> {
            fileManager.addNewFile();
            panel.repaint();
        });
        addFileButton.setBounds(width + 50, 10, 100, 50);
        frame.getContentPane().add(addFileButton);

        JButton addCatalogButton = new JButton("Add Catalog");
        addCatalogButton.addActionListener(e -> {
            fileManager.addNewCatalog();
            panel.repaint();
        });
        addCatalogButton.setBounds(width + 50, 70, 100, 50);
        frame.getContentPane().add(addCatalogButton);

        JButton deleteFileButton = new JButton("Delete");
        deleteFileButton.setBounds(width + 50, 130, 100, 50);
        frame.getContentPane().add(deleteFileButton);
        deleteFileButton.addActionListener(e -> {
            fileManager.delete();
            panel.repaint();
        });

        JButton copyFileButton = new JButton("Choose");
        copyFileButton.setBounds(width + 50, 190, 100, 50);
        frame.getContentPane().add(copyFileButton);
        copyFileButton.addActionListener(e -> fileManager.choose());

        JButton pasteFileButton = new JButton("Paste");
        pasteFileButton.setBounds(width + 50, 250, 100, 50);
        frame.getContentPane().add(pasteFileButton);
        pasteFileButton.addActionListener(e -> {
            fileManager.paste();
            panel.repaint();
        });

        JButton moveFileButton = new JButton("Move");
        moveFileButton.setBounds(width + 50, 310, 100, 50);
        frame.getContentPane().add(moveFileButton);
        moveFileButton.addActionListener(e -> {
            fileManager.move();
            panel.repaint();
        });

    }

    public JTree getFileManagerTree() {
        return fileManagerTree;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}

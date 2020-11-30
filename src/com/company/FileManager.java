package com.company;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class FileManager {
    private final Frame parent;
    private DefaultMutableTreeNode buffer;
    private final HDD HDD;
    private final FileSystem fileSystem;

    public FileManager(Frame parent, HDD HDD, FileSystem fileSystem) {
        this.parent = parent;
        this.HDD = HDD;
        this.fileSystem = fileSystem;
    }

    public void elementChosen() {
        Arrays.stream(HDD.getSectorsArray()).filter(sector -> sector.getSectorState() == SectorStatus.SELECTED).forEach(sector -> sector.setSectorStatus(SectorStatus.FILLED));

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

        if (node == null)
            return;

        File chosenFile = (File) node.getUserObject();
        int curLink = chosenFile.getLink();
        while (curLink != -1) {
            HDD.getSectorsArray()[curLink].setSectorStatus(SectorStatus.SELECTED);
            curLink = parent.getFileSystem().getClustersArray()[curLink];
        }
    }

    public void addNewFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || !node.getAllowsChildren() || node.isLeaf()) {
            JOptionPane.showConfirmDialog(parent.frame, "Нужно выбрать каталог, где будет новый файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nameFile;
        while (true) {
            nameFile = JOptionPane.showInputDialog(parent.frame, "Введите название файла");
            if (nameFile == null) {
                return;
            } else if (nameFile.equals("")) {
                JOptionPane.showMessageDialog(parent.frame, "Пустое название недопустимо", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            } else {
                break;
            }
        }
        int sizeFile;
        while (true) {
            try {
                sizeFile = Integer.parseInt(JOptionPane.showInputDialog(parent.frame, "Введите размер файла"));
                if (sizeFile < HDD.getSectorSize()) {
                    throw new Exception();
                } else {
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent.frame, "Были введены некорректные данные", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        File newFile = new File(nameFile, sizeFile);
        JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newFile), "Добавление файла", JOptionPane.INFORMATION_MESSAGE);

        if (fileSystem.isSuccess()) {
            fileSystem.setFailed(true);
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(newFile, false);
            if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                node.remove(0);
            }
            node.add(fileNode);
            parent.getFileManagerTree().updateUI();
            elementChosen();
            parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
        }
    }

    public void addNewCatalog() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || !node.getAllowsChildren()) {
            JOptionPane.showConfirmDialog(parent.frame, "Нужно выбрать каталог, где будет новый каталог",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nameCatalog;
        while (true) {
            nameCatalog = JOptionPane.showInputDialog(parent.frame, "Введите название каталога");
            if (nameCatalog == null) {
                return;
            } else if (nameCatalog.equals("")) {
                JOptionPane.showMessageDialog(parent.frame, "Пустое название недопустимо", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            } else {
                break;
            }
        }

        Catalog newCatalog = new Catalog(nameCatalog);
        JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newCatalog), "Добавление файла", JOptionPane.INFORMATION_MESSAGE);

        if (fileSystem.isSuccess()) {
            fileSystem.setFailed(true);
            if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                node.remove(0);
            }
            DefaultMutableTreeNode catalogNode = new DefaultMutableTreeNode(newCatalog, true);
            catalogNode.add(new DefaultMutableTreeNode(new File("", 0)));
            node.add(catalogNode);
            parent.getFileManagerTree().updateUI();
            elementChosen();
            parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
        }
    }

    public void delete() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || node.getParent() == null) {
            JOptionPane.showConfirmDialog(parent.frame, "Выберите файл, который хотите удалить",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(parent.frame, "Вы действительно хотите удалить файл " + node.getUserObject() + "?",
                "Удаление файла", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            JOptionPane.showMessageDialog(parent.frame, fileSystem.deleteFile((Catalog) parentNode.getUserObject(), (File) node.getUserObject()), "Удаление файла", JOptionPane.INFORMATION_MESSAGE);
            if (fileSystem.isSuccess()) {
                fileSystem.setFailed(true);
                parentNode.remove(node);
                if (parentNode.getChildCount() == 0) {
                    parentNode.add(new DefaultMutableTreeNode(new File("", 0)));
                }
                parent.getFileManagerTree().updateUI();
                parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
            }
        }
    }

    public void choose() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

        if (node == null || node.getParent() == null || ((File) node.getUserObject()).getName().equals("")) {
            JOptionPane.showConfirmDialog(parent.frame, "Выберите файл, который хотите скопировать",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        buffer = node;
        JOptionPane.showMessageDialog(parent.frame, "Файл " + node.getUserObject() + " скопирован в буфер обмена",
                "Копирование", JOptionPane.INFORMATION_MESSAGE);
    }

    public void paste() {
        if (buffer != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

            if (node == null || !node.getAllowsChildren() || node.isLeaf()) {
                JOptionPane.showConfirmDialog(parent.frame, "Выберите каталог, куда желаете вставить файл",
                        "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                return;
            }

            DefaultMutableTreeNode newNode = cloneNode(buffer);

            File newFile = (File) newNode.getUserObject();

            JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newFile), "Вставка файла", JOptionPane.INFORMATION_MESSAGE);

            if (fileSystem.isSuccess()) {
                if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                    node.remove(0);
                }
                fileSystem.setFailed(true);
                if (newFile.getClass() == Catalog.class) {
                    node.add(makeTree(new DefaultMutableTreeNode(newFile, true)));
                } else {
                    node.add(new DefaultMutableTreeNode(newFile, false));
                }

                parent.getFileManagerTree().updateUI();
                elementChosen();
                parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
                JOptionPane.showMessageDialog(parent.frame, "Вставка прошла успешно", "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showConfirmDialog(parent.frame, "Вы не скопировали файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        }

    }

    private DefaultMutableTreeNode makeTree(DefaultMutableTreeNode node) {
        Catalog c = (Catalog) node.getUserObject();
        if (c.getFiles().size() == 0) {
            node.add(new DefaultMutableTreeNode(new File("", 0), false));
            return node;
        }
        for (Iterator<File> iterator = c.getFiles().iterator(); iterator.hasNext(); ) {
            File file = iterator.next();
            if (file.getClass() == Catalog.class) {
                node.add(makeTree(new DefaultMutableTreeNode(file, true)));
            } else {
                node.add(new DefaultMutableTreeNode(file, false));
            }
        }
        return node;
    }

    private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        File nodeFile = (File) node.getUserObject();
        DefaultMutableTreeNode newNode;
        if (nodeFile.getClass() != Catalog.class) {
            newNode = new DefaultMutableTreeNode(new File(node.toString(), nodeFile.getSize()));
        } else {
            Catalog catalog = duplicateFiles(node.toString(), (Catalog) nodeFile);
            newNode = new DefaultMutableTreeNode(catalog);
            IntStream.range(0, node.getChildCount()).forEach(i -> {
                if (node.getChildAt(i).isLeaf()) {
                    DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node.getChildAt(i);
                    File file = (File) nodeChild.getUserObject();
                    newNode.add(new DefaultMutableTreeNode(new File(nodeChild.toString(), file.getSize())));
                } else {
                    newNode.add(cloneNode((DefaultMutableTreeNode) node.getChildAt(i)));
                }
            });
        }
        return newNode;
    }

    public Catalog duplicateFiles(String name, Catalog referenceCatalog) {
        Catalog catalog = new Catalog(name);
        LinkedList<File> files = new LinkedList<>();
        LinkedList<File> referenceCatalogFiles = referenceCatalog.getFiles();
        referenceCatalogFiles.stream().forEach(file -> {
            if (file.getClass() == Catalog.class) {
                files.add(duplicateFiles(file.getName(), (Catalog) file));
            } else {
                files.add(new File(file));
            }
        });
        catalog.setFiles(files);
        return catalog;
    }

    public void move() {
        if (buffer != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

            if (node == null || !node.getAllowsChildren() || node.isLeaf() || node == buffer || node.isNodeAncestor(buffer)) {
                JOptionPane.showConfirmDialog(parent.frame, "Выберите каталог, куда желаете вставить файл",
                        "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                return;
            }

            File file = (File) buffer.getUserObject();

            Catalog targetedCatalog = (Catalog) node.getUserObject();

            if (targetedCatalog.addFile(file) != 0) {
                DefaultMutableTreeNode bufferParent = (DefaultMutableTreeNode) buffer.getParent();
                bufferParent.remove(buffer);
                ((Catalog) bufferParent.getUserObject()).getFiles().remove(file);
                if (bufferParent.getChildCount() == 0) {
                    bufferParent.add(new DefaultMutableTreeNode(new File("", 0)));
                }
                if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                    node.remove(0);
                }
                node.add(buffer);
                buffer.setParent(node);
                parent.getFileManagerTree().updateUI();
                elementChosen();
                parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
                JOptionPane.showMessageDialog(parent.frame, "Перемещение прошло успешно", "Перемещение", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (file.getClass() == Catalog.class)
                    JOptionPane.showMessageDialog(parent.frame, "Каталог " + file.getName() + " уже существует в каталоге " + targetedCatalog.getName(), "Перемещение каталога", JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(parent.frame, "Файл " + file.getName() + " уже существует в каталоге " + targetedCatalog.getName(), "Перемещение файла", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent.frame, "Вы не скопировали файл",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
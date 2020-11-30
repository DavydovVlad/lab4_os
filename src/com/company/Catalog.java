package com.company;

import java.util.Iterator;
import java.util.LinkedList;

public class Catalog extends File {
    private LinkedList<File> files = new LinkedList<>();

    public Catalog(String name) {
        super(name, 1);
    }

    public int addFile(File newFile) {
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (newFile.getClass() == file.getClass() && newFile.getName().equals(file.getName())) {
                return 0;
            }
        }
        files.add(newFile);
        return 1;
    }

    public int deleteFile(File currentFile, FileSystem fileSystem) {
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
            File file = iterator.next();
            if (currentFile.getName().equals(file.getName())) {
                if (currentFile.getClass() == Catalog.class && file.getClass() == Catalog.class) {
                    Catalog catalog = (Catalog) currentFile;
                    for (int i = 0; i < catalog.files.size(); i++) {
                        catalog.deleteFile(catalog.files.get(i), fileSystem);
                        i--;
                    }
                }
                fileSystem.deleteFromDisk(currentFile);
                files.remove(currentFile);
                return 1;
            }
        }
        return 0;
    }

    public LinkedList<File> getFiles() {
        return files;
    }

    public void setFiles(LinkedList<File> files) {
        this.files = files;
    }

    public int getFullSize() {
        int size = getSize();
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
            File file = iterator.next();
            if (file.getClass() == Catalog.class) {
                size += ((Catalog) file).getFullSize();
            } else {
                size += file.getSize();
            }
        }
        return size;
    }
}
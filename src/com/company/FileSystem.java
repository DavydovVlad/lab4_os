package com.company;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;

public class FileSystem {

    private final HDD hdd;
    private final int[] clustersArray;
    private Catalog root;
    private boolean failed = true;
    public FileSystem(HDD hdd) {
        this.hdd = hdd;
        clustersArray = new int[hdd.getSize()];
        Arrays.fill(clustersArray, -2);
        initRootFolder();
    }

    private void initRootFolder() {
        root = new Catalog("root");
        root.setLink(0);
        clustersArray[0] = -1;
        hdd.getSectorsArray()[0].setSectorStatus(SectorStatus.FILLED);
        hdd.decreeFreeSectors();
    }

    public String addFile(Catalog catalog, File file) {
        if (file.getSize() == 0) {
            return "Добавление пустого файла";
        }
        int fullSize;
        if (file.getClass() == Catalog.class) {
            fullSize = ((Catalog) file).getFullSize();
        } else {
            fullSize = file.getSize();
        }
        int fileSectorSize = fullSize / hdd.getSectorSize();
        if (hdd.getFreeSectors() < fileSectorSize)
            return "Недостаточно места на диске для создания данного файла";
        if (catalog.addFile(file) != 0) {
            failed = false;
            System.out.println("Rest: " + (hdd.getFreeSectors() - fileSectorSize));
            file.setLink(addInDisk(fileSectorSize));
            if (file.getClass() == Catalog.class) {
                allocateAllEntireFiles((Catalog) file);
                return "Каталог " + file.getName() + " успешно создан в каталоге " + catalog.getName();
            } else
                return "Файл " + file.getName() + " успешно создан в каталоге " + catalog.getName();
        } else {
            if (file.getClass() == Catalog.class)
                return "Каталог " + file.getName() + " уже существует в каталоге " + catalog.getName();
            else
                return "Файл " + file.getName() + " уже существует в каталоге " + catalog.getName();
        }
    }

    private void allocateAllEntireFiles(Catalog file) {
        for (Iterator<File> iterator = file.getFiles().iterator(); iterator.hasNext(); ) {
            File f = iterator.next();
            f.setLink(addInDisk(f.getSize() / hdd.getSectorSize()));
            if (f.getClass() == Catalog.class) {
                allocateAllEntireFiles((Catalog) f);
            }
        }
    }

    private int addInDisk(int fileSectorSize) {
        int i = 0;
        int startIndex = 0;
        int prevIndex = -1;
        while (i < fileSectorSize) {
            int indexInCluster = Main.getRandomNumber(0, hdd.getSectorsNum() - 1);
            if (clustersArray[indexInCluster] == -2) {
                if (i == 0)
                    startIndex = indexInCluster;

                if (prevIndex != -1) {
                    clustersArray[prevIndex] = indexInCluster;
                }
                clustersArray[indexInCluster] = -1;
                hdd.getSectorsArray()[indexInCluster].setSectorStatus(SectorStatus.FILLED);
                prevIndex = indexInCluster;
                hdd.decreeFreeSectors();
                i++;
            }
        }
        return startIndex;
    }

    public String deleteFile(Catalog catalog, File fileName) {
        if (catalog.deleteFile(fileName, this)!=0) {
            failed = false;
            deleteFromDisk(fileName);
            if (fileName.getClass() == Catalog.class)
                return "Каталог " + fileName.getName() + " успешно удалён";
            else
                return "Файл " + fileName.getName() + " успешно удалён";
        } else {
            if (fileName.getClass() == Catalog.class)
                return "Каталог " + fileName.getName() + " не существует в каталоге " + catalog.getName();
            else
                return "Файл " + fileName.getName() + " не существует в каталоге " + catalog.getName();
        }
    }

    public void deleteFromDisk(File file) {
        int curLink = file.getLink();
        while (curLink != -1) {
            hdd.increeFreeSectors();
            hdd.getSectorsArray()[curLink].setSectorStatus(SectorStatus.EMPTY);
            int pastLink = curLink;
            curLink = clustersArray[curLink];
            clustersArray[pastLink] = -2;
        }
        file.setLink(-1);
    }

    public Catalog getRoot() {
        return root;
    }

    public int[] getClustersArray() {
        return clustersArray;
    }

    public boolean isSuccess() {
        return !failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public HDD getHdd() {
        return hdd;
    }
}
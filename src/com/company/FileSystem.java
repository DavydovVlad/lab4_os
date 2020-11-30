package com.company;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;

public class FileSystem {

    private final HDD HDD;
    private final int[] clustersArray;
    private Catalog root;
    private boolean failed = true;

    public FileSystem(HDD HDD) {
        this.HDD = HDD;
        clustersArray = new int[HDD.getSize()];
        Arrays.fill(clustersArray, -2);
        initRootFolder();
    }

    private void initRootFolder() {
        root = new Catalog("root");
        root.setLink(0);
        clustersArray[0] = -1;
        HDD.getSectorsArray()[0].setSectorStatus(SectorStatus.FILLED);
        HDD.decreeFreeSectors();
    }

    public void showDisk(Graphics g) {
        int len = 10;
        int p = 10;
        for (int i = 0; i <= HDD.getSectorsArray().length / 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (i * 25 + j >= HDD.getSectorsArray().length) {
                    return;
                }
                switch (HDD.getSectorsArray()[i * 25 + j].getSectorState()) {
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
        int fileSectorSize = fullSize / HDD.getSectorSize();
        if (HDD.getFreeSectors() < fileSectorSize)
            return "Недостаточно места на диске для создания данного файла";
        if (catalog.addFile(file) != 0) {
            failed = false;
            System.out.println("Rest: " + (HDD.getFreeSectors() - fileSectorSize));
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
            f.setLink(addInDisk(f.getSize() / HDD.getSectorSize()));
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
            int indexInCluster = Main.getRandomNumber(0, HDD.getSectorsNum() - 1);
            if (clustersArray[indexInCluster] == -2) {
                if (i == 0)
                    startIndex = indexInCluster;

                if (prevIndex != -1) {
                    clustersArray[prevIndex] = indexInCluster;
                }
                clustersArray[indexInCluster] = -1;
                HDD.getSectorsArray()[indexInCluster].setSectorStatus(SectorStatus.FILLED);
                prevIndex = indexInCluster;
                HDD.decreeFreeSectors();
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
            HDD.increeFreeSectors();
            HDD.getSectorsArray()[curLink].setSectorStatus(SectorStatus.EMPTY);
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
}
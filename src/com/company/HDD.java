package com.company;

import java.util.stream.IntStream;

public class HDD {

    private final int size;
    private int freeSectors;
    private final int sectorSize;
    private final int sectorsNum;
    private final Sector[] sectorsArray;

    public HDD(int size, int sectorsSize) {
        sectorsNum = size / sectorsSize;
        sectorsArray = new Sector[sectorsNum];
        IntStream.range(0, sectorsNum).forEach(i -> sectorsArray[i] = new Sector(SectorStatus.EMPTY));
        this.freeSectors = size / sectorsSize;
        this.size = size;
        this.sectorSize = sectorsSize;
    }

    public Sector[] getSectorsArray() {
        return sectorsArray;
    }

    public int getSize() {
        return size;
    }

    public int getSectorSize() {
        return sectorSize;
    }

    public int getFreeSectors() {
        return freeSectors;
    }

    public int getSectorsNum() {
        return sectorsNum;
    }

    public void decreeFreeSectors() {
        freeSectors--;
    }

    public void increeFreeSectors() {
        freeSectors++;
    }
}
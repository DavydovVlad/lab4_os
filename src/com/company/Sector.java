package com.company;

public class Sector {

    private SectorStatus sectorStatus;

    public Sector(SectorStatus sectorStatus) {
        this.sectorStatus = sectorStatus;
    }

    public SectorStatus getSectorState() {
        return sectorStatus;
    }

    public void setSectorStatus(SectorStatus sectorStatus) {
        this.sectorStatus = sectorStatus;
    }
}
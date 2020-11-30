package com.company;

public class File {
    private final String name;
    private int size;
    private int link;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public File(File file) {
        this.name = file.name;
        this.size = file.size;
        this.link = file.link;
    }

    public int getLink() {
        return link;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}

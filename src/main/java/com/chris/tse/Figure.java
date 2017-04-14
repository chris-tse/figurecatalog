package com.chris.tse;

/**
 * Created by christophertse on 2017/03/25.
 */
public class Figure {
    private String name;
    private FigureSize size;
    private String manufacturer;
    private boolean pub;

    public Figure (String name, FigureSize size, String manufacturer, boolean pub) {
        this.name = name;
        this.size = size;
        this.manufacturer = manufacturer;
        this.pub = pub;
    }

    public String getName() { return name; }

    public String getSize() {
        return size.toString();
    }

    public String getManufacturer() { return manufacturer; }

    public boolean isPub() { return pub; }

    public void setName(String name) { this.name = name; }

    public void setManufacturer(String man) { this.manufacturer = man; }

    public void setSize(String size) { this.size = FigureSize.valueOf(size.toUpperCase()); }
}

package com.chris.tse;

/**
 * Created by christophertse on 2017/03/25.
 */
public class Figure {
    private String name;
    private FigureSize size;
    private String price;

    public Figure (String name, FigureSize size, String price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }

    public String getName() { return name; }

    public String getSize() {
        return size.toString();
    }

    public String getPrice() {
        return price;
    }

    public void setName(String name) { this.name = name; }

    public void setPrice(String price) { this.price = price; }

    public void setSize(String size) { this.size = FigureSize.valueOf(size.toUpperCase()); }
}

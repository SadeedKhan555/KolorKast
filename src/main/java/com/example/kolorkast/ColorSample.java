package com.example.kolorkast;

public class ColorSample {
    private String colorname;

    public String getColorname() {
        return colorname;
    }

    public void setColorname(String colorname) {
        this.colorname = colorname;
    }

    @Override
    public String toString() {
        return "ColorSample{" +
                "colorname='" + colorname + '\'' +
                '}';
    }
}

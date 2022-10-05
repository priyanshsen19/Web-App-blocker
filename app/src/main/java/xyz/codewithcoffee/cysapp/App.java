package xyz.codewithcoffee.cysapp;

import android.graphics.drawable.Drawable;

public class App {

    private boolean selected;
    private String name;
    private String code;
    private String version;
    private Drawable icon;

    public App() {}

    public App(String name,String code, String version, Drawable icon, boolean selected)
    {
        this.name = name; 
        this.code = code;
        this.icon = icon;
        this.version = version;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}

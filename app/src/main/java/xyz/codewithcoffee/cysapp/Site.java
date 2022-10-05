package xyz.codewithcoffee.cysapp;

import java.io.Serializable;
import xyz.codewithcoffee.cyc_app.R;

public class Site implements Serializable {

    private String code = null;
    private String name = null;
    private boolean selected = false;

    public Site(String code, String name, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
    }


    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

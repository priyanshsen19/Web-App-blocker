package xyz.codewithcoffee.cysapp;

import android.graphics.Color;
import android.view.View;

import xyz.codewithcoffee.cyc_app.R;

public class CardElement {
    private int bg, icon;
    private String text;
    private View.OnClickListener ocl;
    private int color;
    CardElement(){
        bg=icon= R.drawable.transparent;
        color= Color.TRANSPARENT;
        ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        text="";
    }

    CardElement(View.OnClickListener ocl, int bg,int icon, String text, int color)
    {
        this.ocl = ocl;
        this.bg = bg;
        this.icon = icon;
        this.text = text;
        this.color=color;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) { this.text = text; }

    public View.OnClickListener getOcl() { return ocl; }

    public void setOcl(View.OnClickListener ocl) { this.ocl = ocl; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

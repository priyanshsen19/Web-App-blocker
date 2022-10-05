package xyz.codewithcoffee.cysapp;

public class Tym {

    private int hour,min;
    private String format;
    public static Tym NULLTYM = new Tym(-1,-1,"");

    public Tym(){}

    public Tym(int hour, int min, String format)
    {
        this.hour = hour;
        this.min = min;
        this.format = format;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}

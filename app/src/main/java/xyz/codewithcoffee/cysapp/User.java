package xyz.codewithcoffee.cysapp;

public class User
{
    private String username;
    private String uid;
    private int admin;

    public User() {}

    public User(String username,String uid, int admin) {
        this.username=username;
        this.uid=uid;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
package com.pantherman594.gitzucccd;

import android.graphics.Bitmap;

import java.util.HashMap;

public class Friend {
    private String name;
    private String username;
    private Bitmap profImg;
    private boolean isId;

    private static HashMap<String, Friend> friendDatabase = new HashMap<>();

    public static void addFriend(Friend friend) {
        friendDatabase.put(friend.getUsername(), friend);
    }

    public static Friend getFriend(String username) {
        return friendDatabase.get(username);
    }

    public static Friend getFriend(int index) {
        if (index < size()) {
            String url = friendDatabase.keySet().toArray(new String[size()])[index];
            return friendDatabase.get(url);
        }
        return null;
    }

    public static int size() {
        return friendDatabase.size();
    }

    public Friend(String name, String username, boolean isId, Bitmap profImg) {
        this.name = name;
        this.username = username;
        this.profImg = profImg;
        this.isId = isId;
    }

    public Friend(String username, String name) {
        this.username = username;
        this.isId = false;
        if (this.username.startsWith("/profile?id=")) {
            this.username = this.username.substring(12);
            this.isId = true;
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isId() {
        return isId;
    }

    public String getProfUrl() {
        if (isId) {
            return  "https://m.facebook.com/profile.php?id=" + username;
        } else {
            return "https://m.facebook.com/" + username;
        }
    }

    public static String getProfUrl(String username) {
        if (username.startsWith("/profile?id=")) {
            username = username.substring(12);
            return  "https://m.facebook.com/profile.php?id=" + username;
        } else {
            return "https://m.facebook.com/" + username;
        }
    }

    public Bitmap getProfImg() {
        return profImg;
    }

    public void setProfImg(Bitmap profImg) {
        this.profImg = profImg;
    }
}

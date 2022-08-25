package com.graphicdesigncoding.learnapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

public class User implements Comparable<User>{

    private static final String TAG = "User";

    private Bitmap userImg;
    private String userName;
    private String score;

    public User(Bitmap userImg, String userName,String score) {
        super();
        this.setUserImg(userImg);
        this.setUserName(userName);
        this.setScore(score);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getUserImg() {
        return userImg;
    }

    public void setUserImg(Bitmap userImg) {
        this.userImg = userImg;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public int compareTo(User user) {
        if (getScore() == null || user.getScore() == null) {
            return 0;
        }
            return Integer.compare(Integer.parseInt(getScore()), Integer.parseInt(user.getScore()));
    }
}

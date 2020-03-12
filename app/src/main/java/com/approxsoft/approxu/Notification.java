package com.approxsoft.approxu;

public class Notification {

    public String text, fullName, profileImage, date, time;

    public Notification()
    {

    }

    public Notification(String text, String fullName, String profileImage, String date, String time) {
        this.text = text;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.date = date;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

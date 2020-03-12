package com.approxsoft.approxu.Model;


public class MessageData {

    public String date, fullName , profileImage,time, message;

    public MessageData()
    {

    }

    public MessageData(String date, String fullName, String profileImage, String time, String message) {
        this.date = date;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.time = time;
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

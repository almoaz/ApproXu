package com.approxsoft.approxu;

public class Comments
{
    public String comment, date, time, fullName,profileImage;

    public Comments()
    {

    }

    public Comments(String comment, String date, String time, String fullName, String profileImage) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileimage) {
        this.profileImage = profileimage;
    }
}

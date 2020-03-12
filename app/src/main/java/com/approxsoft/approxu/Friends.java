package com.approxsoft.approxu;

public class Friends
{
    public String date, fullName , profileImage;

    public Friends()
    {

    }

    public Friends(String date, String fullName, String profileImage) {
        this.date = date;
        this.fullName = fullName;
        this.profileImage = profileImage;
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
}
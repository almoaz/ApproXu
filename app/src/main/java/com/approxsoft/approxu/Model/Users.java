package com.approxsoft.approxu.Model;

public class Users {
    String uid, fullName, profileImage;

    public Users()
    {

    }

    public Users(String uid, String fullName, String profileImage) {
        this.uid = uid;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

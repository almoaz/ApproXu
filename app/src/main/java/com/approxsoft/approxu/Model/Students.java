package com.approxsoft.approxu.Model;

public class Students {
    private String fullName, stdId, profileImage;

    public Students(){

    }

    public Students(String fullName, String stdId, String profileImage) {
        this.fullName = fullName;
        this.stdId = stdId;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}

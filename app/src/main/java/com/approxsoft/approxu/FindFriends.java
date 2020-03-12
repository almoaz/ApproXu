package com.approxsoft.approxu;

public class FindFriends
{
    public String profileImage, fullName, university;

    public FindFriends()
    {

    }

    public FindFriends(String profileImage, String fullName, String university) {
        this.profileImage = profileImage;
        this.fullName = fullName;
        this.university = university;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }
}
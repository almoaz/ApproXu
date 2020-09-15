package com.approxsoft.approxu;

public class Posts
{

    private String uid, time, date, postImage, description, profileImage,fullName, Image1, Image2,Image3,Image4;
    public Posts()
    {

    }

    public Posts(String uid, String time, String date, String postImage, String description, String profileImage, String fullName, String image1, String image2, String image3, String image4) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.description = description;
        this.profileImage = profileImage;
        this.fullName = fullName;
        Image1 = image1;
        Image2 = image2;
        Image3 = image3;
        Image4 = image4;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getImage4() {
        return Image4;
    }

    public void setImage4(String image4) {
        Image4 = image4;
    }
}
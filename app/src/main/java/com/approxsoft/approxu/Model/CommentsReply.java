package com.approxsoft.approxu.Model;

public class CommentsReply
{
    public String reply, date, time, fullName,profileImage;

    public CommentsReply()
    {

    }

    public CommentsReply(String reply, String date, String time, String fullName, String profileImage) {
        this.reply = reply;
        this.date = date;
        this.time = time;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
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

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}

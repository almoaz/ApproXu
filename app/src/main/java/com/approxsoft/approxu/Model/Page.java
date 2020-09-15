package com.approxsoft.approxu.Model;

public class Page {
    private String pageName, date,pageProfileImage;

    public  Page(){

    }

    public Page(String pageName, String date, String pageProfileImage) {
        this.pageName = pageName;
        this.date = date;
        this.pageProfileImage = pageProfileImage;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPageProfileImage() {
        return pageProfileImage;
    }

    public void setPageProfileImage(String pageProfileImage) {
        this.pageProfileImage = pageProfileImage;
    }
}

package com.approxsoft.approxu.Model;

public class Data {
    String subjectId;
    String subjectName;
    String subjectTopics;
    String subjectDate;

    public Data(){

    }

    public Data(String subjectId, String subjectName, String subjectTopics, String subjectDate) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectTopics = subjectTopics;
        this.subjectDate = subjectDate;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectTopics() {
        return subjectTopics;
    }

    public String getSubjectDate() {
        return subjectDate;
    }
}

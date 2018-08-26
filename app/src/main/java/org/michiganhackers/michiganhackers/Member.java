package org.michiganhackers.michiganhackers;

import java.util.List;

public class Member {
    private String name;
    private String bio;
    private List<String> teams;
    private String year;
    private String major;
    private String title;
    private String uid;
    private String photoUrl;

    public Member() {
    }

    public Member(String name, String uid, List<String> teams) {
        this.name = name;
        this.teams = teams;
        this.uid = uid;
    }

    public Member(String name, String uid, String bio, List<String> teams, String year, String major, String title) {
        this.name = name;
        this.uid = uid;
        this.bio = bio;
        this.teams = teams;
        this.year = year;
        this.major = major;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

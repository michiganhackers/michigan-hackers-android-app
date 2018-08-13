package org.michiganhackers.michiganhackers;

public class Member {
    private String name;
    private String bio;
    private String team;
    private String year;
    private String major;
    private String title;
    private String uid;
    private String photoUrl;

    public Member() {
    }

    public Member(String name, String uid, String team) {
        this.name = name;
        this.team = team;
        this.uid = uid;
    }

    public Member(String name, String uid, String bio, String team, String year, String major, String title) {
        this.name = name;
        this.uid = uid;
        this.bio = bio;
        this.team = team;
        this.year = year;
        this.major = major;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getTeam() {
        return team;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(String team) {
        this.team = team;
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

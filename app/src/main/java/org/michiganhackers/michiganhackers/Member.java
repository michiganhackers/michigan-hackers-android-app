package org.michiganhackers.michiganhackers;

public class Member {
    private String name;
    private String bio;
    private String team;
    private int year;
    private String major;

    public Member(String name, String team) {
        this.name = name;
        this.team = team;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

}

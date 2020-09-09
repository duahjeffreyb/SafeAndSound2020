package com.example.elizabethwhitebaker.safeandsound;

class Initiator {
    private int initiatorID;
    private String firstName;
    private String lastName;
    private String username;
    private String picturePath;
    private String phoneNumber;
    private String password;

    Initiator() {}
    Initiator(String first, String last, String user, String path, String phone, String pass) {
        firstName = first;
        lastName = last;
        username = user;
        picturePath = path;
        phoneNumber = phone;
        password = pass;
    }

    int getInitiatorID() { return initiatorID; }
    void setInitiatorID(int initiatorID) { this.initiatorID = initiatorID; }
    String getFirstName() { return firstName; }
    void setFirstName(String firstName) { this.firstName = firstName; }
    void setLastName(String lastName) {
        this.lastName = lastName;
    }
    void setUsername(String username) {
        this.username = username;
    }
    void setPicturePath(String picturePath) { this.picturePath = picturePath; }
    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    String getPassword() { return password; }
    void setPassword(String password) {
        this.password = password;
    }
    String getLastName() { return lastName; }
    String getUsername() { return username; }
    String getPicturePath() { return picturePath; }
    String getPhoneNumber() { return phoneNumber; }

    @Override
    public String toString() {
        return "Initiator{" +
                "initiatorID=" + initiatorID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", picturePath=" + picturePath + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

package com.example.elizabethwhitebaker.safeandsound;

class Member {
    private int memberID;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String replyStatus;
    private String response;

    Member() {}
    Member(String first, String last, String phone) {
        firstName = first;
        lastName = last;
        phoneNumber = phone;
    }

    int getMemberID() {
        return memberID;
    }
    void setMemberID(int memberID) { this.memberID = memberID; }
    String getFirstName() {
        return firstName;
    }
    String getLastName() {
        return lastName;
    }
    String getPhoneNumber() {
        return phoneNumber;
    }
    String getReplyStatus() {
        return replyStatus;
    }
    String getResponse() {
        return response;
    }
    void setFirstName(String firstName) { this.firstName = firstName; }
    void setLastName(String lastName) { this.lastName = lastName; }
    void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    void setReplyStatus(String replyStatus) { this.replyStatus = replyStatus; }
    void setResponse(String response) { this.response = response; }

    @Override
    public String toString() {
        return "Member{" +
                "memberID=" + memberID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", replyStatus='" + replyStatus + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
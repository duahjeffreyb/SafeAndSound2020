package com.example.elizabethwhitebaker.safeandsound;

class Group {
    private int groupID;
    private String groupName;

    Group() {}
    Group(String name) {
        groupName = name;
    }

    int getGroupID() {
        return groupID;
    }
    void setGroupID(int groupID) { this.groupID = groupID; }
    String getGroupName() { return groupName; }
    void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupID=" + groupID +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
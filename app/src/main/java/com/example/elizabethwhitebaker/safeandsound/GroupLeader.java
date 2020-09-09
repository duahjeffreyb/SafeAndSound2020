package com.example.elizabethwhitebaker.safeandsound;

class GroupLeader {
    private int groupLeaderID;
    private int initiatorID;
    private int groupID;

    GroupLeader() {}
    GroupLeader(int initiatorID, int groupID) {
        this.initiatorID = initiatorID;
        this.groupID = groupID;
    }

    int getGroupLeaderID() { return groupLeaderID; }
    void setGroupLeaderID(int groupLeaderID) { this.groupLeaderID = groupLeaderID; }
    int getInitiatorID() { return initiatorID; }
    int getGroupID() {
        return groupID;
    }

    @Override
    public String toString() {
        return "GroupLeader{" +
                "groupLeaderID=" + groupLeaderID +
                ", initiatorID=" + initiatorID +
                ", groupID=" + groupID +
                '}';
    }
}

package com.example.elizabethwhitebaker.safeandsound;

class EventGroup {
    private int eventGroupID;
    private int eventID;
    private int groupID;

    EventGroup() {}
    EventGroup(int eventID, int groupID) {
        this.eventID = eventID;
        this.groupID = groupID;
    }

    int getEventGroupID() {return eventGroupID;}
    void setEventGroupID(int eventGroupID) {this.eventGroupID = eventGroupID;}
    int getEventID() {return eventID;}
    int getGroupID() {return groupID;}
}

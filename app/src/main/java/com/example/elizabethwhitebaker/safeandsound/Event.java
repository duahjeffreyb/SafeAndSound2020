package com.example.elizabethwhitebaker.safeandsound;

class Event {
    private int eventID;
    private String eventName;
    private String eventDescription;

    Event() {}
    Event(String name, String desc) {
        eventName = name;
        eventDescription = desc;
    }

    int getEventID() {return eventID;}
    void setEventID(int eventID) {this.eventID = eventID;}
    String getEventName() {return eventName;}
    void setEventName(String eventName) {this.eventName = eventName;}
    String getEventDescription() {return eventDescription;}
    void setEventDescription(String eventDescription) {this.eventDescription = eventDescription;}
}

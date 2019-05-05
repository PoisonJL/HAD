package com.team7.hadcontrolpanel;

public class CalEvent {
    //declare varailbes
    private String eventID;
    private String title;
    private String date;
    private String event;

    //construcot
    public CalEvent() {
        this.eventID = " ";
        this.title = " ";
        this.date = " ";
        this.event = " ";
    }

    //Constructor
    public CalEvent(String eventID, String title, String date, String event) {
        this.eventID = eventID;
        this.title = title;
        this.date = date;
        this.event = event;
    }

    //Constructor
    public CalEvent(CalEvent orig) {
        this.eventID = orig.eventID;
        this.title = orig.title;
        this.date = orig.date;
        this.event = orig.event;
    }
    //getter methods
    public String getEventID()
    {
        return eventID;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDate()
    {
        return date;
    }
    public String getEvent()
    {
        return event;
    }
}
package com.team7.hadcontrolpanel;

public class CalTask {
    private String taskID;
    private String taskname;
    private String titlename;
    private String dayname;

    public CalTask(){
        this.taskID = "";
        this.taskname = "";
        this.titlename = "";
        this.dayname = "";

    }

    public CalTask(String taskID, String taskname, String titlename, String dayname) {
        this.taskID = taskID;
        this.taskname = taskname;
        this.titlename = titlename;
        this.dayname = dayname;

    }

    public CalTask(CalTask orig) {
        this.taskID = orig.taskID;
        this.taskname = orig.taskname;
        this.titlename = orig.titlename;
        this.dayname = orig.dayname;
    }

    public String getTaskID()
    {
        return taskID;
    }
    public String gettaskname()
    {
        return taskname;
    }
    public String gettitlename()
    {
        return titlename;
    }
    public String getdayname()
    {
        return dayname;
    }



}










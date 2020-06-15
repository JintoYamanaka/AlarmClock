package com.example.alarmclock.listcomponent;

public class ListItem {

    private int alarmID = -1;
    private String musicName = null;
    private String time = null;

    public String getMusicName() {
        return musicName;
    }

    public String getTime() {
        return time;
    }

    public String getHour(){
        return getTime().substring(0,2);
    }

    public String getMinute(){
        return getTime().substring(3,5);
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmID() {
        return alarmID;
    }
}

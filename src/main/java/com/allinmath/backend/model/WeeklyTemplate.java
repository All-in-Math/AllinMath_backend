package com.allinmath.backend.model;

import java.util.List;

public class WeeklyTemplate {
    private List<TimeSlot> monday;
    private List<TimeSlot> tuesday;
    private List<TimeSlot> wednesday;
    private List<TimeSlot> thursday;
    private List<TimeSlot> friday;
    private List<TimeSlot> saturday;
    private List<TimeSlot> sunday;

    public WeeklyTemplate() {}

    public List<TimeSlot> getMonday() {
        return monday;
    }

    public void setMonday(List<TimeSlot> monday) {
        this.monday = monday;
    }

    public List<TimeSlot> getTuesday() {
        return tuesday;
    }

    public void setTuesday(List<TimeSlot> tuesday) {
        this.tuesday = tuesday;
    }

    public List<TimeSlot> getWednesday() {
        return wednesday;
    }

    public void setWednesday(List<TimeSlot> wednesday) {
        this.wednesday = wednesday;
    }

    public List<TimeSlot> getThursday() {
        return thursday;
    }

    public void setThursday(List<TimeSlot> thursday) {
        this.thursday = thursday;
    }

    public List<TimeSlot> getFriday() {
        return friday;
    }

    public void setFriday(List<TimeSlot> friday) {
        this.friday = friday;
    }

    public List<TimeSlot> getSaturday() {
        return saturday;
    }

    public void setSaturday(List<TimeSlot> saturday) {
        this.saturday = saturday;
    }

    public List<TimeSlot> getSunday() {
        return sunday;
    }

    public void setSunday(List<TimeSlot> sunday) {
        this.sunday = sunday;
    }
}

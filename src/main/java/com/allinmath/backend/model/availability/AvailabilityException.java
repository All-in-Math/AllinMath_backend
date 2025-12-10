package com.allinmath.backend.model.availability;

import java.util.Date;
import java.util.List;

public class AvailabilityException {
    private Date date;
    private boolean fullDayBlocked;
    private List<TimeSlot> slots;

    public AvailabilityException() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFullDayBlocked() {
        return fullDayBlocked;
    }

    public void setFullDayBlocked(boolean fullDayBlocked) {
        this.fullDayBlocked = fullDayBlocked;
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<TimeSlot> slots) {
        this.slots = slots;
    }
}

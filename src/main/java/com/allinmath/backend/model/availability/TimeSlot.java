package com.allinmath.backend.model.availability;

import java.util.Date;

public class TimeSlot {
    private Date startAt;
    private Date endAt;

    public TimeSlot() {}

    public TimeSlot(Date startAt, Date endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }
}

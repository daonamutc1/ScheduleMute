package com.daonam.schedulemute.data;

import java.util.List;

public class ScheduleOfDay {
    int dayOfWeek;
    List<Action> actions;

    public ScheduleOfDay(int dayOfWeek, List<Action> actions) {
        this.dayOfWeek = dayOfWeek;
        this.actions = actions;
    }
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public List<Action> getActions() {
        return actions;
    }
}

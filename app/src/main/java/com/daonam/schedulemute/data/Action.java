package com.daonam.schedulemute.data;

public class Action {
    boolean isMute;
    int start;
    //end sẽ lấy ở ở start của action tiếp theo

    public Action(boolean isMute, int start) {
        this.isMute = isMute;
        this.start = start;
    }

    public boolean isMute() {
        return isMute;
    }

    public int getStart() {
        return start;
    }
}

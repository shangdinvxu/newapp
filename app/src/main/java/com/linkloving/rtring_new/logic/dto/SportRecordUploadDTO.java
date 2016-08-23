package com.linkloving.rtring_new.logic.dto;

import com.linkloving.band.dto.SportRecord;

import java.util.ArrayList;

public class SportRecordUploadDTO {
    private String device_id, utc_time, offset;
    private Integer user_id;
    private ArrayList<SportRecord> list;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUtc_time() {
        return utc_time;
    }

    public void setUtc_time(String utc_time) {
        this.utc_time = utc_time;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public ArrayList<SportRecord> getList() {
        return list;
    }

    public void setList(ArrayList<SportRecord> list) {
        this.list = list;
    }


}

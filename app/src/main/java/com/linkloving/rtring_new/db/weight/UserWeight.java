package com.linkloving.rtring_new.db.weight;

/**
 * Created by zkx on 2016/5/4.
 */
public class UserWeight {
    private String recordId;
    private String userId;
    private String time;
    private String weight;

    public UserWeight(String time, String userId, String weight) {
        this.time = time;
        this.userId = userId;
        this.weight = weight;
    }

    public UserWeight() {
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "UserWeight{" +
                "recordId='" + recordId + '\'' +
                ", userId='" + userId + '\'' +
                ", time='" + time + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}

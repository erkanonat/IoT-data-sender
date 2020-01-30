package com.iot.tb.datasender.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.util.Date;

@Table("average_speed_traffic")
public class AverageSpeedTrafficData implements Serializable {


    @PrimaryKeyColumn(name = "ptsid",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String ptsId;

    @PrimaryKeyColumn(name = "recordDate",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private String recordDate;

    @PrimaryKeyColumn(name = "averagespeed",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
    private double averageSpeed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
    @Column(value = "timestamp")
    private Date timeStamp;

    @Override
    public String toString() {
        return "AverageSpeedTrafficData [ptsId=" + ptsId + ", recordDate=" + recordDate + ", averageSpeed=" + averageSpeed
                + ", timeStamp=" + timeStamp + "]";
    }

    public String getPtsId() {
        return ptsId;
    }

    public void setPtsId(String ptsId) {
        this.ptsId = ptsId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

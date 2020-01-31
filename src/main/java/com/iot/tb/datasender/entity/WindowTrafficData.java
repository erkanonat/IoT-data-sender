package com.iot.tb.datasender.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.util.Date;



@Table("window_traffic")
public class WindowTrafficData implements Serializable{
    @PrimaryKeyColumn(name = "ptsid",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String ptsId;
    @PrimaryKeyColumn(name = "recordDate",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private String recordDate;
    @PrimaryKeyColumn(name = "vehicletype",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
    private String vehicleType;
    @Column(value = "totalcount")
    private long totalCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
    @Column(value = "timestamp")
    private Date timeStamp;

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
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public Date getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    @Override
    public String toString() {
        return "TrafficData [ptsId=" + ptsId + ", vehicleType=" + vehicleType + ", totalCount=" + totalCount
                + ", timeStamp=" + timeStamp + "]";
    }


}

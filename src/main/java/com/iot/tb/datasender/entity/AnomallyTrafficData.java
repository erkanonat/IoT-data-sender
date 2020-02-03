package com.iot.tb.datasender.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;

@Table("anomally_traffic_data")
public class AnomallyTrafficData {


    @PrimaryKeyColumn(name = "platenumber",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String plateNumber;

    @PrimaryKeyColumn(name = "recorddate",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private String recordDate;

    @PrimaryKeyColumn(name = "duplicates",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
    private String duplicates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
    @Column(value = "timestamp")
    private Date timeStamp;

    @Column(value = "totalcount")
    private long totalCount;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(String duplicates) {
        this.duplicates = duplicates;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "TrafficData [plateNumber=" + plateNumber + ", recordDate=" + recordDate +  ", duplicates=" + duplicates + ", totalCount=" + totalCount
                + ", timeStamp=" + timeStamp + "]";
    }
}

package com.iot.tb.datasender.entity;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.annotations.SerializedName;
import com.havelsan.thingsboardclient.model.Alarm;
import com.havelsan.thingsboardclient.model.AlarmId;
import com.havelsan.thingsboardclient.model.EntityId;
import com.havelsan.thingsboardclient.model.TenantId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {

    @SerializedName("ackTs")
    private Long ackTs = null;
    @SerializedName("clearTs")
    private Long clearTs = null;
    @SerializedName("createdTime")
    private Long createdTime = null;
    @SerializedName("details")
    private JsonNode details = null;
    @SerializedName("endTs")
    private Long endTs = null;
    @SerializedName("id")
    private AlarmId id = null;
    @SerializedName("name")
    private String name = null;
    @SerializedName("originator")
    private EntityId originator = null;
    @SerializedName("propagate")
    private Boolean propagate = null;
    @SerializedName("severity")
    private Alarm.SeverityEnum severity = null;
    @SerializedName("startTs")
    private Long startTs = null;
    @SerializedName("status")
    private Alarm.StatusEnum status = null;
    @SerializedName("tenantId")
    private TenantId tenantId = null;
    @SerializedName("type")
    private String type = null;


    public static Alarm valueOf(AlarmDto dto){

        return Alarm.builder().id(dto.getId())
                .status(dto.status)
                .severity(dto.getSeverity())
                .name(dto.getName())
                .type(dto.getType())
                .originator(dto.getOriginator())
                .propagate(dto.propagate)
                .details(dto.details.toString())
                .ackTs(dto.ackTs)
                .clearTs(dto.clearTs)
                .clearTs(dto.clearTs)
                .createdTime(dto.createdTime)
                .tenantId(dto.tenantId)
                .startTs(dto.startTs)
                .endTs(dto.endTs)
                .build();

    }

}



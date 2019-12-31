package com.iot.tb.datasender.tbclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;


@Component
public class SampleMqttClient {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final String deviceToken;
    private final String deviceName;
    private final String clientId;
    private final MqttClientPersistence persistence;
    private final MqttAsyncClient client;



    public SampleMqttClient(String uri, String deviceName, String deviceToken) throws Exception {
        this.clientId = MqttAsyncClient.generateClientId();
        this.deviceToken = deviceToken;
        this.deviceName = deviceName;
        this.persistence = new MemoryPersistence();
        this.client = new MqttAsyncClient(uri, clientId, persistence);
    }

    public boolean connect() throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(deviceToken);
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
//                    log.info("[{}] connected to Thingsboard!", deviceName);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable e) {
//                    log.error("[{}] failed to connect to Thingsboard!", deviceName, e);
                }
            }).waitForCompletion();
        } catch (MqttException e) {
//            log.error("Failed to connect to the server", e);
        }
        return client.isConnected();
    }

    public void disconnect() throws Exception {
        client.disconnect().waitForCompletion();
    }

    public void publishAttributes(JsonNode data) throws Exception {
        publish("v1/devices/me/attributes", data, true);
    }

    public void publishTelemetry(JsonNode data) throws Exception {
        publish("v1/devices/me/telemetry", data, false);
    }

    private void publish(String topic, JsonNode data, boolean sync) throws Exception {
        MqttMessage msg = new MqttMessage(MAPPER.writeValueAsString(data).getBytes(StandardCharsets.UTF_8));
        IMqttDeliveryToken deliveryToken = client.publish(topic, msg, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
//                log.trace("Data updated!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                log.error("[{}] Data update failed!", deviceName, exception);
            }
        });
        if (sync) {
            deliveryToken.waitForCompletion();
        }
    }

    private static double randomWalk(double location) {

        double r = Math.random();
        double range = 0.00030d;

//        if(r<0.50){
//            return location-range;
//        }else {
//            return location+range;
//        }
        return  location+range;
    }

    public static void scenario_1()  throws IOException, Exception {
        SampleMqttClient client = new SampleMqttClient("tcp://127.0.0.1","PTS_1","lM8fc3ty9xIkwuiZsP1G");

        client.connect();
        System.out.println("connected");
//        double lat = 39.907360;
//        double lon = 32.753005;
        double tempInit = 81.454545d;

        for(int i =0;i< 1000 ; i++) {

            double temp  = Math.random();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode nodeTelemetry = mapper.createObjectNode();
            ObjectNode nodeAttribute = mapper.createObjectNode();

            Random rand = new Random();
            int LargeTruckCount = rand.nextInt(120 - 80) + 80;// random speed between 80 to 120
            int SmallTruckCount = rand.nextInt(60 - 40) + 40;// random speed between 80 to 120
            int TaxiCount = rand.nextInt(200 - 150) + 150;// random speed between 80 to 120
            int PrivateCar = rand.nextInt(90 - 60) + 60;// random speed between 80 to 120

            nodeTelemetry.put("averageSpeed", String.valueOf( (temp<0.5) ? tempInit+temp : tempInit-temp  ));
            nodeTelemetry.put("LargeTruckCount", LargeTruckCount);
            nodeTelemetry.put("SmallTruckCount", SmallTruckCount);
            nodeTelemetry.put("TaxiCount", TaxiCount);
            nodeTelemetry.put("PrivateCar", PrivateCar);
//
//            nodeTelemetry.put("lat", lat);
//            lon = randomWalk(lon);
//            nodeTelemetry .put("lon", lon);

            System.out.println("publish message : " + nodeTelemetry.toString());
            client.publishTelemetry(nodeTelemetry);


            Thread.sleep(3000);
        }

        client.disconnect();

    }

//    public static void main(String[] args) throws  IOException, Exception {
//
//        scenario_1();
//    }



    /* getter setter*/

    public static ObjectMapper getMAPPER() {
        return MAPPER;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getClientId() {
        return clientId;
    }

    public MqttClientPersistence getPersistence() {
        return persistence;
    }

    public MqttAsyncClient getClient() {
        return client;
    }
}

package com.iot.tb.datasender.service;





import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
public class SampleMqttClient {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Getter
    private final String deviceToken;
    @Getter
    private final String deviceName;
    @Getter
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
                    log.info("[{}] connected to Thingsboard!", deviceName);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable e) {
                    log.error("[{}] failed to connect to Thingsboard!", deviceName, e);
                }
            }).waitForCompletion();
        } catch (MqttException e) {
            log.error("Failed to connect to the server", e);
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
                log.trace("Data updated!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                log.error("[{}] Data update failed!", deviceName, exception);
            }
        });
        if (sync) {
            deliveryToken.waitForCompletion();
        }
    }

    private static String generateRandomPlateNumber() {

        Random rand = new Random();

        List<String> cities = new ArrayList<String>();

        for(int i=1;i<=80;i++)
            cities.add( (String.valueOf(i).length()<2) ? "0"+String.valueOf(i) : String.valueOf(i) );
        String cityCode = cities.get(rand.nextInt(80));

        List<String> strList = Arrays.asList(new String[]{"A","B","C","D","E","F","G","H","K","N"});

        String strCode1 = strList.get(rand.nextInt(10));
        String strCode2 = strList.get(rand.nextInt(10));

        int numCode = rand.nextInt(999-100)+100;


        return cityCode+"_"+strCode1+strCode2+"_"+String.valueOf(numCode);
    }
    public static void main(String[] args) throws  IOException, Exception {

        List<String> routeList = Arrays.asList(new String[]{"PTS_1", "PTS_2", "PTS_3", "PTS_4", "PTS_5"});
        List<String> colorList = Arrays.asList(new String[]{"white", "black", "red", "blue", "grey", "yellow"});
        List<String> vehicleTypeList = Arrays.asList(new String[]{"Large Truck", "Small Truck", "Private Car", "Bus", "Taxi"});

        Random rand = new Random();

        SampleMqttClient client1 = new SampleMqttClient("tcp://0.0.0.0:1883", "PTS_1", "u770miwXk5WJA3zasFat");
        SampleMqttClient client2 = new SampleMqttClient("tcp://0.0.0.0:1883", "PTS_2", "Mb9XuKtfeNMkJSMS1Y6u");
        SampleMqttClient client3 = new SampleMqttClient("tcp://0.0.0.0:1883", "PTS_3", "vW0eLrTS7ia1UnL9B5bF");
        SampleMqttClient client4 = new SampleMqttClient("tcp://0.0.0.0:1883", "PTS_4", "9y5Cp1LUQQFJyLrR24k7");
        SampleMqttClient client5 = new SampleMqttClient("tcp://0.0.0.0:1883", "PTS_5", "UTWdMASNEbZIs3JY4DR8");

        Map<Integer, SampleMqttClient> clientMap = new HashMap<>();
        clientMap.put(Integer.valueOf(client1.deviceName.split("_")[1]), client1);
        clientMap.put(Integer.valueOf(client2.deviceName.split("_")[1]), client2);
        clientMap.put(Integer.valueOf(client3.deviceName.split("_")[1]), client3);
        clientMap.put(Integer.valueOf(client4.deviceName.split("_")[1]), client4);
        clientMap.put(Integer.valueOf(client5.deviceName.split("_")[1]), client5);

        client1.connect();
        client2.connect();
        client3.connect();
        client4.connect();
        client5.connect();

        try {

            ObjectMapper mapper = new ObjectMapper();

            List<ObjectNode> iotEvents = new ArrayList<>();

            for(int j=0;j<1000;j++) {

                ObjectNode iotData =  mapper.createObjectNode();

                String ptsId = routeList.get(rand.nextInt(5));
                String vehicleType = vehicleTypeList.get(rand.nextInt(5));
                String plateNumber = generateRandomPlateNumber();
                String color = colorList.get(rand.nextInt(6));
                Date now = new Date();
                String pattern = "yyyy-MM-dd HH:mm:ss";
                DateFormat df = new SimpleDateFormat(pattern);
                String timeStampAsFormat = df.format(now);

                double speed = rand.nextInt(120 - 50) + 50;// random speed between 50 to 120

                iotData.put("ptsId", ptsId);
                iotData.put("plateNumber", plateNumber);
                iotData.put("color", color);
                iotData.put("speed", speed);
                iotData.put("vehicleType", vehicleType);
                iotData.put("timestamp", timeStampAsFormat);

                iotEvents.add(iotData);

            }

            Collections.shuffle(iotEvents);

            for(ObjectNode node : iotEvents){

                Integer ptsNo = Integer.valueOf(node.get("ptsId").toString().split("_")[1].substring(0,1));

                SampleMqttClient client = clientMap.get(ptsNo);

                if(client!=null){
                    System.out.println(node.toString());
                    client.publishTelemetry(node);
                }

                Thread.sleep(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client1.disconnect();
            client2.disconnect();
            client3.disconnect();
            client4.disconnect();
            client5.disconnect();
        }

    }

}

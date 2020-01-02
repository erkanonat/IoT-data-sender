package com.iot.tb.datasender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iot.tb.datasender.entity.TotalTrafficData;
import com.iot.tb.datasender.repository.TotalTrafficDataRepository;
import com.iot.tb.datasender.tbclient.SampleMqttClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrafficDataService {

    private static final Logger logger = Logger.getLogger(TrafficDataService.class);

    @Autowired
    private TotalTrafficDataRepository totalRepository;

//    @Autowired
//    private WindowTrafficDataRepository windowRepository;

    HashMap<String,SampleMqttClient> clients = new HashMap<>();

    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public TrafficDataService(){
            try {
                SampleMqttClient client1 = new SampleMqttClient("tcp://127.0.0.1","PTS_1","eBFlkIhn5cmRb2fFqyZR");
                SampleMqttClient client2 = new SampleMqttClient("tcp://127.0.0.1","PTS_2","lHeWs9HbA8rX2pfVupy3");
                SampleMqttClient client3 = new SampleMqttClient("tcp://127.0.0.1","PTS_3","jZbCptu4yezuD97D6Y1w");
                SampleMqttClient client4 = new SampleMqttClient("tcp://127.0.0.1","PTS_4","QjQAIrWd7eFzvzvkPENM");
                SampleMqttClient client5 = new SampleMqttClient("tcp://127.0.0.1","PTS_5","EH9EmVKeJWIGWJdAxUYU");

                clients.put("PTS_1", client1);
                clients.put("PTS_2", client2);
                clients.put("PTS_3", client3);
                clients.put("PTS_4", client4);
                clients.put("PTS_5", client5);

            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
    }

    //Method sends traffic data message in every 5 seconds.
    @Scheduled(fixedRate = 5000)
    public void trigger() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            // send totalTrafficData
            List<TotalTrafficData> totalTrafficList = new ArrayList<TotalTrafficData>();
            HashMap<String, ObjectNode> telemetryMap1 = new HashMap<>();
            totalRepository.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> totalTrafficList.add(e));


            for(TotalTrafficData t : totalTrafficList){
                if(telemetryMap1.get(t.getPtsId())==null) {
                    ObjectNode node = mapper.createObjectNode();
                    telemetryMap1.put(t.getPtsId(), node);
                }
            }
            for(TotalTrafficData t : totalTrafficList){
                    ObjectNode node = telemetryMap1.get(t.getPtsId());
                    node.put(this.getCountType(t.getVehicleType()),t.getTotalCount());

            }
            for(Map.Entry<String,SampleMqttClient> client : clients.entrySet()){
                client.getValue().connect();
                client.getValue().publishTelemetry(telemetryMap1.get(client.getKey()));
            }


            // send windowTrafficData
//            List<WindowTrafficData> windowTrafficList = new ArrayList<WindowTrafficData>();

            // send anomaly Detection Data

            // send average speed data.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, SampleMqttClient> getClients() {
        return clients;
    }

    public void setClients(HashMap<String, SampleMqttClient> clients) {
        this.clients = clients;
    }

    private String getCountType(String vehicleType){
        if("Large Truck".equals(vehicleType)){
             return "LargeTruckCount";
        }else if ("Bus".equals(vehicleType)){
            return "BusCount";
        }else if ("Taxi".equals(vehicleType)){
            return "TaxiCount";
        }else if ("Private Car".equals(vehicleType)){
            return "PrivateCarCount";
        }else if ("Small Truck".equals(vehicleType)){
            return "SmallTruckCount";
        }else{
            return "SedanCount";
        }
    }

}

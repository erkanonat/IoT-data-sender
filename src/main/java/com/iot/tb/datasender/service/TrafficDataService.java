package com.iot.tb.datasender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iot.tb.datasender.entity.TotalTrafficData;
import com.iot.tb.datasender.entity.WindowTrafficData;
import com.iot.tb.datasender.repository.TotalTrafficDataRepository;
import com.iot.tb.datasender.repository.WindowTrafficDataRepository;
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
    private TotalTrafficDataRepository totalTrafficRepository;

    @Autowired
    private WindowTrafficDataRepository windowTrafficRepository;

    HashMap<String,SampleMqttClient> clients = new HashMap<>();

    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public TrafficDataService(){
            try {
                SampleMqttClient client1 = new SampleMqttClient("tcp://127.0.0.1","PTS_1","u770miwXk5WJA3zasFat");
                SampleMqttClient client2 = new SampleMqttClient("tcp://127.0.0.1","PTS_2","Mb9XuKtfeNMkJSMS1Y6u");
                SampleMqttClient client3 = new SampleMqttClient("tcp://127.0.0.1","PTS_3","vW0eLrTS7ia1UnL9B5bF");
                SampleMqttClient client4 = new SampleMqttClient("tcp://127.0.0.1","PTS_4","9y5Cp1LUQQFJyLrR24k7");
                SampleMqttClient client5 = new SampleMqttClient("tcp://127.0.0.1","PTS_5","UTWdMASNEbZIs3JY4DR8");

                clients.put("PTS_1", client1);
                clients.put("PTS_2", client2);
                clients.put("PTS_3", client3);
                clients.put("PTS_4", client4);
                clients.put("PTS_5", client5);


                client1.connect();
                client2.connect();
                client3.connect();
                client4.connect();
                client5.connect();


                for(Map.Entry<String,SampleMqttClient> clt : clients.entrySet()) {
                    clt.getValue().connect();
                }

            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
    }

    //Method sends traffic data message in every 5 seconds.
    @Scheduled(fixedRate = 5000)
    public void trigger() throws  Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();

            // send totalTrafficData
            List<TotalTrafficData> totalTrafficList = new ArrayList<TotalTrafficData>();
            List<WindowTrafficData> windowTrafficList = new ArrayList<WindowTrafficData>();

            HashMap<String, ObjectNode> attributeMap = new HashMap<>();
            totalTrafficRepository.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> totalTrafficList.add(e));
            windowTrafficRepository.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> windowTrafficList.add(e));

            System.out.println("create attribute json nodes");
            for(TotalTrafficData t : totalTrafficList) {
                if(attributeMap.get(t.getPtsId())==null) {
                    ObjectNode node = mapper.createObjectNode();
                    attributeMap.put(t.getPtsId(), node);
                }
            }

            for(TotalTrafficData t : totalTrafficList) {
                ObjectNode node = attributeMap.get(t.getPtsId());
                node.put("t:"+this.getCountType(t.getVehicleType()),t.getTotalCount());
            }
            for(WindowTrafficData w : windowTrafficList) {
                ObjectNode node = attributeMap.get(w.getRouteId());
                node.put("w:"+this.getCountType(w.getVehicleType()),w.getTotalCount());
            }

            for(Map.Entry<String,SampleMqttClient> client : clients.entrySet()) {
                client.getValue().publishAttributes(attributeMap.get(client.getKey()));
            }



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

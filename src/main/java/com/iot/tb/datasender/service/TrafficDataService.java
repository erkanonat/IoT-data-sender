package com.iot.tb.datasender.service;

import com.iot.tb.datasender.entity.TotalTrafficData;
import com.iot.tb.datasender.entity.WindowTrafficData;
import com.iot.tb.datasender.repository.TotalTrafficDataRepository;
import com.iot.tb.datasender.repository.WindowTrafficDataRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TrafficDataService {


    private static final Logger logger = Logger.getLogger(TrafficDataService.class);



    @Autowired
    private TotalTrafficDataRepository totalRepository;

    @Autowired
    private WindowTrafficDataRepository windowRepository;



    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //Method sends traffic data message in every 5 seconds.
    @Scheduled(fixedRate = 5000)
    public void trigger() {
        List<TotalTrafficData> totalTrafficList = new ArrayList<TotalTrafficData>();
        List<WindowTrafficData> windowTrafficList = new ArrayList<WindowTrafficData>();

        //Call dao methods
        totalRepository.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> totalTrafficList.add(e));
        //windowRepository.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> windowTrafficList.add(e));


        //logger.info("Sending to UI "+response);
        //send to ui

        //this.template.convertAndSend("/topic/trafficData", response);


    }

}

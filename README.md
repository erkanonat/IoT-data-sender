# iot-data-sender
This project is spring boot application which, 
reads spark-processor results from cassandra db 
  and send the results to the thignsboard server as device(PTS) telemetry data in order to visualize the results with using thingsboard built-in widgets.
  There are 4 tables in cassandra namely; total_traffic, window_traffic, anomally_traffic_data, average_speed_traffi tables. 
  these tables stores spark's final results which produced by analyzing iot events coming from kafka broker. 


cassandra iot-traffic-key-space tables:

![Alt text](/src/main/resources/traffic_key_space_tables.png?raw=true "final dashboard")  
  

thingsboard PTS-DEMO-DASHBOARD screenshot : 
    thingsboard's built-in widgets are very usefull for visualizing the results and showing graphs and charts . 
    With thingsboard , we can create customize dashboards with rich widget's of it . 
![Alt text](/src/main/resources/dashboard-with-alarm.png?raw=true "final dashboard")

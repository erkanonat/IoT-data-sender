package com.iot.tb.datasender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.havelsan.thingsboardclient.model.Device;
import com.iot.tb.datasender.entity.TbDeviceNode;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThingsboardHttpService {


    @Value("${tb.restclient.endpoint}")
    private String EGYS_REST_ENDPOINT;

    @Value("${tb.restclient.username}")
    private String USERNAME;

    @Value("${tb.restclient.password}")
    private String PASSWORD;

    private RestTemplate restTemplate;
    private String token;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ThingsboardHttpService.class);

    public ThingsboardHttpService() {
        logger.info("constructor called");
        ClientHttpRequestFactory requestFactory = new
                HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        restTemplate = new RestTemplate(requestFactory);
    }

    @PostConstruct
    public void postConstruct() {
        // set token
        this.loginRestTemplate();
    }

    private void refreshToken() {
        // update token
        loginRestTemplate();
    }

    private void loginRestTemplate() {

        try {

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", USERNAME);
            loginRequest.put("password", PASSWORD);
            ResponseEntity<JsonNode> tokenInfo = restTemplate.postForEntity(EGYS_REST_ENDPOINT + "/api/auth/login",
                    loginRequest, JsonNode.class);
            this.token = tokenInfo.getBody().get("token").asText();

        } catch (ResourceAccessException rae) {
            logger.error("egys rest api , connection error..............: " + rae.getMessage());
        }
    }

    private HttpHeaders getHttpHeaders() {

        if (this.token == null || this.token.length() == 0) {
            loginRestTemplate();
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Authorization", "Bearer " + token);
        return requestHeaders;
    }

    public List<TbDeviceNode> getDevicesByType(String type) {

        List<TbDeviceNode> deviceList = new ArrayList<>();
        try {

            HttpHeaders requestHeaders = getHttpHeaders();
            if (requestHeaders == null) {
                System.out.println("not connected to egys....authentication failed.......");
            }

            String urlStr = EGYS_REST_ENDPOINT+"api/devices/find?limit={limit}&type={type}";

            HttpEntity<Device> httpEntity = new HttpEntity<Device>(requestHeaders);

            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(urlStr, HttpMethod.GET, httpEntity
                    , JsonNode.class, "100","PTS");

            JsonNode result = responseEntity.getBody();

            if(result!=null && result.get("data").isArray()){
                for(JsonNode node : result.get("data")) {
                    TbDeviceNode pts = new TbDeviceNode(node.get("name").toString(),null ,node.get("id").get("id").toString());
                    deviceList.add(pts);
                }
            }

            return deviceList;
        } catch (HttpClientErrorException e) {

            e.printStackTrace();
            logger.error(e.getMessage());
            return null;

        }
    }

    public String postTelemetry(ObjectNode data ,String deviceId ) {

        try {

            HttpHeaders requestHeaders = getHttpHeaders();
            if (requestHeaders == null) {
                logger.warn("auth header couldn't found...");
            }

            String POST_TELEMETRY_URL = "/api/plugins/telemetry/DEVICE/${DEVICE_ID}/timeseries/SERVER_SCOPE";

            String uriPostTelemetry = (EGYS_REST_ENDPOINT + POST_TELEMETRY_URL ).replace("${DEVICE_ID}", deviceId);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.convertValue(data, JsonNode.class);
            HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(node, requestHeaders);

            ResponseEntity responseEntity = restTemplate.postForEntity(uriPostTelemetry, httpEntity, ResponseEntity.class);

            return responseEntity.getStatusCode().toString();
        } catch (HttpClientErrorException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    public String postAttributes(ObjectNode data, String deviceId) throws Exception {
        try {
            HttpHeaders requestHeaders = getHttpHeaders();
            if (requestHeaders == null) {
                logger.warn("auth header couldn't found...");

            }

            String POST_DEVICE_ATTRIBUTE = "/api/plugins/telemetry/${DEVICE_ID}/SERVER_SCOPE";

            String uri = (EGYS_REST_ENDPOINT + POST_DEVICE_ATTRIBUTE ).replace("${DEVICE_ID}", deviceId);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.convertValue(data, JsonNode.class);
            HttpEntity<JsonNode> httpEntity = new HttpEntity<JsonNode>(node, requestHeaders);

            ResponseEntity responseEntity = restTemplate.postForEntity(uri, httpEntity, ResponseEntity.class);

            return responseEntity.getStatusCode().toString();
        } catch (HttpClientErrorException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

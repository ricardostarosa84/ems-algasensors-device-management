//package com.algaworks.algasensors.device.management.api.client.impl;
//
//import com.algaworks.algasensors.device.management.api.client.RestClientFactory;
//import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
//import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
//import io.hypersistence.tsid.TSID;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClient;
//
//@Component
//public class SensorMonitoringClientImpl implements SensorMonitoringClient {
//
//    private final RestClient restClient;
//
//    public SensorMonitoringClientImpl(RestClientFactory restClient) {
//        this.restClient = restClient.temperatureMonitoringRestClient();
//    }
//
//
//    @Override
//    public void enableMonitoring(TSID tsid) {
//        restClient.put()
//                .uri("/api/sensors/{sensorId}/monitoring/enable", tsid)
//                .retrieve()
//                .toBodilessEntity();
//    }
//
//    @Override
//    public void disableMonitoring(TSID tsid) {
//        restClient.delete()
//                .uri("/api/sensors/{sensorId}/monitoring/enable", tsid)
//                .retrieve()
//                .toBodilessEntity();
//    }
//
//    @Override
//    public SensorMonitoringOutput getDetail(TSID sensorID) {
//        return restClient.get()
//                .uri("/api/sensors/{sensorId}/monitoring", sensorID)
//                .retrieve()
//                .body(SensorMonitoringOutput.class);
//    }
//}

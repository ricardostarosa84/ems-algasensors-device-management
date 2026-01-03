package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorID;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient sensorMonitoringClient;

    @GetMapping
    public Page<SensorOutput> getAll(@PageableDefault Pageable page){
        Page<Sensor> all = sensorRepository.findAll(page);

        return all.map(this::convertSensorOutput);
    }

    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable @Nonnull TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return convertSensorOutput(sensor);
    }

    @GetMapping("{sensorId}/detail")
    public SensorDetailOutput getOneWithDetail(@PathVariable @Nonnull TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        SensorMonitoringOutput monitoring = sensorMonitoringClient.getDetail(sensorId);

        return SensorDetailOutput.builder()
                .sensorOutput(convertSensorOutput(sensor))
                .sensorMonitoringOutput(monitoring)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput sensorInput){

        Sensor build = Sensor.builder()
                .id(new SensorID(IdGenerator.getTSID()))
                .ip(sensorInput.getIp())
                .name(sensorInput.getName())
                .location(sensorInput.getLocation())
                .model(sensorInput.getModel())
                .protocol(sensorInput.getProtocol())
                .enabled(Boolean.FALSE)
                .build();

        Sensor sensor = sensorRepository.saveAndFlush(build);

        return convertSensorOutput(sensor);
    }

    @PutMapping("{sensorId}")
    public SensorOutput updateSensor(@PathVariable @Nonnull TSID sensorId, @RequestBody SensorInput sensorInput){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        updateSensor(sensorInput, sensor);

       return convertSensorOutput(sensorRepository.save(sensor));
    }

    @PutMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enableSensor(@PathVariable @Nonnull TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setEnabled(Boolean.TRUE);

        convertSensorOutput(sensorRepository.save(sensor));
        sensorMonitoringClient.enableMonitoring(sensorId);
    }

    @DeleteMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableSensor(@PathVariable @Nonnull TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setEnabled(Boolean.FALSE);

        convertSensorOutput(sensorRepository.save(sensor));
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    @DeleteMapping("{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensor(@PathVariable @Nonnull TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorID(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensorRepository.delete(sensor);
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    private void updateSensor(SensorInput sensorInput, Sensor sensor) {
        sensor.setName(sensorInput.getName());
        sensor.setIp(sensorInput.getIp());
        sensor.setLocation(sensorInput.getLocation());
        sensor.setProtocol(sensorInput.getProtocol());
        sensor.setModel(sensorInput.getModel());
    }

    private SensorOutput convertSensorOutput(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .ip(sensor.getIp())
                .name(sensor.getName())
                .location(sensor.getLocation())
                .model(sensor.getModel())
                .protocol(sensor.getProtocol())
                .build();
    }
}

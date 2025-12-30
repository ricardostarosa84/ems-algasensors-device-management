package com.algaworks.algasensors.device.management.api.model;


import jakarta.annotation.Nonnull;
import lombok.Data;

@Data
public class SensorInput {
    @Nonnull
	private String name;
    @Nonnull
	private String ip;
    @Nonnull
	private String location;
    @Nonnull
	private String protocol;
    @Nonnull
	private String model;
}

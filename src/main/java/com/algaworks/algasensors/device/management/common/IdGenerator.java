package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;

import java.util.Optional;

public class IdGenerator {

    private static final TSID.Factory TSID_GENERATOR;

    static {
        Optional.ofNullable(System.getenv("tsid.node"))
                .ifPresent(tsidNode -> System.setProperty("tsid.node", tsidNode));
        Optional.ofNullable(System.getenv("tsid.node.count"))
                .ifPresent(tsidNode -> System.setProperty("tsid.node.count", tsidNode));

        TSID_GENERATOR = TSID.Factory.builder().build();
    }

    private IdGenerator(){}

    public static TSID getTSID(){

        return TSID_GENERATOR.generate();
    }
}

package com.kpi;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Config config = new Config();
        config.setClusterName("dev");

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName("com.kpi.CounterMapStore");
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setWriteDelaySeconds(0);

        MapConfig mapConfig = new MapConfig("counters-map");
        mapConfig.setMapStoreConfig(mapStoreConfig);

        config.addMapConfig(mapConfig);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        Map<Integer, Long> counters = hz.getMap("counters-map");

        int id = Integer.parseInt(args[0]);

        for (int i = 0; i < 100000; i++) {
            counters.compute(id, (k, v) -> v == null ? 1 : v + 1);
        }

        System.out.println("Counter " + id + " = " + counters.get(id));

        hz.shutdown();
    }
}

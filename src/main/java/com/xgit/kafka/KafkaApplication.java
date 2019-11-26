package com.xgit.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaApplication {
    public static String BROKER_SERVERS = "";

    public static void main(String[] args) {
        if(args.length>0){
            BROKER_SERVERS = args[0];
        }
        SpringApplication.run(KafkaApplication.class, args);
    }

}

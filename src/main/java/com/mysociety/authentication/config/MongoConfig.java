package com.mysociety.authentication.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;


@Configuration
public class MongoConfig {

    @Value("${database_uri}")
    private String DATABASE_URI;

    @Value("${database}")
    private String DATABASE;

    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create(DATABASE_URI);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient){
        return new MongoTemplate(mongoClient, DATABASE);
    }
}

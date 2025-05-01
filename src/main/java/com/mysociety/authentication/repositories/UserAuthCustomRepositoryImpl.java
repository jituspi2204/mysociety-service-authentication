package com.mysociety.authentication.repositories;

import com.mysociety.authentication.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class UserAuthCustomRepositoryImpl implements UserAuthCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void refreshTokenByPhoneNumber(String phoneNumber){
        Query query = Query.query(Criteria.where("phone_number")
                .is(phoneNumber));
        Update update = new Update()
                .set("token", null).set("token_expired_at", null);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void updateUserFieldsById(String id, Map<String , Object> fields){
        Query query = Query.query(Criteria.where("_id")
                .is(id));
        Update update = new Update();
        Set<Map.Entry<String, Object>> entrySet = fields.entrySet();
        for(Map.Entry<String, Object> entry : entrySet){
            update.set(entry.getKey(),entry.getValue());
        }
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void updateUserTokenByPhoneNumber(String phoneNumber, String token, long validity){
        Query query = Query.query(Criteria.where("phone_number")
                .is(phoneNumber));
        Update update = new Update()
                .set("token", token).set("token_expired_at", new Date(System.currentTimeMillis() + validity));
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void updateUserFieldsByPhoneNumber(String phoneNumber, Map<String ,Object> fields){
        Query query = Query.query(Criteria.where("phone_number")
                .is(phoneNumber));
        Update update = new Update();
        Set<Map.Entry<String, Object>> entrySet = fields.entrySet();
        for(Map.Entry<String, Object> entry : entrySet){
            update.set(entry.getKey(),entry.getValue());
        }
        mongoTemplate.updateFirst(query, update, User.class);
    }
}

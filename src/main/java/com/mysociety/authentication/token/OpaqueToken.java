package com.mysociety.authentication.token;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document("opaquetokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpaqueToken {

    @MongoId
    private String id;
    @Field("ip_address")
    private String ipAddress;
    @Field("device_type")
    private String deviceType;
    @Field("token_alias")
    private String tokenAlias;
    @Field("token_value")
    private String tokenValue;
    @Field("created_at")
    private Date createdAt;
    @Field("expired_at")
    private Date expiredAt;
    @Field("issued_by")
    private String issuedBy;
    @Field("is_active")
    private boolean isActive;

}

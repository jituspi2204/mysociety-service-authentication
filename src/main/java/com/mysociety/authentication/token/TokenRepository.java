package com.mysociety.authentication.token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<OpaqueToken, String> {

    public Optional<OpaqueToken> findByTokenAlias(String tokenAlias);
    public void deleteByTokenAlias(String tokenAlias);
}

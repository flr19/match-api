package com.match.matchapi;

import com.match.matchapi.Matches;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends MongoRepository<Matches, String> {

}
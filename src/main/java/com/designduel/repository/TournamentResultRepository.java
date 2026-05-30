package com.designduel.repository;

import com.designduel.model.TournamentResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TournamentResultRepository extends MongoRepository<TournamentResult, String> {
    List<TournamentResult> findAllByOrderByGeneratedAtDesc();
}

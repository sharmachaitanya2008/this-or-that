package com.designduel.repository;

import com.designduel.model.Judge;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface JudgeRepository extends MongoRepository<Judge, String> {
    Optional<Judge> findByUsername(String username);
    Optional<Judge> findByUsernameIgnoreCase(String username);
    long countByFinalPickId(String designId);
}

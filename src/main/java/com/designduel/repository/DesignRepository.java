package com.designduel.repository;

import com.designduel.model.Design;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DesignRepository extends MongoRepository<Design, String> {
    List<Design> findByActiveTrue();
    Page<Design> findByActiveTrue(Pageable pageable);
    long countByActiveTrue();
}

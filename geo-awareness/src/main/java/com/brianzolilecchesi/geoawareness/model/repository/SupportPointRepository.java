package com.brianzolilecchesi.geoawareness.model.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brianzolilecchesi.geoawareness.model.persistency.supportPoint.SupportPoint;

@Repository
public interface SupportPointRepository extends MongoRepository<SupportPoint, String> {
    
}

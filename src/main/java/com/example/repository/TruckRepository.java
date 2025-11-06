package com.example.repository;

import com.example.model.Truck;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TruckRepository extends MongoRepository<Truck, String> {
}

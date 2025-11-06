package com.example.repository;

import com.example.model.LeaseContract;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeaseContractRepository extends MongoRepository<LeaseContract, String> {
}

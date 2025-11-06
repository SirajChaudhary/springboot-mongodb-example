package com.example.service;

import com.example.model.LeaseContract;
import com.example.repository.LeaseContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaseContractService {

    private final MongoTemplate mongoTemplate;
    private final LeaseContractRepository leaseContractRepository;

    // CREATE : Add a new Lease Contract
    public LeaseContract createLeaseContract(LeaseContract contract) {
        return leaseContractRepository.save(contract);
    }

    // READ : Get all Lease Contracts
    public List<LeaseContract> getAllLeaseContracts() {
        return leaseContractRepository.findAll();
    }

    // READ : Get Lease Contract by ID
    public LeaseContract getLeaseContractById(String id) {
        return leaseContractRepository.findById(id).orElse(null);
    }

    // UPDATE : Replace entire Lease Contract document
    public LeaseContract updateLeaseContract(String id, LeaseContract updatedContract) {
        updatedContract.setId(id);
        return leaseContractRepository.save(updatedContract);
    }

    // DELETE : Remove Lease Contract by ID
    public void deleteLeaseContract(String id) {
        leaseContractRepository.deleteById(id);
    }

    // FILTER : Lease Contracts by Status
    public List<LeaseContract> findByStatus(String status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, LeaseContract.class);
    }

    // SEARCH : Lease Contracts by Lessee Name or Destination City (Regex)
    public List<LeaseContract> searchLeaseContracts(String keyword) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("lesseeName").regex(keyword, "i"),
                Criteria.where("destinationCity").regex(keyword, "i")
        ));
        return mongoTemplate.find(query, LeaseContract.class);
    }

    // PARTIAL UPDATE : Update only Status field
    public void updateLeaseContractStatus(String id, String status) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, LeaseContract.class);
    }

    // PARTIAL UPDATE : Update only Lease Amount field
    public void updateLeaseContractAmount(String id, double newAmount) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("leaseAmount", newAmount);
        mongoTemplate.updateFirst(query, update, LeaseContract.class);
    }

    // AGGREGATION : Group by Origin City and calculate total lease
    public List<?> totalLeaseByOriginCity() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("originCity")
                        .sum("leaseAmount").as("totalLease")
                        .count().as("contractsCount")
        );
        return mongoTemplate.aggregate(aggregation, "leaseContracts", Object.class).getMappedResults();
    }

    // TRANSACTION : Activate Lease Contract (set status ACTIVE)
    @Transactional
    public void activateLeaseContract(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", "ACTIVE");
        mongoTemplate.updateFirst(query, update, LeaseContract.class);
    }
}

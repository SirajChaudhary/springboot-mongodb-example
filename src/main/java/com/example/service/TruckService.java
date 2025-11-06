package com.example.service;

import com.example.model.Truck;
import com.example.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final MongoTemplate mongoTemplate;
    private final TruckRepository truckRepository;

    // CREATE : Add a new Truck
    public Truck createTruck(Truck truck) {
        return truckRepository.save(truck);
    }

    // READ : Get all Trucks
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    // READ : Get Truck by ID
    public Truck getTruckById(String id) {
        return truckRepository.findById(id).orElse(null);
    }

    // DELETE : Remove Truck by ID
    public void deleteTruck(String id) {
        truckRepository.deleteById(id);
    }

    // FILTER : Available Trucks in a specific City
    public List<Truck> findAvailableTrucks(String city) {
        Query query = new Query(Criteria.where("available").is(true)
                .and("currentCity").is(city));
        return mongoTemplate.find(query, Truck.class);
    }

    // SEARCH : Find Trucks by Truck Number or Type (case-insensitive)
    public List<Truck> searchTrucks(String keyword) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("truckNumber").regex(keyword, "i"),
                Criteria.where("type").regex(keyword, "i")
        ));
        return mongoTemplate.find(query, Truck.class);
    }

    // SORT : Sort Trucks by Capacity (ascending or descending)
    public List<Truck> sortByCapacity(boolean desc) {
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Query query = new Query().with(Sort.by(direction, "capacityTons"));
        return mongoTemplate.find(query, Truck.class);
    }

    // PAGINATION : Retrieve Trucks by Page and Size
    public List<Truck> paginateTrucks(int page, int size) {
        Query query = new Query().skip((long) (page - 1) * size).limit(size);
        return mongoTemplate.find(query, Truck.class);
    }

    // PROJECTION : Include only selected Truck fields
    public List<Truck> projectedTrucks() {
        Query query = new Query();
        query.fields().include("truckNumber").include("type").include("available");
        return mongoTemplate.find(query, Truck.class);
    }

    // FILTER : Trucks with Capacity greater than given tons
    public List<Truck> trucksAboveCapacity(double minTons) {
        Query query = new Query(Criteria.where("capacityTons").gt(minTons));
        return mongoTemplate.find(query, Truck.class);
    }

    // PARTIAL UPDATE : Update Truck's Current City
    public void updateTruckCity(String truckNumber, String newCity) {
        Query query = new Query(Criteria.where("truckNumber").is(truckNumber));
        Update update = new Update().set("currentCity", newCity);
        mongoTemplate.updateFirst(query, update, Truck.class);
    }

    // PARTIAL UPDATE : Update Truck Availability
    public void updateAvailability(String truckNumber, boolean available) {
        Query query = new Query(Criteria.where("truckNumber").is(truckNumber));
        Update update = new Update().set("available", available);
        mongoTemplate.updateFirst(query, update, Truck.class);
    }

    // GROUPING : Group Trucks by City and calculate total count and capacity
    public List<?> groupByCity() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("currentCity")
                        .count().as("truckCount")
                        .sum("capacityTons").as("totalCapacity")
        );
        return mongoTemplate.aggregate(aggregation, "trucks", Object.class).getMappedResults();
    }
}

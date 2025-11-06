package com.example.controller;

import com.example.model.Truck;
import com.example.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    // CREATE : Add a new Truck
    @PostMapping
    public Truck createTruck(@RequestBody Truck truck) {
        return truckService.createTruck(truck);
    }

    // READ : Get all Trucks
    @GetMapping
    public List<Truck> getAllTrucks() {
        return truckService.getAllTrucks();
    }

    // READ : Get Truck by ID
    @GetMapping("/{id}")
    public Truck getTruckById(@PathVariable String id) {
        return truckService.getTruckById(id);
    }

    // DELETE : Remove Truck by ID
    @DeleteMapping("/{id}")
    public void deleteTruck(@PathVariable String id) {
        truckService.deleteTruck(id);
    }

    // FILTER : Find available Trucks in a specific City
    @GetMapping("/available")
    public List<Truck> findAvailableTrucks(@RequestParam String city) {
        return truckService.findAvailableTrucks(city);
    }

    // SEARCH : Find Trucks by Truck Number or Driver Name
    @GetMapping("/search")
    public List<Truck> searchTrucks(@RequestParam String keyword) {
        return truckService.searchTrucks(keyword);
    }

    // SORT : Sort Trucks by Capacity (Ascending or Descending)
    @GetMapping("/sort")
    public List<Truck> sortTrucksByCapacity(@RequestParam(defaultValue = "false") boolean desc) {
        return truckService.sortByCapacity(desc);
    }

    // PAGINATION : Get Trucks by Page and Size
    @GetMapping("/page")
    public List<Truck> paginateTrucks(@RequestParam int page, @RequestParam int size) {
        return truckService.paginateTrucks(page, size);
    }

    // PROJECTION : Get only Selected Truck Fields (Projection Example)
    @GetMapping("/projected")
    public List<Truck> projectedTrucks() {
        return truckService.projectedTrucks();
    }

    // FILTER : Trucks above given Capacity (in Tons)
    @GetMapping("/capacity")
    public List<Truck> trucksAboveCapacity(@RequestParam double minTons) {
        return truckService.trucksAboveCapacity(minTons);
    }

    // PARTIAL UPDATE : Update Truck's City
    @PatchMapping("/{truckNumber}/city")
    public void updateTruckCity(@PathVariable String truckNumber, @RequestParam String city) {
        truckService.updateTruckCity(truckNumber, city);
    }

    // PARTIAL UPDATE : Update Truck Availability
    @PatchMapping("/{truckNumber}/availability")
    public void updateTruckAvailability(@PathVariable String truckNumber, @RequestParam boolean available) {
        truckService.updateAvailability(truckNumber, available);
    }

    // GROUPING : Group Trucks by City and count them
    @GetMapping("/groupByCity")
    public List<?> groupTrucksByCity() {
        return truckService.groupByCity();
    }
}

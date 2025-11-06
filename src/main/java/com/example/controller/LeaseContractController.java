package com.example.controller;

import com.example.model.LeaseContract;
import com.example.service.LeaseContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class LeaseContractController {

    private final LeaseContractService leaseContractService;

    // CREATE : Add a new Lease Contract
    @PostMapping
    public LeaseContract createLeaseContract(@RequestBody LeaseContract contract) {
        return leaseContractService.createLeaseContract(contract);
    }

    // READ : Get all Lease Contracts
    @GetMapping
    public List<LeaseContract> getAllLeaseContracts() {
        return leaseContractService.getAllLeaseContracts();
    }

    // READ : Get Lease Contract by ID
    @GetMapping("/{id}")
    public LeaseContract getLeaseContractById(@PathVariable String id) {
        return leaseContractService.getLeaseContractById(id);
    }

    // UPDATE : Replace full Lease Contract document
    @PutMapping("/{id}")
    public LeaseContract updateLeaseContract(@PathVariable String id, @RequestBody LeaseContract contract) {
        return leaseContractService.updateLeaseContract(id, contract);
    }

    // DELETE : Remove Lease Contract by ID
    @DeleteMapping("/{id}")
    public void deleteLeaseContract(@PathVariable String id) {
        leaseContractService.deleteLeaseContract(id);
    }

    // FILTER : Lease Contracts by Status
    @GetMapping("/status")
    public List<LeaseContract> findByStatus(@RequestParam String status) {
        return leaseContractService.findByStatus(status);
    }

    // SEARCH : Lease Contracts by Lessee or Destination City
    @GetMapping("/search")
    public List<LeaseContract> searchLeaseContracts(@RequestParam String keyword) {
        return leaseContractService.searchLeaseContracts(keyword);
    }

    // PARTIAL UPDATE : Update only Status
    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable String id, @RequestParam String status) {
        leaseContractService.updateLeaseContractStatus(id, status);
    }

    // PARTIAL UPDATE : Update only Lease Amount
    @PatchMapping("/{id}/amount")
    public void updateLeaseAmount(@PathVariable String id, @RequestParam double amount) {
        leaseContractService.updateLeaseContractAmount(id, amount);
    }

    // AGGREGATION : Total Lease grouped by Origin City
    @GetMapping("/totalLeaseByOriginCity")
    public List<?> totalLeaseByOriginCity() {
        return leaseContractService.totalLeaseByOriginCity();
    }

    // TRANSACTION : Activate Lease Contract
    @PostMapping("/{id}/activate")
    public void activateLeaseContract(@PathVariable String id) {
        leaseContractService.activateLeaseContract(id);
    }
}

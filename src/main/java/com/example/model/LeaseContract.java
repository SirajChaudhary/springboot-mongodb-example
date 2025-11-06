package com.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "leaseContracts")
public class LeaseContract {

    @Id
    private String id;

    private String truckNumber;    // Foreign key (reference to Truck)
    private String lesseeName;
    private String originCity;
    private String destinationCity;
    private double leaseAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;         // ACTIVE, COMPLETED, CANCELLED
}

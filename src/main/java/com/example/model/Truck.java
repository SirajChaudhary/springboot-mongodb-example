package com.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "trucks")
public class Truck {

    @Id
    private String id;

    private String truckNumber;   // Unique identifier for linking with LeaseContract
    private String type;          // e.g., Flatbed, Refrigerated, Container
    private double capacityTons;
    private String ownerCompany;
    private String currentCity;
    private boolean available;
}

# Spring Boot MongoDB Example — Truck & Lease Contract Management

This project demonstrates an **end-to-end Spring Boot and MongoDB** integration using `MongoTemplate`, repository interfaces, and advanced query techniques.

It manages **Trucks** and **Lease Contracts** with various MongoDB features — CRUD, filtering, searching, sorting, pagination, projection, aggregation, and transaction examples.

---

## Project Overview

This Spring Boot application connects to a local MongoDB database (`truck_lease_service_db`) and exposes REST APIs to manage trucks and lease contracts.  
It uses `MongoTemplate` for dynamic query building and aggregation pipelines, and `MongoTransactionManager` to enable multi-document transactions.

---

## Prerequisites

- Java 21
- Spring Boot 3.5.7
- Maven 3.9+  
- MongoDB running locally (default port 27017)  
- Postman 

---

## How to Run

1. **Start MongoDB** locally.
2. **To import sample MongoDB data, refer to the instructions in the how-to-import-sample-data.txt file located in the /resources folder of the project.** <br>

3. **Clone this project:**
   ```bash
   git clone https://github.com/sirajchaudhary/springboot-mongodb-example.git
   cd springboot-mongodb-example
   ```
4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
5. **Import Postman collection:**
   Import the file `postman_collection.json` located in the root folder.
6. **Test APIs** one by one from Postman.

---

## API Endpoints Overview

### Truck APIs

| API Feature | Method | Endpoint | Example Input | Example Behavior |
|--------------|---------|-----------|----------------|------------------|
| Create Truck | POST | /api/trucks | JSON Body | Add a new truck |
| Get All Trucks | GET | /api/trucks | None | Returns list of trucks |
| Get Truck by ID | GET | /api/trucks/{id} | Path ID | Fetch specific truck |
| Delete Truck | DELETE | /api/trucks/{id} | Path ID | Delete truck record |
| Filter Available by City | GET | /api/trucks/available?city=Hyderabad | Query Param | Returns available trucks in Hyderabad |
| Search Trucks | GET | /api/trucks/search?keyword=Refrigerated | Query Param | Search by truck number or type |
| Sort Trucks | GET | /api/trucks/sort?desc=true | Query Param | Sorts by capacity descending |
| Pagination | GET | /api/trucks/page?page=1&size=5 | Query Param | Returns first 5 trucks |
| Projection | GET | /api/trucks/projected | None | Returns selected truck fields |
| Filter by Capacity | GET | /api/trucks/capacity?minTons=25 | Query Param | Trucks with capacity > 25 tons |
| Partial Update City | PATCH | /api/trucks/{truckNumber}/city | Path + Query | Update city of a truck |
| Partial Update Availability | PATCH | /api/trucks/{truckNumber}/availability | Path + Query | Update availability flag |
| Group by City | GET | /api/trucks/groupByCity | None | Aggregates total trucks and capacity per city |

---

### Lease Contract APIs

| API Feature | Method | Endpoint | Example Input | Example Behavior |
|--------------|---------|-----------|----------------|------------------|
| Create Lease Contract | POST | /api/contracts | JSON Body | Add a new lease contract |
| Get All Lease Contracts | GET | /api/contracts | None | Fetch all contracts |
| Get Lease Contract by ID | GET | /api/contracts/{id} | Path ID | Fetch single contract |
| Update Lease Contract | PUT | /api/contracts/{id} | JSON Body | Replace full document |
| Delete Lease Contract | DELETE | /api/contracts/{id} | Path ID | Remove contract |
| Filter by Status | GET | /api/contracts/status?status=ACTIVE | Query Param | Filter contracts by status |
| Search Contracts | GET | /api/contracts/search?keyword=Bangalore | Query Param | Search by lessee name or destination |
| Partial Update Status | PATCH | /api/contracts/{id}/status?status=COMPLETED | Path + Query | Update contract status only |
| Partial Update Amount | PATCH | /api/contracts/{id}/amount?amount=90000 | Path + Query | Update only lease amount |
| Aggregation | GET | /api/contracts/totalLeaseByOriginCity | None | Sum lease amount grouped by city |
| Transaction | POST | /api/contracts/{id}/activate | Path ID | Activate a lease via transaction |

---

## MongoTemplate Features Demonstrated

| # | Feature | Example Method |
|---|----------|----------------|
| 1 | CRUD | createTruck, createLeaseContract |
| 2 | Filter | findAvailableTrucks, findByStatus |
| 3 | Search (Regex) | searchTrucks, searchLeaseContracts |
| 4 | Sort | sortByCapacity |
| 5 | Pagination | paginateTrucks |
| 6 | Projection | projectedTrucks |
| 7 | Expression (gt) | trucksAboveCapacity |
| 8 | Grouping / Aggregation | groupByCity, totalLeaseByOriginCity |
| 9 | Partial Update (set) | updateTruckCity, updateAvailability, updateLeaseAmount |
| 10 | Transaction | activateLeaseContract |

---

## Code Highlights

### 1. Searching (Regex Queries)
```java
public List<Truck> searchTrucks(String keyword) {
    Query query = new Query(new Criteria().orOperator(
        Criteria.where("truckNumber").regex(keyword, "i"),
        Criteria.where("type").regex(keyword, "i")
    ));
    return mongoTemplate.find(query, Truck.class);
}
```
This uses **`Criteria.orOperator()`** with **case-insensitive regex** for flexible searching.

---

### 2. Sorting by Capacity
```java
public List<Truck> sortByCapacity(boolean desc) {
    Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
    Query query = new Query().with(Sort.by(direction, "capacityTons"));
    return mongoTemplate.find(query, Truck.class);
}
```
Implemented using **`Sort.by()`** with ascending or descending direction dynamically.

---

### 3. Filtering by Status or City
```java
public List<Truck> findAvailableTrucks(String city) {
    Query query = new Query(Criteria.where("available").is(true)
        .and("currentCity").is(city));
    return mongoTemplate.find(query, Truck.class);
}
```
Demonstrates **filter chaining** using multiple `Criteria` fields.

---

### 4. Pagination
```java
public List<Truck> paginateTrucks(int page, int size) {
    Query query = new Query().skip((long) (page - 1) * size).limit(size);
    return mongoTemplate.find(query, Truck.class);
}
```
Implements manual pagination using **`skip()`** and **`limit()`**.

---

### 5. Projection (Field Selection)
```java
public List<Truck> projectedTrucks() {
    Query query = new Query();
    query.fields().include("truckNumber").include("type").include("available");
    return mongoTemplate.find(query, Truck.class);
}
```
Shows how to **include only specific fields** using projection.

---

### 6. Aggregation (Grouping)
```java
public List<?> groupByCity() {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.group("currentCity")
            .count().as("truckCount")
            .sum("capacityTons").as("totalCapacity")
    );
    return mongoTemplate.aggregate(agg, "trucks", Object.class).getMappedResults();
}
```
Performs **grouping and summarization** via the aggregation pipeline.

---

### 7. Transactions Example
```java
@Transactional
public void activateLeaseContract(String id) {
    LeaseContract contract = leaseContractRepository.findById(id).orElseThrow();
    contract.setStatus("ACTIVE");
    leaseContractRepository.save(contract);
}
```
Enabled by **MongoTransactionManager** for safe updates across documents.

---

## Folder Structure

```
springboot-mongodb-example/
│
├── src/main/java/com/example/
│   ├── controller/
│   │   ├── TruckController.java
│   │   └── LeaseContractController.java
│   ├── service/
│   │   ├── TruckService.java
│   │   └── LeaseContractService.java
│   ├── model/
│   │   ├── Truck.java
│   │   └── LeaseContract.java
│   └── config/
│       └── MongoConfig.java
│
├── sample-mongodb-data/
│   ├── trucks.json
│   └── leaseContracts.json
│
├── postman_collection.json
├── pom.xml
├── README.md
└── application.properties
```

---

## Summary

This project demonstrates how to implement advanced MongoDB operations in a clean, layered Spring Boot architecture — showcasing powerful usage of `MongoTemplate`, `Criteria`, and transactions.  
It can serve as a boilerplate for any logistics or rental management microservice.

---

## How to Containerize Your Spring Boot Application with Podman

### **Step 1: Create Containerfile**

```dockerfile
# Use Eclipse Temurin JDK as base image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file built by Maven into the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **Step 2: Update MongoDB URL**

Update your `application.properties` (or `application.yml`) so that MongoDB is accessed from the **host machine**, not inside the container.

**Replace:**
```properties
#spring.data.mongodb.uri=mongodb://localhost:27017/truck_lease_service_db
```

**With:**
```properties
spring.data.mongodb.uri=mongodb://10.107.6.110:27017/truck_lease_service_db
```

> Here `10.107.6.110` is your system's IP address.  
> You can find it using the command:
> ```
> ipconfig
> ```

### **Step 3: Package the Application**

Run the following Maven command to build your JAR:
```bash
mvn clean install
```

### **Step 4: Allow MongoDB to Accept External Connections**

By default, MongoDB on Windows binds only to `127.0.0.1`.

Edit your MongoDB configuration file:
```
C:\Program Files\MongoDB\Server\<version>\bin\mongod.cfg
```

**Find:**
```yaml
net:
  bindIp: 127.0.0.1
  port: 27017
```

**Change to:**
```yaml
net:
  port: 27017
  bindIp: 0.0.0.0
```

Then restart MongoDB:
```bash
net stop MongoDB
net start MongoDB
```

### **Step 5: (Optional) Allow Port 27017 in Firewall**

Open PowerShell as Administrator and run:
```bash
New-NetFirewallRule -DisplayName "Allow MongoDB 27017" -Direction Inbound -Protocol TCP -LocalPort 27017 -Action Allow
```

### **Step 6: Build the Image and Run the Container**

Build the Podman image:
```bash
podman build -t springboot-mongodb-example .
```

Run the container:
```bash
podman run -d --name springboot-mongodb-example-container -p 8080:8080 springboot-mongodb-example
```

### **Step 7: Test MongoDB Connectivity from Inside Container**

Enter the container shell:
```bash
podman exec -it springboot-mongodb-example-container bash
```

Install `netcat`:
```bash
apt-get update && apt-get install -y netcat
apt-get install -y netcat-openbsd
```

Test MongoDB connection:
```bash
nc -zv 10.107.6.110 27017
```

If you see:
```
Connection to 10.107.6.110 27017 port [tcp/*] succeeded!
```
MongoDB is reachable.

Or check via Spring Boot logs:
```bash
podman logs -f springboot-mongodb-example-container
```

If you see:
```
Connected to MongoDB at mongodb://10.107.6.110:27017
```
You’re good to go — MongoDB is connected successfully.

### **Step 8: Run and Test APIs from Local Machine**

Install `curl` (if not already installed):
```bash
apt-get update && apt-get install -y curl
```

Test your API:
```bash
curl http://localhost:8080/api/trucks
```

You can also use **Postman** to test the same endpoint.


---

## License

Free Software, by [Siraj Chaudhary](https://www.linkedin.com/in/sirajchaudhary/)
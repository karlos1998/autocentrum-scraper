package it.letscode.autocentrum_scraper.car_model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarModelRepository extends MongoRepository<CarModel, String> {
}

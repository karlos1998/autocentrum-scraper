package it.letscode.autocentrum_scraper.car_model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface CarModelRepository extends MongoRepository<CarModel, String> {
    @Query(value = "{ 'brandUrl': { '$nin': ?0 } }", delete = true)
    void deleteByBrandUrlNotIn(List<String> brandUrls);
}

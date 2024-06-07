package it.letscode.autocentrum_scraper.car_model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarModelRepository extends MongoRepository<CarModel, String> {

    @Query("{ 'modelUrl': { '$regex': '^?0', '$options': 'i' } }")
    Page<CarModel> findByModelUrlStartingWith(String modelUrl, Pageable pageable);


    @Query(value = "{ 'brandUrl': { '$nin': ?0 } }", delete = true)
    void deleteByBrandUrlNotIn(List<String> brandUrls);

    Boolean existsByModelUrl(String modelUrl);
}

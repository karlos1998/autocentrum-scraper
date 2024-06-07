package it.letscode.autocentrum_scraper.car_model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends MongoRepository<CarModel, String> {

    @Query("{ 'modelUrl': { '$regex': '^?0', '$options': 'i' } }")
    Page<CarModel> findByModelUrlStartingWith(String modelUrl, Pageable pageable);

    @Query(value = "{ 'name': { '$regex': ?0, '$options': 'i' } }")
    Page<CarModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = "{ 'brandUrl': { '$nin': ?0 } }", delete = true)
    void deleteByBrandUrlNotIn(List<String> brandUrls);

    Boolean existsByModelUrl(String modelUrl);
}

package it.letscode.autocentrum_scraper.scraped_brand;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScrapedBrandRepository extends MongoRepository<ScrapedBrand, String> {
}

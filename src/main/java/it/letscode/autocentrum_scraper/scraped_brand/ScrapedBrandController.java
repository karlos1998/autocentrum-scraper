package it.letscode.autocentrum_scraper.scraped_brand;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scraped-brands")
public class ScrapedBrandController {
    private final ScrapedBrandRepository scrapedBrandRepository;

    @GetMapping
    public Page<ScrapedBrand> findAll(Pageable pageable) {
        return scrapedBrandRepository.findAll(pageable);
    }
}

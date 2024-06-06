package it.letscode.autocentrum_scraper.car_model;

import it.letscode.autocentrum_scraper.scraper.ScraperCarModelDetailsService;
import lombok.AllArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static it.letscode.autocentrum_scraper.scraper.ScraperService.setupWebDriver;

@RestController
@AllArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;
    private final ScraperCarModelDetailsService scraperCarModelDetailsService;

    @GetMapping
    public Page<CarModel> findAll(Pageable pageable,
                                  @RequestParam(required = false) String modelUrl) {
        if (modelUrl != null && !modelUrl.isEmpty()) {
            return carModelService.findByModelUrlStartingWith(modelUrl, pageable);
        } else {
            return carModelService.findAll(pageable);
        }
    }

    @GetMapping("test")
    public List<CarModel> test(@RequestParam(required = false) String modelUrl) {
        System.out.println("Test route, find by: " + modelUrl);
        WebDriver driver = setupWebDriver();
        CarModel carModel = new CarModel();
        carModel.setModelUrl(modelUrl);
        try {
            return scraperCarModelDetailsService.getAccurateCarModels(driver, carModel);
        } finally {
            System.out.println("Driver closed");
            driver.close();
        }
    }
}

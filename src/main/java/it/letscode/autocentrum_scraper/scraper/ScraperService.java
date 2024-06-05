package it.letscode.autocentrum_scraper.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.letscode.autocentrum_scraper.brand.Brand;
import it.letscode.autocentrum_scraper.car_model.CarModel;
import it.letscode.autocentrum_scraper.car_model.CarModelRepository;
import lombok.AllArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScraperService {

    public static final String baseUrl = "https://www.autocentrum.pl/";

    private final CarModelRepository carModelRepository;

    private final ScraperBrandService scraperBrandService;
    private final ScraperCarModelDetailsService scraperCarModelDetailsService;

    private WebDriver setupWebDriver() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage"); //todo chce to przetestowac. podobno do uzytku danych z normalnej przegladarki (moze sesja? :LD )

        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    public void run() {

        WebDriver driver = setupWebDriver();

        try {
            List<Brand> brands = scraperBrandService.getBrandsFromWeb(driver);

            for(Brand brand : brands) {
                List<CarModel> carBrandModels = scraperBrandService.getBrandModelsFromWeb(driver, brand.getUrl());
                for(CarModel model : carBrandModels) {
                    System.out.println(model.getModelUrl());
                    List<CarModel> carModels = scraperCarModelDetailsService.getAccurateCarModels(driver, model);
                    carModelRepository.saveAll(carModels);
                }
            }
        } finally {
            driver.quit();
        }
    }
}

package it.letscode.autocentrum_scraper.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.letscode.autocentrum_scraper.brand.Brand;
import it.letscode.autocentrum_scraper.model.Model;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScraperService {

    public static final String baseUrl = "https://www.autocentrum.pl/";

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
                List<Model> carBrandModels = scraperBrandService.getBrandModelsFromWeb(driver, brand.getUrl());
                for(Model model : carBrandModels) {
                    System.out.println(model.getModelUrl());
                    scraperCarModelDetailsService.getAccurateCarModels(driver, model);
                }
            }

        } finally {
            driver.quit();
        }
    }
}

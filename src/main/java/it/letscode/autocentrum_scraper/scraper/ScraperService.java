package it.letscode.autocentrum_scraper.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.letscode.autocentrum_scraper.brand.Brand;
import it.letscode.autocentrum_scraper.car_model.CarModel;
import it.letscode.autocentrum_scraper.car_model.CarModelRepository;
import it.letscode.autocentrum_scraper.scraped_brand.ScrapedBrand;
import it.letscode.autocentrum_scraper.scraped_brand.ScrapedBrandRepository;
import lombok.AllArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class ScraperService {

    public static final String baseUrl = "https://www.autocentrum.pl/";

    private final CarModelRepository carModelRepository;
    private final ScraperBrandService scraperBrandService;
    private final ScraperCarModelDetailsService scraperCarModelDetailsService;
    private final ScrapedBrandRepository scrapedBrandRepository;

    private WebDriver setupWebDriver() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    public void run() {

//        carModelRepository.deleteAll();
//        scrapedBrandRepository.deleteAll();


        WebDriver driver = setupWebDriver();

        // Pobranie listy zescrapowanych marek
        List<String> scrapedBrandsList = scrapedBrandRepository.findAll().stream().map(ScrapedBrand::getName).toList();
        carModelRepository.deleteByBrandUrlNotIn(scrapedBrandsList);

        try {
            List<Brand> brands = scraperBrandService.getBrandsFromWeb(driver);

            ExecutorService executorService = Executors.newFixedThreadPool(20);

            for (Brand brand : brands) {
                if(scrapedBrandsList.contains(brand.getUrl())) {
                    continue;
                }

                List<CarModel> carBrandModels = scraperBrandService.getBrandModelsFromWeb(driver, brand.getUrl());
                CountDownLatch latch = new CountDownLatch(carBrandModels.size());

                for (CarModel model : carBrandModels) {
                    executorService.submit(() -> {
                        WebDriver localDriver = setupWebDriver();
                        try {
                            List<CarModel> carModels = scraperCarModelDetailsService.getAccurateCarModels(localDriver, model);
                            carModelRepository.saveAll(carModels);
                        } finally {
                            localDriver.quit();
                            latch.countDown();
                        }
                    });
                }

                latch.await();
                scrapedBrandRepository.save(new ScrapedBrand(brand.getUrl()));
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            driver.quit();
            System.exit(0);
        }
    }
}

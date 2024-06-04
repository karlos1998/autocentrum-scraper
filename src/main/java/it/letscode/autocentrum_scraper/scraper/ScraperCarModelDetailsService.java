package it.letscode.autocentrum_scraper.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScraperCarModelDetailsService {
    public void getAccurateCarModels(WebDriver driver, String modelUrl) { //w kazydm modelu sa rozne generacje itp.
        String fullModelUrl = String.format("%sdane-techniczne/%s", ScraperService.baseUrl, modelUrl);
        System.out.println(fullModelUrl);
        driver.get(fullModelUrl);

        List<WebElement> carGenerations = driver.findElements(By.cssSelector(".car-selector-box-row a.car-selector-box"));
        for (WebElement carGeneration : carGenerations) {
            System.out.println("Generacja: " + carGeneration.getAttribute("href"));
        }
    }
}

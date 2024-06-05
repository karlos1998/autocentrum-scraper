package it.letscode.autocentrum_scraper.scraper;

import it.letscode.autocentrum_scraper.brand.Brand;
import it.letscode.autocentrum_scraper.car_model.CarModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScraperBrandService {
    public List<Brand> getBrandsFromWeb(WebDriver driver) {

        driver.get(ScraperService.baseUrl + "auta");

        WebElement selectElement = driver.findElement(By.cssSelector("select.select2"));

        Select select = new Select(selectElement);
        List<WebElement> optionsList = select.getOptions();

        return optionsList.stream().filter(option -> option.getAttribute("data-url") != null).map(option -> {
            Brand brand = new Brand();
            brand.setListOptionId(Integer.valueOf(option.getAttribute("value")));
            brand.setUrl(option.getAttribute("data-url"));
            return brand;
        }).toList();
    }

    public List<CarModel> getBrandModelsFromWeb(WebDriver driver, String brandUrl) {
        System.out.println(brandUrl);

        driver.get(ScraperService.baseUrl + brandUrl);

        List<WebElement> selectElements = driver.findElements(By.cssSelector(".car-selector-box.photo-loader.big-box"));
        System.out.println("Znalezionych aut: " + selectElements.size());

         return selectElements.stream().map(item -> {
            CarModel model = new CarModel();

            model.setBrandUrl(brandUrl);

            model.setModelUrl(item.getAttribute("href").replace(ScraperService.baseUrl, ""));

            try {
                WebElement imageElement = item.findElement(By.cssSelector("img[data-src]"));
                model.setImageUrl(imageElement.getAttribute("data-src"));
                //todo - pobierac obrazek, nie tylko link

                model.setTitle(imageElement.getAttribute("alt"));
            } catch (NoSuchElementException ignored) {
                try {
                    model.setTitle(
                        String.format("%s %s",
                            item.findElement(By.cssSelector(".car-name")).getText(),
                            item.findElement(By.cssSelector("h3.name-of-the-car")).getText()
                        )
                    );
                } catch (NoSuchElementException ignored1) {
                    //todo - moze jakas nazwa domyslna ?
                }
            }

            return model;
        }).toList();
    }
}

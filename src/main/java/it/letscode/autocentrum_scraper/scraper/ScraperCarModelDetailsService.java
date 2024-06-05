package it.letscode.autocentrum_scraper.scraper;

import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import it.letscode.autocentrum_scraper.car_model.CarModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScraperCarModelDetailsService {
    public List<CarModel> getAccurateCarModels(WebDriver driver, CarModel model) { //w kazydm modelu sa rozne generacje itp.

        String modelUrl = model.getModelUrl();

        String fullModelUrl = String.format("%sdane-techniczne/%s", ScraperService.baseUrl, modelUrl);
        System.out.println(fullModelUrl);
        driver.get(fullModelUrl);

        List<String> subUrls = getAllSubLinks(driver);

        if(subUrls.isEmpty()) {
            subUrls.add(fullModelUrl);
        }

        List<String> newSubLinks = new ArrayList<>();

        for (String subUrl : subUrls) {
            driver.get(subUrl);
            List<String> subEnginesLinks = getEnginesLinks(driver);
            if(!subEnginesLinks.isEmpty()) {
                newSubLinks.addAll(subEnginesLinks);
            } else {
                newSubLinks.add(subUrl);
            }
        }

        List<CarModel> allModelSpecs = new ArrayList<>();

        for(String subUrl : newSubLinks) {
            driver.get(subUrl);
            System.out.println("-----> " + subUrl);

            model.setName( driver.findElement(By.cssSelector("meta[property=\"og:title\"]")).getAttribute("content").replace("Dane techniczne ", ""));

            List<WebElement> transmissions = getTypesOfTransmissions(driver);

            List<Attribute> baseAttributes = getAttributes(driver.findElement(By.cssSelector("body")));

            System.out.println("Nazwa: " + model.getName());

            model.setFullModelUrl(subUrl);

            if(transmissions.isEmpty()) {
                model.setAttributes(baseAttributes);
                allModelSpecs.add(model);
            } else {
                for(WebElement element : transmissions) {
                    List<Attribute> attributes = getAttributes(element);
                    baseAttributes.removeIf(attr -> attributes.stream().anyMatch(a -> a.getName().equals(attr.getName())));
                    baseAttributes.addAll(attributes);

                    model.setAttributes(baseAttributes);
                    allModelSpecs.add(model);
                }
            }
        }

        return allModelSpecs;

    }

    private List<WebElement> getTypesOfTransmissions(WebDriver driver) {

        try {
            WebElement configuration = driver.findElement(By.cssSelector(".engine-configuration"));
            if(configuration.findElement(By.id("config-select")).isDisplayed()) {
                return configuration.findElements(By.cssSelector(".config-box"));
            }
        } catch (NoSuchElementException ignored) {
        }
        return new ArrayList<>();
    }

    private List<Attribute> getAttributes(WebElement element) {
        List<Attribute> attributes = new ArrayList<>();

        List<WebElement> rows = element.findElements(By.cssSelector(".dt-row"));

        Map<String, String> availableAttributes = new HashMap<>() {{
            put("Długość", "length");
            put("Szerokość", "width");
            put("Wysokość", "height");
            put("Długość", "length");
            put("Liczba drzwi", "doorsCount");
            put("Minimalna masa własna pojazdu (bez obciążenia)", "baseMass");
            put("Rodzaj skrzyni", "gearboxType");
        }};

        for(WebElement row : rows) {
            String description = row.findElement(By.cssSelector(".dt-row__text__content")).getAttribute("innerText");
            String value = row.findElement(By.cssSelector(".dt-param-value")).getAttribute("innerText");

            if(availableAttributes.containsKey(description)) {
                attributes.add(new Attribute(description, value, availableAttributes.get(description)));
            }
        }

        return attributes;
    }

    private List<String> getAllSubLinks(WebDriver driver) {
        return getSubcategories(driver).stream().map(link -> {
            driver.get(link);
            return getSubcategories(driver);
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> getSubcategories(WebDriver driver) {
        List<WebElement> carGenerations = driver.findElements(By.cssSelector(".car-selector-box-row a.car-selector-box"));
        return carGenerations.stream().map(carGeneration -> carGeneration.getAttribute("href")).toList();
    }

    private List<String> getEnginesLinks(WebDriver driver) {
        List<WebElement> carGenerations = driver.findElements(By.cssSelector(".engine-box a.engine-link"));
        return carGenerations.stream().map(carGeneration -> carGeneration.getAttribute("href")).toList();
    }

}

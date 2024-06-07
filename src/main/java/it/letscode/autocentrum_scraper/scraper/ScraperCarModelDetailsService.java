package it.letscode.autocentrum_scraper.scraper;

import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import it.letscode.autocentrum_scraper.car_model.CarModel;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScraperCarModelDetailsService {
    public List<CarModel> getAccurateCarModels(WebDriver driver, CarModel templateModel) { //w kazydm modelu sa rozne generacje itp.

        String modelUrl = templateModel.getModelUrl();

        String fullModelUrl = String.format("%sdane-techniczne/%s", ScraperService.baseUrl, modelUrl);
        System.out.println(fullModelUrl);
        driver.get(fullModelUrl);

        List<String> subUrls = getAllSubLinks(driver);

        System.out.println("SubUrls: " + subUrls.toArray().length);

        int test = 0;
        for(String subUrl : subUrls) {
            if(test++ > 200) break;
            System.out.println(subUrl);
        }

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

            CarModel carModel = null;
            try {
                carModel = templateModel.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            carModel.setName( driver.findElement(By.cssSelector("meta[property=\"og:title\"]")).getAttribute("content").replace("Dane techniczne ", ""));

            List<WebElement> transmissions = getTypesOfTransmissions(driver);

            List<Attribute> baseAttributes = getAttributes(driver.findElement(By.cssSelector("body")));

            System.out.println("Nazwa: " + carModel.getName());

            carModel.setFullModelUrl(subUrl);

            try {
                carModel.setFullImageUrl(driver.findElement(By.cssSelector(".chosen-car img.part__photo__image")).getAttribute("src"));
            } catch (NoSuchElementException ignored) {}

            if(transmissions.isEmpty()) {
                carModel.setAttributes(baseAttributes);
                try {
                    carModel.setGearbox(driver.findElement(By.cssSelector(".engine-configuration .primary-header")).getAttribute("innerText"));
                } catch (NoSuchElementException ignored) {}
                allModelSpecs.add(carModel);
            } else {
                List<WebElement> gearboxConfigurations = driver.findElements(By.cssSelector("select#config-select option"));
                int gearboxIndex = 0;
                for(WebElement element : transmissions) {
                    List<Attribute> attributes = getAttributes(element);
                    baseAttributes.removeIf(attr -> attributes.stream().anyMatch(a -> a.getName().equals(attr.getName())));
                    baseAttributes.addAll(attributes);

                    carModel.setGearbox(gearboxConfigurations.get(gearboxIndex++).getAttribute("innerText"));

                    carModel.setAttributes(baseAttributes);
                    allModelSpecs.add(carModel);
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
            put("Rozstaw osi", "wheelbase");
            put("Prześwit", "clearanceHeight");
            put("Rozstaw kół - przód", "frontWheelSpacing");
            put("Rozstaw kół - tył", "rearWheelSpacing");
            put("Szerokość z lusterkami bocznymi", "widthWithSideGlosses");
            put("Liczba drzwi", "doorsCount");
            put("Minimalna masa własna pojazdu (bez obciążenia)", "baseMass");
            put("Maksymalna masa całkowita pojazdu (w pełni obciążonego)", "fullMass");
            put("Rodzaj skrzyni", "gearboxType");
            put("Liczba miejsc", "seatsCount");
            put("Średnica zawracania", "turningDiameter");
            put("Promień skrętu", "turningRadius");
            put("Maksymalna pojemność bagażnika (siedzenia złożone)", "maxTrunkCapacityWithSeatsFolded");
            put("Minimalna pojemność bagażnika (siedzenia rozłożone)", "minTrunkCapacityWithSeatsUnfolded");
            put("Odległość od oparcia fotela przedniego od kierownicy", "distanceFromFrontSeatBackrestToSteeringWheel");
            put("Odległość od siedzenia przedniego do dachu", "distanceFromFrontSeatToRoof");
            put("Długość siedzenia przedniego", "frontSeatLength");
            put("Odległość od siedzenia tylnego do dachu", "distanceFromRearSeatToRoof");
            put("Długość siedzenia tylnego", "rearSeatLength");
            put("Całkowita długość wnętrza kabiny", "totalInteriorLength");
            put("Całkowita wysokość wnętrza kabiny", "totalInteriorHeight");
            put("Szerokość pomiędzy nadkolami", "widthBetweenWheelArches");
            put("Długość z hakiem holowniczym", "lengthWithTowingHook");
            put("Zwis przedni", "frontOverhang");
            put("Zwis tylny", "rearOverhang");
            put("Szerokość na wysokości podłokietników z tyłu", "widthAtRearArmrestHeight");
            put("Dopuszczalne obciążenie dachu", "roofLoadCapacity");
            put("Produkowany", "produced");
            put("Pojemność skokowa", "engineDisplacement");
            put("Typ silnika", "engineType");
            put("Moc silnika", "enginePower");
            put("Maksymalny moment obrotowy", "maximumTorque");
            put("Montaż silnika", "engineMounting");
            put("Umiejscowienie wałka rozrządu", "camshaftPosition");
            put("Liczba cylindrów", "numberOfCylinders");
            put("Układ cylindrów", "cylinderArrangement");
            put("Liczba zaworów", "numberOfValves");
            put("Stopień sprężania", "compressionRatio");
            put("Zapłon", "ignition");
            put("Typ wtrysku", "injectionType");
            put("Rodzaj układu kierowniczego", "steeringSystemType");
            put("Opony podstawowe", "standardTires");
            put("Opony opcjonalne", "optionalTires");
            put("Rozstaw śrub", "boltPattern");
            put("Rodzaj hamulców (przód)", "frontBrakesType");
            put("Rodzaj hamulców (tył)", "rearBrakesType");
            put("Rodzaj zawieszenia (przód)", "frontSuspensionType");
            put("Rodzaj zawieszenia (tył)", "rearSuspensionType");
            put("Amortyzatory", "shockAbsorbers");
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

//    private List<String> getAllSubLinks(WebDriver driver) {
//        return getAllSubLinks(driver, new ArrayList<>());
//    }

    private List<String> getAllSubLinks(WebDriver driver) {

        //todo - rozwiazanie tymczasowe, ale nie wiem jak juz ogarnac te dupliakty...

        List<String> subLinks = getAllSubLinks(driver, new ArrayList<>());
        Set<String> uniqueSubLinks = new HashSet<>(subLinks);
        return new ArrayList<>(uniqueSubLinks);
    }

    private List<String> getAllSubLinks(WebDriver driver, List<String> list) {

        List<String> array = getSubcategories(driver);
        list.addAll(array);

        list.addAll(array.stream().map(link -> {
            driver.get(link);
            return getAllSubLinks(driver, list);
        }).flatMap(List::stream).toList());

        return list;
    }

    private List<String> getSubcategories(WebDriver driver) {
        System.out.println("getSubcategories");
        List<WebElement> carGenerations = driver.findElements(By.cssSelector(".car-selector-box-row a.car-selector-box"));
        return carGenerations.stream().map(carGeneration -> carGeneration.getAttribute("href")).toList();
    }

    private List<String> getEnginesLinks(WebDriver driver) {
        List<WebElement> carGenerations = driver.findElements(By.cssSelector(".engine-box a.engine-link"));
        return carGenerations.stream().map(carGeneration -> carGeneration.getAttribute("href")).toList();
    }

}

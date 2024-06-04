package it.letscode.autocentrum_scraper.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScraperService {

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
            driver.get("https://www.autocentrum.pl/auta/");

            WebElement selectElement = driver.findElement(By.cssSelector("select.select2"));

            Select select = new Select(selectElement);
            List<WebElement> optionsList = select.getOptions();

            for (WebElement option : optionsList) {
                String value = option.getAttribute("value");
                String dataUrl = option.getAttribute("data-url");
                String text = option.getText();

                System.out.println(value + ", " + (dataUrl != null ? dataUrl : "") + ", " + text);
            }
        } finally {
            driver.quit();
        }
    }
}

package it.letscode.autocentrum_scraper.car_model;

import it.letscode.autocentrum_scraper.scraper.ScraperCarModelDetailsService;
import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static it.letscode.autocentrum_scraper.scraper.ScraperService.setupWebDriver;

@RestController
@AllArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;
    private final ScraperCarModelDetailsService scraperCarModelDetailsService;

    @GetMapping
    public Page<CarModel> findAll(Pageable pageable,
                                  @RequestParam(required = false) String modelUrl,
                                  @RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return carModelService.findByNameContainingAllWords(query, pageable);
        } else if (modelUrl != null && !modelUrl.isEmpty()) {
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

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=car_models.xlsx");

        List<CarModel> carModels = carModelService.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Car Models");

        Set<String> attributeDescriptions = new HashSet<>();
        for (CarModel carModel : carModels) {
            for (Attribute attribute : carModel.getAttributes()) {
                attributeDescriptions.add(attribute.getDescription());
            }
        }

        Row headerRow = sheet.createRow(0);
        int colNum = 0;
        headerRow.createCell(colNum++).setCellValue("ID");
        headerRow.createCell(colNum++).setCellValue("Brand URL");
        headerRow.createCell(colNum++).setCellValue("Model URL");
        headerRow.createCell(colNum++).setCellValue("Full Model URL");
        headerRow.createCell(colNum++).setCellValue("Title");
        headerRow.createCell(colNum++).setCellValue("Name");
        headerRow.createCell(colNum++).setCellValue("Gearbox");

        Map<String, Integer> attributeColumnMap = new HashMap<>();
        for (String description : attributeDescriptions) {
            attributeColumnMap.put(description, colNum);
            headerRow.createCell(colNum++).setCellValue(description);
        }

        int rowNum = 1;
        for (CarModel carModel : carModels) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            row.createCell(colNum++).setCellValue(carModel.getId());
            row.createCell(colNum++).setCellValue(carModel.getBrandUrl());
            row.createCell(colNum++).setCellValue(carModel.getModelUrl());
            row.createCell(colNum++).setCellValue(carModel.getFullModelUrl());
            row.createCell(colNum++).setCellValue(carModel.getTitle());
            row.createCell(colNum++).setCellValue(carModel.getName());
            row.createCell(colNum++).setCellValue(carModel.getGearbox());

            for (Attribute attribute : carModel.getAttributes()) {
                Integer attributeColNum = attributeColumnMap.get(attribute.getDescription());
                if (attributeColNum != null) {
                    row.createCell(attributeColNum).setCellValue(attribute.getValue());
                }
            }
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

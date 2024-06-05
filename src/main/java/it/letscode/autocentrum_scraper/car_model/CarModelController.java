package it.letscode.autocentrum_scraper.car_model;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;

    @GetMapping
    public Page<CarModel> findAll(Pageable pageable) {
        return carModelService.findAll(pageable);
    }
}
package it.letscode.autocentrum_scraper.car_model;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;

    public Page<CarModel> findAll(Pageable pageable) {
        return carModelRepository.findAll(pageable);
    }
}

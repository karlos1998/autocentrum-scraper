package it.letscode.autocentrum_scraper.car_model;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;

    public Page<CarModel> findAll(Pageable pageable) {
        return carModelRepository.findAll(pageable);
    }

    public Page<CarModel> findByModelUrlStartingWith(String modelUrl, Pageable pageable) {
        return carModelRepository.findByModelUrlStartingWith(modelUrl, pageable);
    }

    public Page<CarModel> findByNameContainingAllWords(String name, Pageable pageable) {
        String processedName = name.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String regex = Stream.of(processedName.split("\\s+"))
                .map(Pattern::quote)
                .collect(Collectors.joining(".*", ".*", ".*"));
        return carModelRepository.findByNameContainingIgnoreCase(regex, pageable);
    }

    public List<CarModel> findAll() {
        return carModelRepository.findAll();
    }
}

package it.letscode.autocentrum_scraper.car_model;

import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
public class CarModel {

    @Id
    private String Id;

    private String brandUrl;

    private String modelUrl;

    private String fullModelUrl;

    private String imageUrl;

    private String title;

    private String name;

    private List<Attribute> attributes;

    private String gearbox;
}

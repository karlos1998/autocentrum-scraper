package it.letscode.autocentrum_scraper.car_model;

import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CarModel {

    private String Id;

    private String brandUrl;

    private String modelUrl;

    private String imageUrl;

    private String title;

    private String name;

    private List<Attribute> attributes;
}

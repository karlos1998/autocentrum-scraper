package it.letscode.autocentrum_scraper.model;

import it.letscode.autocentrum_scraper.brand.interfaces.Attribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Model {
    private String brandUrl;

    private String modelUrl;

    private String imageUrl;

    private String title;

    private String name;

    private List<Attribute> attributes;
}

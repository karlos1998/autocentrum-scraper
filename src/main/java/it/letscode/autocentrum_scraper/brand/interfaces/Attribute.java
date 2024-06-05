package it.letscode.autocentrum_scraper.brand.interfaces;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attribute {
    private String name;
    private String value;
    private String description;
}

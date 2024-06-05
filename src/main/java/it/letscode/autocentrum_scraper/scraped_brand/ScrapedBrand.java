package it.letscode.autocentrum_scraper.scraped_brand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class ScrapedBrand {
    @Id
    private String id;

    private String name;

    public ScrapedBrand(String name) {
        setName(name);
    }
}

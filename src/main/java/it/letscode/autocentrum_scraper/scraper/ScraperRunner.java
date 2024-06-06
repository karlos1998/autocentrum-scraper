package it.letscode.autocentrum_scraper.scraper;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class ScraperRunner implements CommandLineRunner {

    private final ScraperService scraperService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "runScraper".equals(args[0])) {
            scraperService.run();
        } else {
            System.out.println("No valid argument provided. Scraper will not run.");
        }
    }
}

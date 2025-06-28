package liekevk.pokedexroute.Datasource.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperties {

    private Properties properties = new Properties();

    public DatabaseProperties() {
        try (InputStream input = DatabaseProperties.class.getClassLoader().getResourceAsStream("application-dev.properties")) {
            if (input == null) {
                System.out.println("Sorry, not my problem you mis a file");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String connectionString() {
        return properties.getProperty("spring.datasource.url") + ";user=" + properties.getProperty("spring.datasource.username") + ";password=" + properties.getProperty("spring.datasource.password");
    }
}

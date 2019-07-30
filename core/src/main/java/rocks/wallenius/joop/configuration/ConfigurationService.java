package rocks.wallenius.joop.configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by philipwallenius on 18/02/16.
 */
public class ConfigurationService {

    private Properties properties;
    private final static String CONFIGURATION_FILE = "/configuration.properties";
    private static ConfigurationService instance;

    private ConfigurationService() {
        properties = new Properties();
        URL url = this.getClass().getResource(CONFIGURATION_FILE);
        try {
            properties.load(Files.newBufferedReader(Paths.get(url.toURI())));
        } catch(URISyntaxException | IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static ConfigurationService getInstance() {
        if(instance == null) {
            instance = new ConfigurationService();
        }
        return instance;
    }

    public String getString(String property) {
        return properties.getProperty(property);
    }

}

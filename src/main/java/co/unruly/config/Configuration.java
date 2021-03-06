package co.unruly.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static co.unruly.config.ConfigurationSource.FIND_NOTHING;

public class Configuration {

    private final ConfigurationSource func;

    public Configuration() {
        this(FIND_NOTHING);
    }

    public Configuration(ConfigurationSource map) {
        this.func = map;
    }

    public Optional<String> get(String s) {
        return Optional.ofNullable(func.get(s));
    }

    public String get(String s, String defaultValue) {
        return get(s).orElse(defaultValue);
    }

    public String require(String s) {
        return get(s).orElseThrow(() -> new ConfigurationMissing(s));
    }

    public Configuration or(ConfigurationSource next) {
        return new Configuration(this.func.or(next));
    }
    
    public Configuration prefixed(String prefix) {
        return new Configuration((key) -> this.func.get(prefix==null ? key : prefix + "." + key)).or(this.func);
    }
    
    public Configuration chained(ConfigurationSource keySource) {
        return new Configuration((key) -> Optional.ofNullable(keySource.get(key)).map((newKey) -> this.func.get(newKey)).orElse(null));
    }

    public static Configuration from(ConfigurationSource func) {
        return new Configuration(func);
    }

    public static Configuration of(ConfigurationSource... sources) {
        return new Configuration(Stream.of(sources)
                                       .reduce(FIND_NOTHING, ConfigurationSource::or));
    }

    public static ConfigurationSource map(Map<String, String> map){
        return map::get;
    }

    public static ConfigurationSource properties(String s) {
        Properties properties = new Properties();

        try {
            properties.load(new FileReader(s));
        } catch (IOException e) {}

        return properties::getProperty;
    }
    public static ConfigurationSource properties(Properties properties) {
        return properties::getProperty;
    }

    public static ConfigurationSource systemProperties() {
        return System::getProperty;
    }

    public static ConfigurationSource environment() {
        return (key) -> System.getenv(key.toUpperCase());
    }

    public static ConfigurationSource secretsManagerAWS(String secretName, String region) {
        return new SecretsManagerAWS(secretName, region)::get;
    }

    public static ConfigurationSource secretsManagerAWS(String secretName, String region, AWSSecretsManager client) {
         return new SecretsManagerAWS(secretName, region, client)::get;
    }
    
    public static ConfigurationSource secretsManagerGCE() throws IOException {
        return new SecretsManagerGCE();
    }
    
}


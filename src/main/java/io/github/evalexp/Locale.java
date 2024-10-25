package io.github.evalexp;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Locale object for text getting
 */
public class Locale {
    private HashMap<String, Locale> locales;

    /**
     * create the locale instance with specific language and search paths
     * @param language target language
     * @param searchPaths language file paths
     */
    public Locale(String language, List<String> searchPaths) {
        for (String languageSearchPath : searchPaths) {
            InputStream inputStream = Context.class.getResourceAsStream(String.format("/%s/%s.yml", languageSearchPath, language));
            try {
                this.locales = new Yaml().load(inputStream);
                break;
            } catch (Exception e) {}
        }
    }

    /**
     * test if load success
     * @return if load success
     */
    public boolean isLoaded() {
        return locales != null;
    }

    /**
     * get target i18n text
     * @param key text key
     * @return i18n text
     */
    public String text(String key) {
        String[] keys = key.split("\\.");
        Object value = this.locales;
        for (String k : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(k);
            } else {
                return null;
            }
        }
        return value != null ? value.toString() : null;
    }
}

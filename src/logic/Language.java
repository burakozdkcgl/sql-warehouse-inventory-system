package logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;

public class Language {
    private static final Map<String, String> translations = new HashMap<>();
    private static final List<Runnable> listeners = new ArrayList<>();
    private static String currentLanguage = "en";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void load(String code) {
        translations.clear();
        currentLanguage = code;

        try (InputStream is = Language.class.getClassLoader().getResourceAsStream("lang_" + code + ".json")) {
            if (is == null) {
                System.err.println("Language file not found: lang_" + code + ".json");
                return;
            }

            JsonNode root = mapper.readTree(is);
            flatten("", root);
            notifyListeners();
        } catch (Exception e) {
            System.err.println("Failed to load language: " + code);
            e.printStackTrace();
        }
    }

    private static void flatten(String prefix, JsonNode node) {
        node.fields().forEachRemaining(entry -> {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue().isObject()) {
                flatten(key, entry.getValue());
            } else {
                translations.put(key, entry.getValue().asText());
            }
        });
    }

    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static void addListener(Runnable r) {
        listeners.add(r);
    }

    private static void notifyListeners() {
        for (Runnable r : listeners) {
            r.run();
        }
    }
}

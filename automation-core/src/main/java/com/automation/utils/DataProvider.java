package com.automation.utils;

import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.models.Bug;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading test data from external sources (JSON, Excel).
 *
 * This class demonstrates the data-driven testing approach, where test data
 * is separated from test logic, making tests more maintainable and scalable.
 *
 * @author Rom
 * @version 1.0
 */
public class DataProvider {

    private static final Logger log = LogManager.getLogger(DataProvider.class);

    /**
     * Private constructor to prevent instantiation (Utility class).
     */
    private DataProvider() {
        throw new UnsupportedOperationException("Utility class- don't instantiate");
    }

    /**
     * Loads bug test data from a JSON file.
     * @param jsonFileName Name of the JSON file in classpath (e.g., "testdata/bugs.json")
     * @return List of Bug objects
     */
    public static List<Bug> loadBugsFromJson(String jsonFileName) {
        log.info("Loading bug data from JSON: {}", jsonFileName);

        try (InputStream inputStream = DataProvider.class.getClassLoader()
                .getResourceAsStream(jsonFileName)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("JSON file not found: " + jsonFileName);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            // Read JSON as list of maps
            List<Map<String, Object>> bugMaps = objectMapper.readValue(
                    inputStream, new TypeReference<List<Map<String, Object>>>() {});

            // Convert maps to Bug objects
            List<Bug> bugs = new ArrayList<>();
            for (Map<String, Object> bugMap : bugMaps) {
                Bug bug = mapToBug(bugMap);
                bugs.add(bug);
            }

            log.info("Loaded {} bugs from JSON", bugs.size());
            return bugs;

        } catch (Exception e) {
            log.error("Failed to load bugs from JSON: {}", jsonFileName, e);
            throw new RuntimeException("Failed to load test data from: " + jsonFileName, e);
        }
    }

    /**
     * Loads a single bug from JSON file by index.
     * @param jsonFileName Name of the JSON file
     * @param index of the bug to load
     * @return The Bug object that fit the index
     */
    public static Bug loadBugFromJson(String jsonFileName, int index) {
        List<Bug> bugs = loadBugsFromJson(jsonFileName);

        if (index < 0 || index >= bugs.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %d out of bounds for bug list of size %d",
                            index, bugs.size()));
        }
        return bugs.get(index);
    }

    /**
     * Converts a Map to a Bug object.
     * @param bugMap Map containing bug data
     * @return Bug object
     */
    private static Bug mapToBug(Map<String, Object> bugMap) {
        Bug.Builder builder = new Bug.Builder();

        if (bugMap.containsKey("bugId")) {
            builder.bugId(getInteger(bugMap, "bugId"));
        }

        if (bugMap.containsKey("date")) {
            builder.date(getString(bugMap, "date"));
        }

        if (bugMap.containsKey("title")) {
            builder.title(getString(bugMap, "title"));
        }

        if (bugMap.containsKey("stepsToReproduce")) {
            builder.stepsToReproduce(getString(bugMap, "stepsToReproduce"));
        }

        if (bugMap.containsKey("expectedResult")) {
            builder.expectedResult(getString(bugMap, "expectedResult"));
        }

        if (bugMap.containsKey("actualResult")) {
            builder.actualResult(getString(bugMap, "actualResult"));
        }

        if (bugMap.containsKey("status")) {
            String statusStr = getString(bugMap, "status");
            BugStatus status = BugStatus.fromDisplayName(statusStr);
            if (status != null) {
                builder.status(status);
            }
        }

        if (bugMap.containsKey("severity")) {
            String severityStr = getString(bugMap, "severity");
            BugSeverity severity = BugSeverity.fromDisplayName(severityStr);
            if (severity != null) {
                builder.severity(severity);
            }
        }

        if (bugMap.containsKey("priority")) {
            String priorityStr = getString(bugMap, "priority");
            BugPriority priority = BugPriority.fromDisplayName(priorityStr);
            if (priority != null) {
                builder.priority(priority);
            }
        }

        if (bugMap.containsKey("detectedBy")) {
            builder.detectedBy(getString(bugMap, "detectedBy"));
        }

        if (bugMap.containsKey("fixedBy")) {
            builder.fixedBy(getString(bugMap, "fixedBy"));
        }

        if (bugMap.containsKey("dateClosed")) {
            builder.dateClosed(getString(bugMap, "dateClosed"));
        }

        if (bugMap.containsKey("attachedFile")) {
            builder.attachedFile(getString(bugMap, "attachedFile"));
        }
        return builder.build();
    }

    /**
     * Helper method to safely get a string value from a map.
     * @param map Map containing the value
     * @param key Key to retrieve
     * @return String value, or null if not present
     */
    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Helper method to safely get an integer value from a map.
     *
     * @param map Map containing the value
     * @param key Key to retrieve
     * @return Integer value, or null if not present
     */
    private static Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Could not parse integer for key '{}': {}", key, value);
            return null;
        }
    }
}

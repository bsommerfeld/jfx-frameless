package de.bsommerfeld.jfx.frameless.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Loads SVG path data from resource files.
 * Caches results for performance.
 */
public final class SvgLoader {

    private static final Map<String, String> CACHE = new HashMap<>();
    private static final Pattern PATH_PATTERN = Pattern.compile("d=\"([^\"]+)\"");

    private SvgLoader() {
    }

    /**
     * Loads SVG path data from a resource.
     *
     * @param resourcePath Resource path (e.g., "/icons/macos-close.svg")
     * @return The path data string, or empty string if not found
     */
    public static String load(String resourcePath) {
        if (CACHE.containsKey(resourcePath)) {
            return CACHE.get(resourcePath);
        }

        try (InputStream is = SvgLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return "";
            }

            String svgContent = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            String pathData = extractPathData(svgContent);
            String result = pathData != null ? pathData : "";
            CACHE.put(resourcePath, result);
            return result;

        } catch (Exception e) {
            return "";
        }
    }

    private static String extractPathData(String svgContent) {
        Matcher matcher = PATH_PATTERN.matcher(svgContent);
        return matcher.find() ? matcher.group(1) : null;
    }
}

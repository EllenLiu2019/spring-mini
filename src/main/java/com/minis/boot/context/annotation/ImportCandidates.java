package com.minis.boot.context.annotation;

import com.minis.core.io.UrlResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public final class ImportCandidates {

    private static final String LOCATION = "META-INF/spring/%s.imports";

    private static final String COMMENT_START = "#";

    private final List<String> candidates;

    private ImportCandidates(List<String> candidates) {
        this.candidates = Collections.unmodifiableList(candidates);
    }

    public List<String> getCandidates() {
        return this.candidates;
    }

    public static ImportCandidates load(Class<?> annotation, ClassLoader classLoader) {
        ClassLoader classLoaderToUse = decideClassloader(classLoader);
        String location = String.format(LOCATION, annotation.getName());
        Enumeration<URL> urls = findUrlsInClasspath(classLoaderToUse, location);
        List<String> importCandidates = new ArrayList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            importCandidates.addAll(readCandidateConfigurations(url));
        }
        return new ImportCandidates(importCandidates);
    }

    private static ClassLoader decideClassloader(ClassLoader classLoader) {
        if (classLoader == null) {
            return ImportCandidates.class.getClassLoader();
        }
        return classLoader;
    }

    private static Enumeration<URL> findUrlsInClasspath(ClassLoader classLoader, String location) {
        try {
            return classLoader.getResources(location);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load configurations from location [" + location + "]", ex);
        }
    }

    private static List<String> readCandidateConfigurations(URL url) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new UrlResource(url).getInputStream(), StandardCharsets.UTF_8))) {
            List<String> candidates = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = stripComment(line);
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                candidates.add(line);
            }
            return candidates;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load configurations from location [" + url + "]", ex);
        }
    }

    private static String stripComment(String line) {
        int commentStart = line.indexOf(COMMENT_START);
        if (commentStart == -1) {
            return line;
        }
        return line.substring(0, commentStart);
    }
}

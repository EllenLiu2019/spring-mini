package com.minis.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class WebUtils {
    public static Map<Integer, Pair<String, Object>> getParametersStartingWith(HttpServletRequest request, String prefix) {
        String method = request.getMethod();
        Map<Integer, Pair<String, Object>> result = new TreeMap<>();
        int index = 0;
        if ("GET".equalsIgnoreCase(method)) {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames != null && paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (prefix.isEmpty() || paramName.startsWith(prefix)) {
                    String unprefixed = paramName.substring(prefix.length());
                    String value = request.getParameter(paramName);

                    result.put(index++, Pair.of(unprefixed, value));
                }
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            result = getBody(request);
        }
        return result;
    }

    public static Map<Integer, Pair<String, Object>> getBody(HttpServletRequest request) {
        Map<Integer, Pair<String, Object>> result = new TreeMap<>();
        int index = 0;
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("\"", "")
                        .replaceAll("\\{", "")
                        .replaceAll("}", "")
                        .replace(",", "");

                if (!StringUtils.isEmpty(line)) {
                    String[] split = line.split(":");
                    result.put(index++, Pair.of(split[0].trim(), split[1].trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}

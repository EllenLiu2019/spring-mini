package com.minis.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class WebUtils {
    public static Map<String, Object> getParametersStartingWith(HttpServletRequest request, String prefix) {
        String method = request.getMethod();
        Map<String, Object> params = new TreeMap<>();
        if ("GET".equalsIgnoreCase(method)) {
            Enumeration<String> paramNames = request.getParameterNames();
            if (prefix == null) {
                prefix = "";
            }
            while (paramNames != null && paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (prefix.isEmpty() || paramName.startsWith(prefix)) {
                    String unprefixed = paramName.substring(prefix.length());
                    String value = request.getParameter(paramName);

                    params.put(unprefixed, value);
                }
            }
        } else if ("POST".equalsIgnoreCase(method)) {
             params = getBody(request);
        }
        return params;
    }

    public static Map<String, Object> getBody(HttpServletRequest request) {
        Map<String, Object> params = new TreeMap<>();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("\"", "")
                        .replaceAll("\\{", "")
                        .replaceAll("}", "")
                        .replace(",", "");

                if (!StringUtils.isEmpty(line)) {
                    String[] split = line.split(":");
                    params.put(split[0].trim(), split[1].trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return params;
    }

}

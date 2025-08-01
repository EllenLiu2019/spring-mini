package com.minis.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesLoaderUtils {
    private static final String XML_FILE_EXTENSION = ".xml";
    public static Properties loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    public static void fillProperties(Properties props, Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                props.loadFromXML(is);
            }
            else {
                props.load(is);
            }
        }
    }
}

package com.minis.core.env;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Map;

@Slf4j
public class SystemEnvironmentPropertySource extends MapPropertySource {

    public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    public Object getProperty(String name) {
        String actualName = resolvePropertyName(name);
        log.debug("PropertySource '" + getName() + "' does not contain property '" + name +
                "', but found equivalent '" + actualName + "'");
        return super.getProperty(actualName);
    }

    protected final String resolvePropertyName(String name) {
        String resolvedName = checkPropertyName(name);
        if (resolvedName != null) {
            return resolvedName;
        }
        String uppercasedName = name.toUpperCase(Locale.ROOT);
        if (!name.equals(uppercasedName)) {
            resolvedName = checkPropertyName(uppercasedName);
            if (resolvedName != null) {
                return resolvedName;
            }
        }
        return name;
    }

    private String checkPropertyName(String name) {
        // Check name as-is
        if (this.source.containsKey(name)) {
            return name;
        }
        // Check name with just dots replaced
        String noDotName = name.replace('.', '_');
        if (!name.equals(noDotName) && this.source.containsKey(noDotName)) {
            return noDotName;
        }
        // Check name with just hyphens replaced
        String noHyphenName = name.replace('-', '_');
        if (!name.equals(noHyphenName) && this.source.containsKey(noHyphenName)) {
            return noHyphenName;
        }
        // Check name with dots and hyphens replaced
        String noDotNoHyphenName = noDotName.replace('-', '_');
        if (!noDotName.equals(noDotNoHyphenName) && this.source.containsKey(noDotNoHyphenName)) {
            return noDotNoHyphenName;
        }
        // Give up
        return null;
    }

}

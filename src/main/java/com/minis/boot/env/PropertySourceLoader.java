package com.minis.boot.env;

import java.io.IOException;
import java.util.List;

import com.minis.core.env.PropertySource;

public interface PropertySourceLoader {

    List<PropertySource<?>> load() throws IOException;
}

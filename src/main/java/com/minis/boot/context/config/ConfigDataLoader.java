package com.minis.boot.context.config;

import java.io.IOException;

public interface ConfigDataLoader {

    ConfigData load() throws IOException;
}

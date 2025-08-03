package com.minis.core.io;

import com.minis.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResource extends AbstractFileResolvingResource {

    private final String path;

    private final String absolutePath;

    public ClassPathResource(String path) {
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.absolutePath = pathToUse;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.absolutePath);
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override
    public String getDescription() {
        return "class path resource [" + this.absolutePath + "]";
    }
}

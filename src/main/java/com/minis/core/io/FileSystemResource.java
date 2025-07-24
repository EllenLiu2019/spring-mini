package com.minis.core.io;

import com.minis.utils.ResourceUtils;
import com.minis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Slf4j
public class FileSystemResource implements Resource {

    private final String path;

    private final File file;

    private final Path filePath;


    public FileSystemResource(String path) {
        this.path = StringUtils.cleanPath(path);
        this.file = new File(path);
        this.filePath = this.file.toPath();
    }

    public FileSystemResource(Path filePath) {
        this.path = StringUtils.cleanPath(filePath.toString());
        this.file = null;
        this.filePath = filePath;
    }

    public final String getPath() {
        return this.path;
    }

    public boolean isReadable() {
        return (this.file != null ? this.file.canRead() && !this.file.isDirectory() :
                Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath));
    }

    public boolean isWritable() {
        return (this.file != null ? this.file.canWrite() && !this.file.isDirectory() :
                Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return Files.newInputStream(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public boolean exists() {
        if (isFile()) {
            try {
                return getFile().exists();
            } catch (IOException ex) {
                log.error("Could not retrieve File for existence check of " + getDescription(), ex);
            }
        }
        // Fall back to stream existence: can we open the stream?
        try {
            getInputStream().close();
            return true;
        } catch (Throwable ex) {
            log.error("Could not retrieve InputStream for existence check of " + getDescription(), ex);
            return false;
        }
    }

    @Override
    public URL getURL() throws IOException {
        return this.file.exists() ? this.file.toURI().toURL() : ResourceUtils.getURL(this.path);
    }

    @Override
    public URI getURI() throws IOException {
        return this.file.toURI();
    }

    @Override
    public File getFile() throws IOException {
        return (this.file != null ? this.file : this.filePath.toFile());
    }

    @Override
    public long contentLength() throws IOException {
        long length = this.file.length();
        if (length == 0L && !this.file.exists()) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for checking its content length");
        }
        return length;
    }

    @Override
    public long lastModified() throws IOException {
        return 0;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return null;
    }

    @Override
    public String getFilename() {
        return (this.file != null ? this.file.getName() : this.filePath.getFileName().toString());
    }

    @Override
    public String getDescription() {
        return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
    }
}

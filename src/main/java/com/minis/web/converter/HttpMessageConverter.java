package com.minis.web.converter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface HttpMessageConverter {
    void write(Object obj, HttpServletResponse response) throws IOException;
}

package com.minis.web.converter;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.utils.DefaultObjectMapper;
import com.minis.utils.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class DefaultHttpMessageConverter implements HttpMessageConverter {
    String defaultContentType = "text/json;charset=UTF-8";
    String defaultCharacterEncoding = "UTF-8";

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void write(Object obj, HttpServletResponse response) throws IOException {
        response.setContentType(defaultContentType);
        response.setCharacterEncoding(defaultCharacterEncoding);
        writeInternal(obj, response);
        response.flushBuffer();
    }

    private void writeInternal(Object obj, HttpServletResponse response) throws IOException {
        String sJsonStr = this.objectMapper.writeValuesAsString(obj);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(sJsonStr);
    }

}

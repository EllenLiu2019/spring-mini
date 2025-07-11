package com.minis.web.context.support;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XmlScanComponentHelper {
    public static List<String> getNodeValue(URL xmlPath) {
        List<String> packages = new ArrayList<>();
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(xmlPath);
        } catch (Exception e) {
            log.error("failed to read info from file {} ", xmlPath);
            throw new RuntimeException(e);
        }
        assert document != null;
        Element root = document.getRootElement();
        List<Element> elements = root.elements("component-scan");
        for (Element element : elements) {
            packages.add(element.attributeValue("base-package"));
        }
        return packages;
    }
}

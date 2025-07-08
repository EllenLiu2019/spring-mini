package com.minis.web;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XmlScanComponentHelper {
    public static List<String> getNodeValue(URL xmlPath) {
        List<String> packages = new ArrayList<>();
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(xmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert document != null;
        Element rootElement = document.getRootElement();
        for (Element element : rootElement.elements()) {
            packages.add(element.attributeValue("base-package"));
        }
        return packages;
    }
}

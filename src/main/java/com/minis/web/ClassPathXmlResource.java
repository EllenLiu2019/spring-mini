package com.minis.web;

import java.net.URL;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ClassPathXmlResource implements Resource {
    private Document document;
    private Element rootElement;
    private Iterator<Element> elementIterator;

    public ClassPathXmlResource(URL xmlPath) {
        SAXReader saxReader=new SAXReader();
        try {
            this.document = saxReader.read(xmlPath);
            this.rootElement=document.getRootElement();
            this.elementIterator=this.rootElement.elementIterator();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean hasNext() {
        return this.elementIterator.hasNext();
    }
    @Override
    public Object next() {
        return this.elementIterator.next();
    }
}
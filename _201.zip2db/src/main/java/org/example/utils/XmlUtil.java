package org.example.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {

    /*public static String parseXmlString(String xmlStr) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlStr));
        Document doc = builder.parse(is);
        return doc.getDocumentElement().getTextContent().trim();
    }*/

    public List<Map> parseXmlString(String xmlStr) throws Exception {
        List<Map> resList = new ArrayList<Map>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlStr));
        Document doc = builder.parse(is);
        NodeList rows = doc.getDocumentElement().getElementsByTagName("row");

        for (int i = 0; i < rows.getLength(); i++) {
            HashMap<String, Object> dataMap = new HashMap<>();
            Node row = rows.item(i);
            NodeList childNodes = row.getChildNodes();
            for (int i1 = 0; i1 < childNodes.getLength(); i1++) {
                Node child = childNodes.item(i1);
                if (child.getNodeType() == Node.ELEMENT_NODE) { // 过滤非元素节点
                    dataMap.put(child.getNodeName(), child.getTextContent());
                    // System.out.println(child.getNodeName() + " " + child.getTextContent());
                }
            }
            // System.out.println("=============================================");
            resList.add(dataMap);
        }


        return resList;
    }

    public Map<String, String> parseXmlHeader(String xmlStr) throws Exception {
        Map<String, String> headerMap = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlStr));
        Document doc = builder.parse(is);

        // 获取 XML 文档的根节点及其子节点
        NodeList childNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("header")) {
                NodeList headerChildren = node.getChildNodes();
                for (int j = 0; j < headerChildren.getLength(); j++) {
                    Node headerChild = headerChildren.item(j);
                    if (headerChild.getNodeType() == Node.ELEMENT_NODE) {
                        headerMap.put(headerChild.getNodeName(), headerChild.getTextContent());
                    }
                }
            }
        }

        return headerMap;
    }
}

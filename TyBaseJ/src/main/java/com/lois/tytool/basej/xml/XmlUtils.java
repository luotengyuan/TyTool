package com.lois.tytool.basej.xml;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * XML文档工具
 * 集成常见的Xml文档操作工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class XmlUtils {
    private static String XML_ROOT_NAME = "ROOT";
    /**
     * xml转map 不带属性.
     * @param xmlStr xml文本
     * @param needRootKey 是否需要在返回的map里追加根节点
     * @return xml的字典信息
     */
    public static Map<String, Object> xmlToMap(final String xmlStr, final boolean needRootKey) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        Element root = doc.getRootElement();
        Map<String, Object> map = xmlToMap(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>(1);
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }
    /**
     * xml转map 不带属性.
     * @param xmlStr xml文本
     * @return xml的字典信息
     */
    public static Map<String, Object> xmlToMap(final String xmlStr) {
        return xmlToMap(xmlStr, false);
    }

    /**
     * 读取xml内容的文件转成map，不带属性，不返回根节点.
     * @param xmlFilePath xml文件路径
     * @return xml的字典信息
     */
    public static Map<String, Object> xmlFileToMap(final String xmlFilePath){
    	SAXReader reader = new SAXReader();
    	File file = new File(xmlFilePath);
        Document doc = null;
        try {
            doc = reader.read(file);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        Element root = doc.getRootElement();
        Map<String, Object> map = xmlToMap(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        return map;
    }

    /**
     * xml转map 带属性.节点属性带@开头，文本属性带#开头
     * @param xmlStr xml文本
     * @param needRootKey 是否需要在返回的map里追加根节点
     * @return 带属性的xml字典信息
     */
    public static Map<String, Object> xmlToMapWithAttr(final String xmlStr, final boolean needRootKey) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xmlToMapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>(1);
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }
    /**
     * xml转map 带属性. 节点属性带@开头，文本属性带#开头
     * @param xmlStr xml文本
     * @return 带属性的xml字典信息
     */
    public static Map<String, Object> xmlToMapWithAttr(final String xmlStr) {
        return xmlToMapWithAttr(xmlStr, false);
    }
    /**
     * xml转map 带属性，不返回根节点.节点属性带@开头，文本属性带#开头
     * @param xmlFilePath xml文件路径
     * @return 带属性的xml字典信息
     */
    public static Map<String, Object> xmlFileToMapWithAttr(final String xmlFilePath) {
    	SAXReader reader = new SAXReader();
    	File file = new File(xmlFilePath);
        Document doc = null;
        try {
            doc = reader.read(file);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        Element root = doc.getRootElement();
        Map<String, Object> map = xmlToMapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        return map;
    }
    /**
     * xml转map 带属性，不返回根节点.
     * @param inputStream xml文件流
     * @return 带属性的xml字典信息
     * @throws DocumentException 文档异常
     */
    public static Map<String, Object> xmlToMapWithAttr(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(inputStream);
        Element root = doc.getRootElement();
        Map<String, Object> map = xmlToMapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        return map;
    }

    /**
     * xml转map 不带属性.
     * @param e 根节点
     * @return xml的字典信息
     */
    private static Map<String, Object> xmlToMap(final Element e) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        List<Element> list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List<Object> mapList = new ArrayList<Object>();
                if (iter.elements().size() > 0) {
                    Map<String, Object> m = xmlToMap(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<Object>();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), m);
                    }
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<Object>();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), iter.getText());
                    }
                }
            }
        } else {
            map.put(e.getName(), e.getText());
        }
        return map;
    }
    /**
     * xml转map 带属性.
     * @param element 根节点
     * @return 带属性的xml字典信息
     */
    private static Map<String, Object> xmlToMapWithAttr(final Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes();
        // 当前节点的所有属性的list
        for (Attribute attr : listAttr0) {
            map.put("@" + attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List<Object> mapList = new ArrayList<Object>();
                if (iter.elements().size() > 0) {
                    Map<String, Object> m = xmlToMapWithAttr(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<Object>();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), m);
                    }
                } else {
                    List<Attribute> listAttr = iter.attributes();
                    // 当前节点的所有属性的list
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put("@" + attr.getName(), attr.getValue());
                        }
                    }
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<Object>();
                            mapList.add(obj);
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        if (hasAttributes) {
                            attrMap.put("#text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // 根节点的
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    /**
     * map转xml map中没有根节点的键.
     * @param map 字典信息
     * @param rootName 根节点信息
     * @return Document对象
     */
    public static String mapToXml(final Map<String, Object> map, final String rootName) {
        Document doc = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement(rootName);
        doc.add(root);
        mapToXml(map, root);
        return doc.asXML();
    }
    /**
     * map转xml map中含有根节点的键.
     * @param map 字典信息
     * @return Document对象
     */
    public static String mapToXml(final Map<String, Object> map) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        int size = map.size();
        //获取第一个键创建根节点
        if (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            Object value = entry.getValue();
            Document doc = DocumentHelper.createDocument();
            if (size == 1 && value instanceof Map) {
                Element root = DocumentHelper.createElement(entry.getKey());
                doc.add(root);
                mapToXml((Map<String, Object>) value, root);
            } else {
                Element root = DocumentHelper.createElement(XML_ROOT_NAME);
                doc.add(root);
                mapToXml(map, root);
            }
            return doc.asXML();
        }
        return null;
    }

    /**
     * map转xml.
     * @param map map字典信息
     * @param body 节点
     * @return xml节点对象
     */
    private static Element mapToXml(final Map<String, Object> map, final Element body) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue() == null? "":entry.getValue();

            if (key.startsWith("@")) {
                //属性
                body.addAttribute(key.substring(1, key.length()), String.valueOf(value));
            } else if ("#text".equals(key)) {
                //有属性时的文本
                body.setText(String.valueOf(value));
            } else {
                if (value instanceof List) {
                    List list = (List) value;
                    Object obj;
                    for (int i = 0; i < list.size(); i++) {
                        obj = list.get(i);
                        //list里是map或String，不会存在list里直接是list的
                        if (obj instanceof Map) {
                            Element subElement = body.addElement(key);
                            mapToXml((Map<String, Object>) list.get(i), subElement);
                        } else {
                            body.addElement(key).setText(String.valueOf(list.get(i)));
                        }
                    }
                } else if (value instanceof Map) {
                    Element subElement = body.addElement(key);
                    mapToXml((Map<String, Object>) value, subElement);
                } else {
                    body.addElement(key).setText(String.valueOf(value));
                }
            }
        }
        return body;
    }
    /**
     * 格式化输出xml文本.
     * @param xmlStr xml字符串信息
     * @return 格式化后的文本
     */
    public static String formatXml(final String xmlStr) {
        Document document = null;
        try {
            document = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        return formatXml(document);
    }
    /**
     * 格式化输出xml.
     * @param document documetn对象
     * @return 格式化后的文本
     */
    public static String formatXml(final Document document) {
        // 格式化输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter writer = new StringWriter();
        // 格式化输出流
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        // 将document写入到输出流
        try {
            xmlWriter.write(document);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            try {
                if (xmlWriter != null) {
                    xmlWriter.close();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return writer.toString();
    }
}



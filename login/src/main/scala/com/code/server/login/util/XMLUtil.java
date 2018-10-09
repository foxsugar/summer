package com.code.server.login.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FirenzesEagle on 2016/7/7 0007.
 * Email:liumingbo2008@gmail.com
 */
public class XMLUtil {

    private Logger logger = LoggerFactory.getLogger(XMLUtil.class);
    /**
     * 将微信服务器发送的Request请求中Body的XML解析为Map
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseRequestXmlToMap(HttpServletRequest request) throws Exception {
        // 解析结果存储在HashMap中
        Map<String, String> resultMap;
        InputStream inputStream = request.getInputStream();
        resultMap = parseInputStreamToMap(inputStream);
        return resultMap;
    }

    /**
     * 将输入流中的XML解析为Map
     *
     * @param inputStream
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static Map<String, String> parseInputStreamToMap(InputStream inputStream) throws DocumentException, IOException, SAXException {
        // 解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 读取输入流
        SAXReader reader = new SAXReader();
        //fix 微信支付 XXE漏洞
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = reader.read(inputStream);
        //得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        //遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        //释放资源
        inputStream.close();
        return map;
    }


    public static Map<String,String> parseInputStreamToMapSafe(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> map = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        try {
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // Disable external DTDs as well
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            // And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."

            // remaining parser logic
        } catch (ParserConfigurationException e) {
            // This should catch a failed setFeature feature
//            logger.info("ParserConfigurationException was thrown. The feature '" +
//                    FEATURE + "' is probably not supported by your XML processor.");
        }
        DocumentBuilder safebuilder = dbf.newDocumentBuilder();


        org.w3c.dom.Document document = safebuilder.parse(inputStream);
        //得到xml根元素
        org.w3c.dom.Element root = document.getDocumentElement();
        // 得到根元素的所有子节点
        NodeList elementList = root.getChildNodes();
        //遍历所有子节点
        for (int i=0;i<elementList.getLength();i++) {
            Node node = elementList.item(i);
            map.put(node.getNodeName(), node.getNodeValue());
        }
        //释放资源
        inputStream.close();
        return map;

    }

    /**
     * 将String类型的XML解析为Map
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXmlStringToMap(String str) throws Exception {
        Map<String, String> resultMap;
        InputStream inputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        resultMap = parseInputStreamToMap(inputStream);
        return resultMap;
    }

}

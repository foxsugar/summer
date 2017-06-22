package com.code.server.login.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PayUtil {
	
	
	public static String appid = "wxad87bc7722faff71";//应用id
	
	public static String mch_id = "1458783202";//商户号
	
	public static String Key = "Iiarg081390357682513935781706ldd";//API秘钥
	
	public static String notify_url = "http://ldlwy01.3jqp.com:8080/mj_web/callback";
	//public static String Key = "e7b177fc771846a94b95f8fb71d3648e";//沙箱秘钥


    public static String createSign(String characterEncoding,String key,SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();  
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）  
        Iterator it = es.iterator();  
        while(it.hasNext()) {  
            Map.Entry entry = (Map.Entry)it.next();  
            String k = (String)entry.getKey();  
            Object v = entry.getValue();  
            if(null != v && !"".equals(v)   
                    && !"sign".equals(k) && !"key".equals(k)) {  
                sb.append(k + "=" + v + "&");  
            }  
        }  
        sb.append("key=" + key);
        String s = "";
        		try {
			s = new String(sb.toString().getBytes(characterEncoding),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        String sign = MD5Util.MD5Encode(sb.toString(), "ISO-8859-1").toUpperCase();
        return sign;  
    }  
    
    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 创建唯一的订单号
     * @return
     */
    public static String getOrderIdByUUId() {
        int machineId = 1;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {//有可能是负数
            hashCodeV = - hashCodeV;
        }
        // 0 代表前面补充0     
        // 4 代表长度为4     
        // d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }
    
    
    
    public void readStringXml(String xml) {  
        Document doc = null;  
        try {  
    
            // 读取并解析XML文档  
            // SAXReader就是一个管道，用一个流的方式，把xml文件读出来  
            //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
            // SAXReader reader = new SAXReader(); //User.hbm.xml表示你要解析的xml文档  
            // Document document = reader.read(new File("User.hbm.xml"));  
            // 下面的是通过解析xml字符串的  
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML  
                                                                                                                            
            Element rootElt = doc.getRootElement(); // 获取根节点  
            System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称  
    
            Iterator iter = rootElt.elementIterator("head"); // 获取根节点下的子节点head  
    
            // 遍历head节点  
            while (iter.hasNext()) {  
    
                Element recordEle = (Element) iter.next();  
                String title = recordEle.elementTextTrim("title"); // 拿到head节点下的子节点title值  
                System.out.println("title:" + title);  
    
                Iterator iters = recordEle.elementIterator("script"); // 获取子节点head下的子节点script  
    
                // 遍历Header节点下的Response节点  
                while (iters.hasNext()) {  
    
                    Element itemEle = (Element) iters.next();  
    
                    String username = itemEle.elementTextTrim("username"); // 拿到head下的子节点script下的字节点username的值  
                    String password = itemEle.elementTextTrim("password");  
    
                    System.out.println("username:" + username);  
                    System.out.println("password:" + password);  
                }  
            }  
            Iterator iterss = rootElt.elementIterator("body"); ///获取根节点下的子节点body  
            // 遍历body节点  
            while (iterss.hasNext()) {  
    
                Element recordEless = (Element) iterss.next();  
                String result = recordEless.elementTextTrim("result"); // 拿到body节点下的子节点result值  
                System.out.println("result:" + result);  
    
                Iterator itersElIterator = recordEless.elementIterator("form"); // 获取子节点body下的子节点form  
                // 遍历Header节点下的Response节点  
                while (itersElIterator.hasNext()) {  
    
                    Element itemEle = (Element) itersElIterator.next();  
    
                    String banlce = itemEle.elementTextTrim("banlce"); // 拿到body下的子节点form下的字节点banlce的值  
                    String subID = itemEle.elementTextTrim("subID");  
    
                    System.out.println("banlce:" + banlce);  
                    System.out.println("subID:" + subID);  
                }  
            }  
        } catch (DocumentException e) {  
            e.printStackTrace();  
    
        } catch (Exception e) {  
            e.printStackTrace();  
    
        }  
    }  
    
    /** 
     * @description 将xml字符串转换成map 
     * @param xml 
     * @return Map 
     */  
    public static Map readStringXmlOut(String xml) {  
        Map map = new HashMap();  
        Document doc = null;  
        try {  
            // 将字符串转为XML  
            doc = DocumentHelper.parseText(xml);   
            // 获取根节点  
            Element rootElt = doc.getRootElement();   
            // 拿到根节点的名称  
            System.out.println("根节点：" + rootElt.getName());   
    
            // 获取根节点下的子节点head  
            Iterator iter = rootElt.elementIterator("head");   
            // 遍历head节点  
            while (iter.hasNext()) {  
    
                Element recordEle = (Element) iter.next();  
                // 拿到head节点下的子节点title值  
                String title = recordEle.elementTextTrim("title");   
                System.out.println("title:" + title);  
                map.put("title", title);  
                // 获取子节点head下的子节点script  
                Iterator iters = recordEle.elementIterator("script");   
                // 遍历Header节点下的Response节点  
                while (iters.hasNext()) {  
                    Element itemEle = (Element) iters.next();  
                    // 拿到head下的子节点script下的字节点username的值  
                    String username = itemEle.elementTextTrim("username");   
                    String password = itemEle.elementTextTrim("password");  
    
                    System.out.println("username:" + username);  
                    System.out.println("password:" + password);  
                    map.put("username", username);  
                    map.put("password", password);  
                }  
            }  
    
            //获取根节点下的子节点body  
            Iterator iterss = rootElt.elementIterator("body");   
            // 遍历body节点  
            while (iterss.hasNext()) {  
                Element recordEless = (Element) iterss.next();  
                // 拿到body节点下的子节点result值  
                String result = recordEless.elementTextTrim("result");   
                System.out.println("result:" + result);  
                // 获取子节点body下的子节点form  
                Iterator itersElIterator = recordEless.elementIterator("form");   
                // 遍历Header节点下的Response节点  
                while (itersElIterator.hasNext()) {  
                    Element itemEle = (Element) itersElIterator.next();  
                    // 拿到body下的子节点form下的字节点banlce的值  
                    String banlce = itemEle.elementTextTrim("banlce");   
                    String subID = itemEle.elementTextTrim("subID");  
    
                    System.out.println("banlce:" + banlce);  
                    System.out.println("subID:" + subID);  
                    map.put("result", result);  
                    map.put("banlce", banlce);  
                    map.put("subID", subID);  
                }  
            }  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return map;  
    } 
    
    
    /**
     * 获取返回信息
     * @param request
     * @return
     */
    public static String ObtainParameterString(HttpServletRequest request){
    	BufferedReader reader;
    	StringBuffer inputString = null;
    	String xmlString = null;
    	
		try {
			reader = request.getReader();
        String line = "";
         inputString = new StringBuffer();

			while ((line = reader.readLine()) != null) {
			    inputString.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return inputString.toString();
    }
    
    
    /**
     * 获取返回信息
     * @param request
     * @return
     */
    public static Map<String,String> ObtainParameterMap(HttpServletRequest request){
		Map<String,String> map = new HashMap<String,String>();
	   	 String[] array = ObtainParameterString(request).split("&");
	   	 for(String str : array){
	   		String[] arraystr = str.split("=");
	   		map.put(arraystr[0], arraystr[1]);
	   	 }
		
        return map;
    }
    
    /**
     * 解析xmlString
     * @param xmlPOST
     * @return
     */
    public static Element ParsingXML(String xmlPOST){
    	Element element = null;
    	try {  
            SAXReader reader = new SAXReader();  
             Document doc;   
             doc = DocumentHelper.parseText(xmlPOST);   
  
             element = doc.getRootElement();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		return element;
    }
    
    public static void main(String[] args) {
		
    	SortedMap<String,String> secondParams = new TreeMap<>();
    	
    	secondParams.put("appid", "wxad87bc7722faff71");
		secondParams.put("bank_type", "CFT");
		secondParams.put("cash_fee", "100");
		secondParams.put("fee_type", "CNY");
		secondParams.put("is_subscribe", "N");
		secondParams.put("mch_id", "1458783202");
		secondParams.put("nonce_str", "552w8oce3x7k337yg3e5acpa75s7amib");
		secondParams.put("openid", "o7UAf030CxyIMVMsukRIYlQT4JOk");
		secondParams.put("out_trade_no", "1000001950573514");
		secondParams.put("result_code", "SUCCESS");
		secondParams.put("return_code", "SUCCESS");
		secondParams.put("time_end", "20170425104847");
		secondParams.put("total_fee", "100");
		secondParams.put("trade_type", "APP");
		secondParams.put("transaction_id", "4003042001201704258268240754");
		
		
		//String paySign = PayUtil.createSign("UTF-8" , secondParams);
    	
    	//System.out.println(paySign);
    	
		System.out.println("1".equals(String.valueOf(Integer.valueOf("100")/100)));
		System.out.println(Integer.valueOf("100")/100);
		
    	
	}
}

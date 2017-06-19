package com.code.server.login.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;



public class TestGetPost {  
    /** 
     * 向指定URL发送GET方法的请求 
     * @param url  发送请求的URL 
     * @param param  请求参数，请求参数应该是name1=value1&name2=value2的形式 
     * @return URL所代表远程资源的响应 
     */  
  
    public static String sendGet(String url, String param) {  
        String result = "";  
        BufferedReader in = null;  
        try {  
            String urlName = url + "?" + param;  
            URL realUrl = new URL(urlName);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // 建立实际的连接  
            conn.connect();  
            // 获取所有响应头字段  
                                Map<String, List<String>> map = conn.getHeaderFields();  
            // 遍历所有的响应头字段  
            for (String key : map.keySet()) {  
                System.out.println(key + "--->" + map.get(key));  
            }  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } catch (Exception e) {  
            System.out.println("发送GET请求出现异常！" + e);  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输入流  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
  
/**  
     * 向指定URL发送POST方法的请求  
     * @param url 发送请求的URL  
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式  
     * @return URL所代表远程资源的响应  
     */  
    public static String sendPost(String url, String param) {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  
        try {  
            URL realUrl = new URL(url);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
              
            conn.setDoOutput(true);// 发送POST请求必须设置如下两行  
            conn.setDoInput(true);  
              
            out = new PrintWriter(conn.getOutputStream());// 获取URLConnection对象对应的输出流s  
            out.print(param);// 发送请求参数  
            out.flush();// flush输出流的缓冲  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));// 定义BufferedReader输入流来读取URL的响应  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } catch (Exception e) {  
            System.out.println("发送POST请求出现异常！" + e);  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输出流、输入流  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
  


  /*  public static String json2XML(String json){
        JSONObject jobj = JSONObject.fromObject(json);
        
        String xml =  new XMLSerializer().write(jobj);
        return xml;
    }*/
    


    
    // 提供主方法，测试发送GET请求和POST请求  
    public static void main(String args[]) {  
        // 发送GET请求  
     /*String s = TestGetPost.sendGet("http://123.56.8.137:8090/mj_background/user/inlogin",null);  
      System.out.println(s);  */
    	
    	SortedMap<String,String> packageParams = new TreeMap<String, String>();
    	
    	/*packageParams.put("appid",PayUtil.appid);//appID       应用id
        packageParams.put("mch_id",PayUtil.mch_id);//appID       商户号
        packageParams.put("nonce_str", PayUtil.getRandomStringByLength(32));//32位随机数
        packageParams.put("body","龙七棋牌-充值");//商品描述
        packageParams.put("out_trade_no",PayUtil.getOrderIdByUUId());
        packageParams.put("total_fee","201");//充值金额
        packageParams.put("spbill_create_ip","123.56.8.137");//终端IP
        packageParams.put("notify_url","http://123.56.8.137:8090/mj_background/user/showlogin");//通知地址
        packageParams.put("trade_type","APP");//支付类型
*/       /* packageParams.put("device_info","WEB");
        packageParams.put("sign_type","MD5");
        packageParams.put("detail","1234");
        packageParams.put("attach","aaaaa");
        packageParams.put("fee_type","CNY");
        packageParams.put("time_start",System.currentTimeMillis() / 1000);*/
        
    	
    	
        
        packageParams.put("appid",PayUtil.appid);//appID       应用id
		packageParams.put("mch_id",PayUtil.mch_id);//appID       商户号
		packageParams.put("nonce_str", PayUtil.getRandomStringByLength(32));//32位随机数
		packageParams.put("body","爱好是滴啊金属大师");//商品描述
		packageParams.put("out_trade_no",PayUtil.getOrderIdByUUId());
		packageParams.put("total_fee","1");//充值金额
		packageParams.put("spbill_create_ip","123.56.8.137");//终端IP
		packageParams.put("trade_type","APP");//支付类型
		packageParams.put("notify_url","http://123.56.8.137:8090/mj_background/user/showlogin");//通知地址
        
/*		String sign = PayUtil.createSign("UTF-8", packageParams);
		packageParams.put("sign", sign);
*/        //System.out.println(sign);
        
//        String str = UserService.postCharge(packageParams);
        
        
//        System.out.println(str);
        
    	String xml = "<xml>";
    	xml+=  "<appid><![CDATA["+packageParams.get("appid")+"]]></appid>";
    	xml+=   "<mch_id><![CDATA["+packageParams.get("mch_id")+"]]></mch_id>";
    	xml+=   "<nonce_str>"+packageParams.get("nonce_str")+"</nonce_str>";
    	xml+=  "<body><![CDATA["+packageParams.get("body")+"]]></body>";
    	xml+=  "<out_trade_no><![CDATA["+packageParams.get("out_trade_no")+"]]></out_trade_no>";
    	xml+=  "<total_fee><![CDATA["+packageParams.get("total_fee")+"]]></total_fee>";
		xml+=  "<spbill_create_ip><![CDATA["+packageParams.get("spbill_create_ip")+"]]></spbill_create_ip>";
		xml+=   "<trade_type><![CDATA["+packageParams.get("trade_type")+"]]></trade_type>";
		xml+=   "<notify_url><![CDATA["+packageParams.get("notify_url")+"]]></notify_url>";
		//xml+=   "<sign><![CDATA["+sign+"]]></sign>";
		xml+="</xml>";
		
		
		
		//String str = sendPost("https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey",xml);
		
		//System.out.println(str);
		
		//System.out.println(xml);
		
		/*SortedMap<String,String> secondParams = new TreeMap<>();
		
		secondParams.put("appid", PayUtil.appid);
		secondParams.put("bank_type", "CFT");
		secondParams.put("cash_fee", "1");
		secondParams.put("fee_type", "CNY");
		secondParams.put("is_subscribe", "N");
		secondParams.put("mch_id", "1458783202");
		secondParams.put("nonce_str", "o6mofzq59tmvnvytojso3hsz49h731ya");
		secondParams.put("openid", "o7UAf030CxyIMVMsukRIYlQT4JOk");
		secondParams.put("out_trade_no", "1000000833460608");
		secondParams.put("result_code", "SUCCESS");
		secondParams.put("return_code", "SUCCESS");
		secondParams.put("time_end", "20170418133002");
		secondParams.put("total_fee", "1");
		secondParams.put("trade_type", "APP");
		secondParams.put("transaction_id", "4003042001201704187439936558");
		
		
		String paySign = PayUtil.createSign("UTF-8", secondParams);
		
		System.out.println(paySign);*/
		//String str = UserService.postCharge(packageParams); 
        
		//System.out.println(str);
       // Element e = PayUtil.ParsingXML(str);
		
		/*String str = UserService.postCharge(packageParams);
		System.out.println(str);
		*/
        
        //System.out.println(e.elementText("nonce_str"));
        //System.out.println(s1);
        
       /* 
        try {  
            SAXReader reader = new SAXReader();  
             Document doc;   
             doc = DocumentHelper.parseText(s1);   
  
            //Document doc = reader.read(ffile); //读取一个xml的文件  
            Element root = doc.getRootElement();  
            Attribute testCmd= root.attribute("test");  
            Element eName = root.element("return_msg");  
            System.out.println("return_msg*--"+eName.getTextTrim());        
          
        } catch (Exception e) {  
            e.printStackTrace();  
        }  */
        
		
        
       
		//System.out.println(second);
    }  
    
  
    
}  
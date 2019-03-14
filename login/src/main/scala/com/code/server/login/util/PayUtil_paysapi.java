package com.code.server.login.util;

import com.code.server.db.model.Constant;
import com.code.server.login.service.ServerManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class PayUtil_paysapi {

	private static Logger logger = LoggerFactory.getLogger(PayUtil_paysapi.class);

	public static String UID = "xxxxxxxxxxxx";

//	public static String NOTIFY_URL = "http://您自己的域名/pays/notifyPay";

//	public static String RETURN_URL = "http://您自己的域名/pays/returnPay";

	public static String BASE_URL = "https://pay.paysapi.com";

//	public static String TOKEN = "xxxxxxxxxxxx";

//	public static Map<String, Object> payOrder(Map<String, Object> remoteMap) {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("uid", UID);
//		paramMap.put("notify_url", NOTIFY_URL);
//		paramMap.put("return_url", RETURN_URL);
//		paramMap.putAll(remoteMap);
//		paramMap.put("key", getKey(paramMap));
//		return paramMap;
//	}

	public static String getKey(Map<String, Object> remoteMap) {
		String key = "";
		if (null != remoteMap.get("goodsname")) {
			key += remoteMap.get("goodsname");
		}
		if (null != remoteMap.get("istype")) {
			key += remoteMap.get("istype");
		}
		if (null != remoteMap.get("notify_url")) {
			key += remoteMap.get("notify_url");
		}
		if (null != remoteMap.get("orderid")) {
			key += remoteMap.get("orderid");
		}
		if (null != remoteMap.get("orderuid")) {
			key += remoteMap.get("orderuid");
		}
		if (null != remoteMap.get("price")) {
			key += remoteMap.get("price");
		}
		if (null != remoteMap.get("return_url")) {
			key += remoteMap.get("return_url");
		}
		Constant constant = ServerManager.constant;
		key += constant.getPayToken();
		if (null != remoteMap.get("uid")) {
			key += remoteMap.get("uid");
		}
		return WXMD5.MD5Encode(key);
	}

	public static boolean checkPayKey(PaySaPi paySaPi) {
		String key = "";
		if (!StringUtils.isBlank(paySaPi.getOrderid())) {
			logger.info("支付回来的订单号：" + paySaPi.getOrderid());
			key += paySaPi.getOrderid();
		}
		if (!StringUtils.isBlank(paySaPi.getOrderuid())) {
			logger.info("支付回来的支付记录的ID：" + paySaPi.getOrderuid());
			key += paySaPi.getOrderuid();
		}
		if (!StringUtils.isBlank(paySaPi.getPaysapi_id())) {
			logger.info("支付回来的平台订单号：" + paySaPi.getPaysapi_id());
			key += paySaPi.getPaysapi_id();
		}
		if (!StringUtils.isBlank(paySaPi.getPrice())) {
			logger.info("支付回来的价格：" + paySaPi.getPrice());
			key += paySaPi.getPrice();
		}
		if (!StringUtils.isBlank(paySaPi.getRealprice())) {
			logger.info("支付回来的真实价格：" + paySaPi.getRealprice());
			key += paySaPi.getRealprice();
		}
		logger.info("支付回来的Key：" + paySaPi.getKey());
		Constant constant = ServerManager.constant;
		key += constant.getPayToken();
		logger.info("我们自己拼接的Key：" + WXMD5.MD5Encode(key));
		return paySaPi.getKey().equals( WXMD5.MD5Encode(key));
	}

	public static String getOrderIdByUUId() {
		int machineId = 1;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0;d 代表参数为正数型
		return machineId + String.format("%01d", hashCodeV);
	}

}

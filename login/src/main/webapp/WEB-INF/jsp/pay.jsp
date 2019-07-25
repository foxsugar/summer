<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@include file="timeWithOrder.jsp" %>
<%@include file="Md5.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>To Pay</title>
<script>
self.moveTo(0,0);
self.resizeTo(screen.availWidth,screen.availHeight);
</script>
<style> 
.tabPages{
margin-top:150px;text-align:center;display:block; border:3px solid #d9d9de; padding:30px; font-size:14px;
}
</style>
</head>
<body onLoad="document.uncome.submit()">
<div id="Content">
  <div class="tabPages">我们正在为您连接微信支付，请稍等......</div>
</div>

<%
	String AuthorizationURL = "http://pays.kooo8.com/Pay_Index.html";
	String merchantId = "11011";
	String keyValue = "9kehci9n1kn3q17lhm9d64itq3i7q8vn" ;
	String Channelid=(String)request.getAttribute("Bankco");
	String  Moneys	= (String) request.getAttribute("Moneys");
	System.out.println("===");
	System.out.println("Channelid " + Channelid);
	System.out.println(Moneys);
	String pay_tradetype=null;
	String pay_tongdao=null;
	String 	pay_bankcode=null;

	if(Channelid.equals("1")){
		pay_bankcode="902";   //'银行编码
	}else if(Channelid.equals("2")){
		pay_bankcode="903";   //'银行编码
	} else if (Channelid.equals("3")) {
		pay_bankcode = "901";
	}

//	Channelid = "wx";
//	pay_bankcode="901";   //'银行编码
//	pay_tradetype="900021";   //'通道类型
//	pay_tongdao="WxSm";

	String	pay_memberid=merchantId;//商户id
	String	pay_orderid=(String) request.getAttribute("orderId");
	System.out.println("===================================");
	System.out.println("order id is " + pay_orderid);
	System.out.println("===================================");
	String	pay_applydate=generateTime();//yyyy-MM-dd HH:mm:ss

	String baseUrl = "http://94.191.19.227:8085";
	String	pay_notifyurl=baseUrl+"/Pay/notify";//通知地址
	String	pay_callbackurl=baseUrl + "/Pay/callback";//回调地址

	System.out.println("===================================");
	System.out.println(pay_notifyurl);
	System.out.println(pay_callbackurl);
	System.out.println("===================================");
	String	pay_amount=Moneys;
	String	pay_reserved1= "";

	String	pay_reserved2="";
	String	pay_reserved3="";
	String	pay_productname="充值";
	String	pay_productnum="";
	String	pay_productdesc="";
	String	pay_producturl="";

	String stringSignTemp="pay_amount="+pay_amount+"&pay_applydate="+pay_applydate+  "&pay_bankcode="+pay_bankcode+"&pay_callbackurl="+pay_callbackurl+"&pay_memberid="+pay_memberid+"&pay_notifyurl="+pay_notifyurl+"&pay_orderid="+pay_orderid+"&key="+keyValue+"";
	String pay_md5sign=md5(stringSignTemp);

%>
<!-- 
<iframe src="about:blank" id="kaixin" name="kaixin" align="center" width="960"  height="301" marginwidth="1" marginheight="1" frameborder="0" scrolling="no"> </iframe>
	<form name="uncome" action="<%=AuthorizationURL%>" method="post" target="kaixin">
 -->	
<form name="uncome" action="<%=AuthorizationURL%>" method="post">
<input type="hidden" name="pay_memberid"  value="<%=pay_memberid%>">
<input type="hidden" name="pay_orderid"  value="<%=pay_orderid%>">
<input type="hidden" name="pay_applydate"  value="<%=pay_applydate%>">
<input type="hidden" name="pay_bankcode"  value="<%=pay_bankcode%>">
<input type="hidden" name="pay_notifyurl"  value="<%=pay_notifyurl%>">
<input type="hidden" name="pay_callbackurl"  value="<%=pay_callbackurl%>">
<input type="hidden" name="pay_amount"  value="<%=pay_amount%>">
<%--<input type="hidden" name="pay_tradetype"  value="<%=pay_tradetype%>">--%>
<%--<input type="hidden" name="pay_tongdao"  value="<%=pay_tongdao%>">--%>
<input type="hidden" name="pay_reserved1"  value="<%=pay_reserved1%>">
<input type="hidden" name="pay_reserved2"  value="<%=pay_reserved2%>">
<input type="hidden" name="pay_reserved3"  value="<%=pay_reserved3%>">
<input type="hidden" name="pay_productname"  value="<%=pay_productname%>">
<input type="hidden" name="pay_productnum"  value="<%=pay_productnum%>">
<input type="hidden" name="pay_productdesc"  value="<%=pay_productdesc%>">
<input type="hidden" name="pay_producturl"  value="<%=pay_producturl%>">
，。<input type="hidden" name="pay_md5sign"  value="<%=pay_md5sign%>">
</form>
</body>
</html>
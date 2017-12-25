<%@page import="java.util.Date"%>
<%@page import="java.util.Random"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%!
	public String generateOrderId(){
		String keyup_prefix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String keyup_append=String.valueOf(new Random().nextInt(899999)+100000);
        String pay_orderid=keyup_prefix+keyup_append;//订单号
		return pay_orderid;
	}
	public String generateTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	%>
</body>
</html>
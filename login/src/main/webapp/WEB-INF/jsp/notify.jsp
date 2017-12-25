<%@page import="java.io.PrintWriter"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
	String memberid=request.getParameter("memberid");
	String orderid=request.getParameter("orderid");
	String amount=request.getParameter("amount");
	String datetime=request.getParameter("datetime");
	String returncode=request.getParameter("returncode");
	String reserved1=request.getParameter("reserved1");
	String reserved2=request.getParameter("reserved2");
	String sign=request.getParameter("sign");
	String keyValue="";
	String SignTemp="amount="+amount+"+datetime="+datetime+"+memberid="+memberid+"+orderid="+orderid+"+returncode="+returncode+"+key="+keyValue+"";
	String md5sign=MD5(SignTemp,32,1);//MD5加密
	if (sign.equals(md5sign)){
		if(returncode.equals("00")){
			//支付成功，写返回数据逻辑
			PrintWriter pw=response.getWriter();
			pw.write("ok");
		}else{
			PrintWriter pw=response.getWriter();
			pw.write("支付失败");
		}
	}else{
		PrintWriter pw=response.getWriter();
		pw.write("验签失败");
	}

%>
</body>
</html>
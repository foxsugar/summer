<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>在线充值</title>
  <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
  <style>
    *{ font-family:Arial, Helvetica, sans-serif;
      font-size:12px}
    .STYLE1 {font-size: 12px}
  </style>
</head>
<body>
<html>
<head>
  <title>网银充值</title>
  <style>
    *{ font-family:Arial, Helvetica, sans-serif;
      font-size:12px}
    .STYLE1 {font-size: 12px}
  </style>
</head>
<body>
<div style="text-align:center">
  <h2>请选择网上银行</h2>
</div>
<%=request.getRequestURI() %>
<form name="form1" action="pay" method="post">
  <table width="445" border="0" align="center" cellpadding="0" cellspacing="0" style="border:#99CC00 solid 2px">
    <tr>
      <td  colspan="2" align="center" bordercolor="#00CCFF">

        <table width="68%" border="0" cellpadding="0" cellspacing="1" bgcolor="#CCCCCC">
          <tr>
            <td width="32%" height="25" align="left" bgcolor="#FFFFFF"><input type="radio" name="Bankco" value="wx"  />微信支付</td>
            <td width="32%" align="left" bgcolor="#FFFFFF"><input type="radio" name="Bankco" value="zfb" />支付宝支付</td>
          </tr>
        </table>充值金额：<input type="text" name="Moneys" value="" />
      </td>
    </tr>
    <tr>
      <td height="36" colspan="2" align="center"><input type="submit" name="submit2" value="确认付款" onclick="return checkMoney()" /></td>
    </tr>
  </table>
</form>
</body>
</html>

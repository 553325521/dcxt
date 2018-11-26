<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%HashMap<String, String> map = (HashMap<String, String>)request.getAttribute("payMap");%>
<script>
//微信支付函数
function onBridgeReady(){
	WeixinJSBridge.invoke(
		'getBrandWCPayRequest', {
			"appId": '<%=String.valueOf(map.get("apiAppid"))%>',     //公众号名称，由商户传入     
			"timeStamp": '<%=String.valueOf(map.get("apiTimestamp"))%>',         //时间戳，自1970年以来的秒数     
			"nonceStr": '<%=String.valueOf(map.get("apiNoncestr"))%>', //随机串     
			"package": '<%=String.valueOf(map.get("apiPackage"))%>',     
			"signType": '<%=String.valueOf(map.get("apiSigntype"))%>', //微信签名方式：     
			"paySign": '<%=String.valueOf(map.get("apiPaysign"))%>'//微信签名 
		},
		function(res){
			if(res.err_msg == "get_brand_wcpay_request:ok" ){
				// 使用以上方式判断前端返回,微信团队郑重提示：
				//res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
				window.opener=null;
				window.open('','_self');
				window.close();
			} 
			console.info(res)
		}); 
}
					
					
//支付按钮
window.onload = function confirmPayment(){
//发起微信支付
	if (typeof WeixinJSBridge == "undefined"){
		if( document.addEventListener ){
			document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
		}else if (document.attachEvent){
			document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
			document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
		}
	}else{
		onBridgeReady();
	}
}

</script>
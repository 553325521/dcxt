<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="<%=basePath%>assets/css/pay/global.css">
		<title></title>
	</head>
	<body>
		<div class="top">
			<h1><%=String.valueOf(request.getAttribute("shopName"))%>（会员消费享更多优惠）</h1>
		</div>
		<form action="<%=basePath%>json/shoppay.jsp" method="POST">
			<input name="shopId" type="hidden" value="<%=String.valueOf(request.getAttribute("shopId"))%>"/>
			<%
				if(request.getAttribute("openId") != null){
			%>
			<input name="openId" type="hidden" value="<%=String.valueOf(request.getAttribute("openId"))%>"/>
			<%} %>
			<ul class="main">
				<li style="padding-right: .4rem;padding-left: .4rem;padding-bottom: .3rem;">
					<!-- <div class="pay_logo clear">
						<div class="pay_logo_l fl">
							<div class="pay_logo_l_img"></div>
							<span>用户LOGO</span>
						</div>
						<div class="pay_logo_r">
							<div class="pay_logo_r_img"></div>
							<span>商家LOGO</span>
						</div>
						<div class="pay_logo_jt">
							<img src="img/pay/jiantou.webp" />
						</div>
					</div> -->
					<div class="Amount_t clear">
						<h3 class="fl">消费总金额</h3>
						<input name="totalMoney" type="text" />
						<p>元</p>
					</div>
					<!-- <p class="pay_logo_p">输入不享优惠金额</p>
					<div class="Amount_b clear">
						<h3 class="fl">不享优惠金额</h3>
						<input type="text" />
						<p style="font-size: .28rem;">&nbsp;元</p>
					</div> -->
				</li>
				<!--<div class="blank"></div>
			 	<li>
					<ul class="Discount">
						<li class="clear">
							<h3 class="fl">满100随机减5~20元<span style="font-size: .24rem;">(不限时段)</span></h3>
							<p>-13.80</p>
						</li>
						<li style="border-bottom: none;">
							<h3 class="h3 fl">不足1元抹零</span></h3>
							<p>-0.15</p>
						</li>
					</ul>
				</li> -->
				<!--<div class="blank"></div>
				 <li>
					<ul class="Discount">
						<li class="clear">
							<h2 class="fl">优惠券</span></h2>
							<h4 class="yhj" style="position: absolute; right: .75rem;">无可用优惠券</h4>
						</li>
						<li class="clear">
							<h2 class="fl">积分<span style="font-size: .24rem;">(共1280.6积分)</span></h2>
							<p style="position: absolute; right: 1.15rem;">-12.00</p>
							<input class="jf_input" type="checkbox" />
						</li>
						<li class="clear">
							<h2 class="fl">还需支付</span></h2>
							<p>-0.15</p>
						</li>
					</ul>
				</li> -->
			</ul>
			<div class="but" >
				<!-- <p>确认买单&nbsp;&nbsp;￥<span>135.00</span></p> -->
				<input type="submit" value="确认支付"></input>
			</div>
		</form>
	</body>
	<script>
//微信支付函数
function onBridgeReady(){
<%-- 	WeixinJSBridge.invoke(
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
} --%>
					
					
//支付按钮
/* function confirmPayment(){
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
} */

</script>
</html>

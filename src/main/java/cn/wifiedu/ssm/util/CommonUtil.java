package cn.wifiedu.ssm.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.google.zxing.WriterException;
import com.thoughtworks.xstream.XStream;

import cn.wifiedu.ssm.vo.MessageVo;
import sun.misc.BASE64Encoder;

public class CommonUtil {

	/**
	 * 
	 * @param url
	 *            完整路径 http://m.dd.com/json/xxx_xxx_xxx.json
	 */
	public static String qrCode(String url) {
		try {

			BufferedImage image = QRCode.genBarcode(url, 200, 200);

			ByteArrayOutputStream os = new ByteArrayOutputStream();// 新建流。

			ImageIO.write(image, "png", os);

			byte b[] = os.toByteArray();// 从流中获取数据数组。

			String base64Url = new BASE64Encoder().encode(b);

			return base64Url;

		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static Map<String, Object> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
		Map<String, Object> map = new HashMap<String, Object>();
		SAXReader reader = new SAXReader();

		InputStream is = request.getInputStream();
		Document doc = reader.read(is);
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for (Element e : list) {
			map.put(e.getName(), e.getText());
		}

		is.close();
		return map;
	}

	public static String objectToXml(MessageVo msg) {
		XStream xs = new XStream();
		xs.alias("xml", msg.getClass());
		return xs.toXML(msg);
	}

	/**
	 * HTTP GET请求
	 */
	public static String get(String url) {
		// 创建Http Client对象, 这就类似打来了一个浏览器并创建了一个浏览器进程
		HttpClient httpclient = HttpClientBuilder.create().build();
		// 创建Get类型的Http请求对象
		HttpGet httpget = new HttpGet(url);
		// 设置报文头字段
		httpget.setHeader("Accept-Language", "zh,en;q=0.8,zh-CN;q=0.6");
		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
		httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		// 用于获取响应对象
		HttpResponse response = null;

		try {
			httpget.setHeader(new Header() {

				public String getValue() {
					return "zh-cn";
				}

				public String getName() {
					return "Accept-Language";
				}

				public HeaderElement[] getElements() throws ParseException {
					return null;
				}
			});

			response = httpclient.execute(httpget);
			int responseStatusCode = response.getStatusLine().getStatusCode();
			// System.out.println("Response statusCode: " + responseStatusCode);

			// HTTP响应报文成功
			if (responseStatusCode == 200) {
				HttpEntity httpEntity = response.getEntity();
				List resultInfoList = new ArrayList();
				if (httpEntity != null) {
					// 打印响应内容长度
					// System.out.println("Response content length: " +
					// httpEntity.getContentLength());
					// 打印响应内容

					String content = EntityUtils.toString(httpEntity);

					// System.out.println("Response content: " + content);

					return content.trim();
				}
			} else {
				// System.out.println();
				return "回应的HTTP报文状态值:" + responseStatusCode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取base配置文件内容
	 * 
	 * @param name
	 * @return
	 */
	public static String getPath(String name) {
		String src = "";
		try {
			ClassLoader classLoader = CommonUtil.class.getClassLoader();
			Properties prop = new Properties();
			InputStream in = classLoader.getResourceAsStream("base.properties");
			prop.load(in);
			src = prop.getProperty(name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src;
	}

	/**
	 * 将Json对象转换成Map
	 * 
	 * @param jsonObject
	 *            json对象
	 * @return Map对象
	 * @throws JSONException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map toMap(String jsonString) throws JSONException {

		JSONObject jsonObject = new JSONObject(jsonString);

		Map result = new HashMap();
		Iterator iterator = jsonObject.keys();
		String key = null;
		String value = null;

		while (iterator.hasNext()) {

			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);

		}
		return result;

	}

	/**
	 * json转成List<Map<String, Object>>
	 * 
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> toListMap(String json) {
		List<Object> list = JSON.parseArray(json);

		List<Map<String, Object>> listw = new ArrayList<Map<String, Object>>();
		for (Object object : list) {
			Map<String, Object> ageMap = new HashMap<String, Object>();
			Map<String, Object> ret = (Map<String, Object>) object;// 取出list里面的值转为map
			listw.add(ret);
		}
		return listw;

	}

	/**
	 * 获取本机内网IP
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getMyIp() {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP
		try {
			Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
				Enumeration address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = (InetAddress) address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	public static String posts(String path, String params, String charset) throws Exception {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(path);

			// 设置参数
			StringEntity postEntity = new StringEntity(params,
					ContentType.create("application/x-www-form-urlencoded", charset));

			httpPost.setEntity(postEntity);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @author kqs
	 * @param code
	 * @return
	 * @return String
	 * @date 2018年8月6日 - 上午10:09:15
	 * @description:根据微信授权code 获取用户信息
	 */
	public static Map<String, Object> getWxUserInfo(String code) {
		Map<String, Object> userMap = getOpenIdByCode(code);

		String url = CommonUtil.getPath("wx_userinfo_get_url");

		String accessToken = userMap.get("USER_WX_TOKEN").toString();

		String openId = userMap.get("USER_WX").toString();

		url = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);

		String res = CommonUtil.get(url);
		if (res != null && res.indexOf("errcode") <= 0) {
			com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(res);
			userMap.put("USER_SN", obj.get("nickname"));
			userMap.put("USER_SEX", obj.get("sex"));
//			userMap.put("province", obj.get("province"));
//			userMap.put("city", obj.get("city"));
//			userMap.put("country", obj.get("country"));
			userMap.put("USER_HEAD_IMG", obj.get("headimgurl"));
			return userMap;
		}
		return null;
	}

	/**
	 * @author kqs
	 * @param code
	 * @return
	 * @return String
	 * @date 2018年8月6日 - 上午10:09:54
	 * @description:根据微信授权code 获取openId
	 */
	public static Map<String, Object> getOpenIdByCode(String code) {
		String url = CommonUtil.getPath("WX_GET_OPENID_URL");
		url = url.replace("CODE", code);
		System.out.println("getOpenIdByCode=" + url);
		String res = CommonUtil.get(url);
		com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(res);
		String openId = obj.get("openid").toString();
		String refresh_token = obj.get("refresh_token").toString();
		String access_token = obj.get("access_token").toString();
		
		Map<String, Object> reMap = new HashMap<>();
		reMap.put("USER_WX", openId);
		reMap.put("USER_WX_TOKEN", access_token);
		reMap.put("USER_WX_REFRESH_TOKEN", refresh_token);
		return reMap;
	}
}

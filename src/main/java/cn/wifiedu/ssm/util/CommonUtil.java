package cn.wifiedu.ssm.util;

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

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

public class CommonUtil {
	
	/**
	 *HTTP GET请求
	 */
	public static String get(String url) {
		// 创建Http Client对象, 这就类似打来了一个浏览器并创建了一个浏览器进程
		HttpClient httpclient = HttpClientBuilder.create().build();
		// 创建Get类型的Http请求对象
		HttpGet httpget = new HttpGet(url);
		// 设置报文头字段
		httpget.setHeader("Accept-Language", "zh,en;q=0.8,zh-CN;q=0.6");
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
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
            //System.out.println("Response statusCode: " + responseStatusCode);  
            
		    // HTTP响应报文成功
		    if (responseStatusCode == 200) {
		        HttpEntity httpEntity = response.getEntity();
		        List resultInfoList = new ArrayList(); 
		        if (httpEntity != null) {
		        	 // 打印响应内容长度    
                   // System.out.println("Response content length: " + httpEntity.getContentLength());  
                    // 打印响应内容    
                    
                    String content = EntityUtils.toString(httpEntity);
                    
                    //System.out.println("Response content: " + content);  
                    
		            return content.trim();
		        }         	
		    } else {
		        //System.out.println();
		        return "回应的HTTP报文状态值:" + responseStatusCode;
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    return null;
		
	}
	
	/**
	 * 获取base配置文件内容
	 * @param name
	 * @return
	 */
	public static String getPath(String name){
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
     * @param json
     * @return
     */
    public static List<Map<String, Object>> toListMap(String json){
    	List<Object> list =JSON.parseArray(json);
    	
    	List< Map<String,Object>> listw = new ArrayList<Map<String,Object>>();
    	for (Object object : list){
	    	Map<String,Object> ageMap = new HashMap<String,Object>();
	    	Map <String,Object> ret = (Map<String, Object>) object;//取出list里面的值转为map
	    	listw.add(ret);
    	}
		return listw;
    	
    }
    
    /**
     * 获取本机内网IP
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
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
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
    
    
}

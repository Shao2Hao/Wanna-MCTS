package m.pay.service;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Formatter;

import m.common.service.Service;
import m.pay.PayData;
import m.system.exception.MException;
import m.system.json.JsonReader;
import m.system.util.HttpRequestUtil;
import m.system.util.JSONMessage;

import com.github.wxpay.sdk.WXPayUtil;

public class WxServerService extends Service {
	public WxServerService(){
		PayData.init();
	}
	private static String accessToken;
	private static Date tokenEndDate;
	public String getAccessToken() throws Exception{
		if(null==tokenEndDate||new Date().getTime()>tokenEndDate.getTime()){
			JsonReader json=new JsonReader(new HttpRequestUtil().doGet(new StringBuffer("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=")
				.append(PayData.getWxpay_appID()).append("&secret=").append(PayData.getWxpay_key()).toString()));
			String access_token=json.get(String.class, "access_token");
			if(null==access_token){
				System.out.println(json);
				throw new MException(this.getClass(),json.get(String.class, "errmsg"));
			}else{
				accessToken=access_token;
				tokenEndDate=new Date(new Date().getTime()+(json.get(Integer.class, "expires_in")-10)*1000);
			}
		}
		return accessToken;
	}
	private static String jsapiTicket;
	private static Date ticketEndDate;
	public String getJsapiTicket(String access_token) throws Exception{
		if(null==ticketEndDate||new Date().getTime()>ticketEndDate.getTime()){
			JsonReader json=new JsonReader(new HttpRequestUtil().doGet(new StringBuffer("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=").append(access_token)
				.append("&type=jsapi").toString()));
			String jsapi_ticket=json.get(String.class, "ticket");
			if(null==jsapi_ticket){
				System.out.println(json);
				throw new MException(this.getClass(),json.get(String.class, "errmsg"));
			}else{
				jsapiTicket=jsapi_ticket;
				ticketEndDate=new Date(new Date().getTime()+(json.get(Integer.class, "expires_in")-10)*1000);
			}
		}
		return jsapiTicket;
	}
	
	
	public JSONMessage getWxConfig(String url) throws Exception{
		JSONMessage message=new JSONMessage();
		String access_token=getAccessToken();
		String jsapi_ticket=getJsapiTicket(access_token);
		String nonceStr=WXPayUtil.generateNonceStr();
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String signature = "";  
		// 注意这里参数名必须全部小写，且必须有序  
		String sign = new StringBuffer("jsapi_ticket=").append(jsapi_ticket).append("&noncestr=").append(nonceStr).append("&timestamp=").append(timestamp).append("&url=").append(url).toString();  
		try {  
			signature = toSignature(sign,"SHA-1");  
		} catch (Exception e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}  
		message.push("appId", PayData.getWxpay_appID());  
		message.push("timestamp", timestamp);  
		message.push("nonceStr", nonceStr);  
		message.push("signature", signature);  
		return message;  
	}
	public static String toSignature(String text,String type) throws Exception{
		MessageDigest crypt = MessageDigest.getInstance(type);  
		crypt.reset();  
		crypt.update(text.getBytes("UTF-8"));  
		return byteToHex(crypt.digest());
	}
	/** 
	* 方法名：byteToHex</br> 
	* 详述：字符串加密辅助方法 </br> 
	* @param hash 
	* @return 说明返回值含义 
	* @throws 说明发生此异常的条件 
	*/  
	private static String byteToHex(final byte[] hash) {  
		Formatter formatter = new Formatter();  
		for (byte b : hash) {  
			formatter.format("%02x", b);  
		}  
		String result = formatter.toString();  
		formatter.close();  
		return result;  
	}  
	
	public static void main(String[] a) throws Exception{
//		String access_token=new WxServerService().getAccessToken();
//		System.out.println(access_token);
//		String jsapi_ticket=new WxServerService().getJsapiTicket(access_token);
//		System.out.println(jsapi_ticket);
//		System.out.println(new WxServerService().getWxConfig("http://ostudio.cc/"));
	}
}

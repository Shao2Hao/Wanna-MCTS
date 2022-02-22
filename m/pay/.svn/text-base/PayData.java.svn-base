package m.pay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import m.system.util.StringUtil;

public class PayData {
	private static boolean isInit=false;

	//网站路径 根目录   以/结束
	private static String pay_webPath;
	//商户订单服务类
	private static String pay_serviceClass;
	
	// 请求网关地址
	private static String alipay_url;
	//// 商户appid
	private static String alipay_appID;
	// 私钥 pkcs8格式的
	private static String alipay_rsaPrivateKey;
	// 支付宝公钥
	private static String alipay_alipayPublicKey;
	// RSA2
	private static String alipay_signType;
	//返回格式
	private static String alipay_format="json";
	//编码
	private static String alipay_charset="UTF-8";
	// 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	private static String alipay_notifyUrl = "action/payAlipay/notifyPage";
	// 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
	private static String alipay_returnUrl = "action/payAlipay/returnPage";

	//微信分配的公众号ID（开通公众号之后可以获取到）
	private static String wxpay_appID;
	//微信公众号的key
	private static String wxpay_key;
	//商户 api key
	private static String wxpay_apiKey;
	//#微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String wxpay_mchID;
	//#HTTPS证书的本地路径
	private static String wxpay_certLocalPath;
	//#HTTPS证书密码，默认密码等于商户号MCHID
	private static String wxpay_certPassword;
	//微信通知地址
	private static String wxpay_notifyUrl = "action/payWxpay/notifyPage";
	public static void init(){
		if(!isInit){
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("pay.properties");
			Properties p = new Properties();
			try {
				p.load(inputStream);
				pay_webPath=StringUtil.noSpace(p.getProperty("pay_webPath"));
				pay_serviceClass=StringUtil.noSpace(p.getProperty("pay_serviceClass"));

				alipay_url=StringUtil.noSpace(p.getProperty("alipay_url"));
				alipay_appID=StringUtil.noSpace(p.getProperty("alipay_appID"));
				alipay_rsaPrivateKey=StringUtil.noSpace(p.getProperty("alipay_rsaPrivateKey"));
				alipay_alipayPublicKey=StringUtil.noSpace(p.getProperty("alipay_alipayPublicKey"));
				alipay_signType=StringUtil.noSpace(p.getProperty("alipay_signType"));

				wxpay_appID=StringUtil.noSpace(p.getProperty("wxpay_appID"));
				wxpay_key=StringUtil.noSpace(p.getProperty("wxpay_key"));
				wxpay_apiKey=StringUtil.noSpace(p.getProperty("wxpay_apiKey"));
				wxpay_mchID=StringUtil.noSpace(p.getProperty("wxpay_mchID"));
				wxpay_certLocalPath=StringUtil.noSpace(p.getProperty("wxpay_certLocalPath"));
				wxpay_certPassword=StringUtil.noSpace(p.getProperty("wxpay_certPassword"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			isInit=true;
		}
	}


	public static boolean isInit() {
		return isInit;
	}


	public static String getAlipay_url() {
		return alipay_url;
	}


	public static String getAlipay_appID() {
		return alipay_appID;
	}


	public static String getAlipay_rsaPrivateKey() {
		return alipay_rsaPrivateKey;
	}


	public static String getAlipay_alipayPublicKey() {
		return alipay_alipayPublicKey;
	}


	public static String getAlipay_signType() {
		return alipay_signType;
	}


	public static String getAlipay_format() {
		return alipay_format;
	}


	public static String getAlipay_charset() {
		return alipay_charset;
	}


	public static String getAlipay_notifyUrl() {
		return alipay_notifyUrl;
	}


	public static String getAlipay_returnUrl() {
		return alipay_returnUrl;
	}


	public static String getWxpay_appID() {
		return wxpay_appID;
	}


	public static String getWxpay_key() {
		return wxpay_key;
	}


	public static String getWxpay_mchID() {
		return wxpay_mchID;
	}


	public static String getWxpay_certLocalPath() {
		return wxpay_certLocalPath;
	}


	public static String getWxpay_certPassword() {
		return wxpay_certPassword;
	}


	public static String getPay_webPath() {
		return pay_webPath;
	}


	public static String getWxpay_notifyUrl() {
		return wxpay_notifyUrl;
	}


	public static String getPay_serviceClass() {
		return pay_serviceClass;
	}


	public static String getWxpay_apiKey() {
		return wxpay_apiKey;
	}
}

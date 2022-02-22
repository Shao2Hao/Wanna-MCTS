package m.pay.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import m.common.action.ActionResult;
import m.pay.PayData;
import m.pay.model.OutPayOrder;
import m.pay.util.HttpClientCustomSSL;
import m.pay.util.ServiceResult;
import m.pay.util.SignTools;
import m.pay.util.WxPayConfig;
import m.pay.util.XMLUtil;
import m.system.exception.MException;
import m.system.json.JsonReader;
import m.system.util.GenerateID;
import m.system.util.HttpRequestUtil;
import m.system.util.JSONMessage;
import com.alipay.api.AlipayApiException;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;

public class WxPayService extends PayService {
	private static WXPay wxpay;
	public WxPayService(){
		PayData.init();
		if(null==wxpay){
			wxpay=new WXPay(new WxPayConfig(PayData.getWxpay_appID(),PayData.getWxpay_apiKey(),PayData.getWxpay_mchID(),PayData.getWxpay_certLocalPath()),WXPayConstants.SignType.MD5);
		}
	}
	/**
	 * 授权链接
	 * @param out_trade_no
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getSnsapiUrl(String out_trade_no) throws UnsupportedEncodingException{
		String redirect_uri=URLEncoder.encode(new StringBuffer(PayData.getPay_webPath()).append("action/payWxpay/doWxPay").toString(),"utf-8");
		return new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(PayData.getWxpay_appID()).append("&redirect_uri=").append(redirect_uri).append("&response_type=code&scope=snsapi_base&state=").append(out_trade_no).append("#wechat_redirect").toString();
	}
	
	public ServiceResult wxPay(String code,OutPayOrder order,String pay_ip) throws MException, Exception{
		System.out.println("wxPay code"+code);
		StringBuffer url=new StringBuffer("https://api.weixin.qq.com/sns/oauth2/access_token?appid=").append(PayData.getWxpay_appID()).append("&secret=").append(PayData.getWxpay_key()).append("&code=").append(code).append("&grant_type=authorization_code");
		JsonReader json=new JsonReader(new HttpRequestUtil().doGet(url.toString()));

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", order.getBody());
		data.put("out_trade_no", order.getOutTradeNo()); 
		data.put("device_info", "");
		data.put("fee_type", "CNY");
		data.put("total_fee", String.valueOf(order.getPayAmount()*100).split("\\.")[0]);
		data.put("spbill_create_ip", pay_ip);
		data.put("notify_url", PayData.getPay_webPath()+PayData.getWxpay_notifyUrl());
		data.put("trade_type", "JSAPI");
		data.put("product_id", order.getOutTradeNo());
		data.put("openid",json.get(String.class, "openid"));
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.unifiedOrder(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				JSONMessage config=getService(WxServerService.class).getWxConfig(new StringBuffer(PayData.getPay_webPath()).append("action/payWxpay/doWxPay").toString());
				Map<String,String> map=new HashMap<String,String>();
				map.put("appId", r.get("appid"));
				map.put("nonceStr", config.get("nonceStr").toString());
				map.put("package", new StringBuffer("prepay_id=").append(r.get("prepay_id")).toString());
				map.put("signType", "MD5");
				map.put("timeStamp", config.get("timestamp").toString());
				map.put("paySign", WXPayUtil.generateSignature(map, PayData.getWxpay_key()));
				JSONMessage message=new JSONMessage();
				message.push("appId", map.get("appId"));
				message.push("nonceStr", map.get("nonceStr"));
				message.push("package", map.get("package"));
				message.push("signType", map.get("signType"));
				message.push("timeStamp", map.get("timeStamp"));
				message.push("paySign", map.get("paySign"));
				message.push("config", config);
				message.push("outUrl", new StringBuffer(PayData.getPay_webPath()).append(order.getOutUrl()).toString());
				result.setHtmlBody(message.toJSONString());
			}else{
				throw new MException(this.getClass(),r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}
    /**
     * app支付
     * 
     * @param out_trade_no
     * @param pay_ip
     * @return
     * @throws MException
     */
	public ServiceResult appPay(String out_trade_no,String pay_ip) throws MException {
		return app(out_trade_no, pay_ip);
	}
	
	public ServiceResult app(String out_trade_no,String pay_ip) throws MException {
		wxpay=new WXPay(new WxPayConfig(PayData.getWxpay_appID(),PayData.getWxpay_apiKey(),PayData.getWxpay_mchID(),PayData.getWxpay_certLocalPath()),WXPayConstants.SignType.MD5);
		OutPayOrder order=getOutPayOrder(out_trade_no,"payWxpay");
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", order.getBody());
		data.put("out_trade_no", order.getOutTradeNo());
		data.put("device_info", "");
		data.put("fee_type", "CNY");
		data.put("total_fee", String.valueOf(order.getPayAmount()*100).split("\\.")[0]);//String.valueOf(order.getPayAmount()*100).split("\\.")[0]
		data.put("spbill_create_ip", pay_ip);
		data.put("notify_url", PayData.getPay_webPath()+PayData.getWxpay_notifyUrl());
		data.put("trade_type", "APP");
		data.put("product_id", order.getOutTradeNo());
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.unifiedOrder(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				JSONMessage message=new JSONMessage();

				  Date date1= new Date();
	              Long time =date1.getTime();
	              time=time/1000;
	              Map<String, String> wxData = new HashMap<String, String>();
	            
				//二次签名 参数  
				wxData.put("appid",r.get("appid"));//appid
				wxData.put("partnerid",r.get("mch_id"));//商家id
				wxData.put("prepayid",r.get("prepay_id"));//预单号
				wxData.put("noncestr",r.get("nonce_str"));//随机字符串
				wxData.put("timestamp",String.valueOf(time));//时间戳
				wxData.put("package","Sign=WXPay");//package
		        WXPayUtil wx=new WXPayUtil();
		        
		        //返回的参数
		        message.push("sign",wx.generateSignature(wxData,PayData.getWxpay_apiKey()));//package
		        message.push("appid",r.get("appid"));//appid
				message.push("mch_id",r.get("mch_id"));//商家id
				message.push("prepay_id",r.get("prepay_id"));//预单号
				message.push("nonce_str",r.get("nonce_str"));//随机字符串
				message.push("timestamp",String.valueOf(time));//时间戳
				message.push("package","Sign=WXPay");//package
		            
				result.setHtmlBody(message.toJSONString());
			}else{
				throw new MException(this.getClass(),r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}


	 /**
	  * 微信小程序支付
	  * @param out_trade_no
	  * @param pay_ip
	  * @return
	  * @throws MException
	  */
	public ServiceResult weChatPay(String out_trade_no,String pay_ip,String openid) throws Exception {
		OutPayOrder order=getOutPayOrder(out_trade_no,"weChatPay");
		//获取订单id获取金额
		//记住 微信支付中最小单位是1分   1就是 1分   需要进行金额转换
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", order.getBody());//为撒要支付
		data.put("out_trade_no", order.getOutTradeNo());
		data.put("device_info", "");
		data.put("fee_type", "CNY");
		data.put("total_fee", "1");//
		data.put("spbill_create_ip", pay_ip);
		data.put("notify_url", PayData.getPay_webPath()+PayData.getWxpay_notifyUrl());
		data.put("trade_type", "JSAPI");  //支付类型小程序支付
	    data.put("product_id", "12");
	    data.put("openid",openid);//小程序用户唯一标识
		ServiceResult result=new ServiceResult();
		try {
			wxpay=new WXPay(new WxPayConfig("wxab144ac62dc61d10","703cbd89247fd4eef14f235373e21ec3","1499569722","/wxx/apiclient_cert.p12"),WXPayConstants.SignType.MD5);
			Map<String, String> r = wxpay.unifiedOrder(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				JSONMessage message=new JSONMessage();
				
				Map<String, String> wxData = new HashMap<String, String>();
	            
				Date date1= new Date();
	            Long time =date1.getTime();
	            time=time/1000;
				
				//二次签名 参数  
				
				wxData.put("appId",r.get("appid"));//appid
				wxData.put("timeStamp",String.valueOf(time));//时间戳
				wxData.put("nonceStr",r.get("nonce_str"));//随机字符串
				wxData.put("package","prepay_id="+r.get("prepay_id"));//package
				wxData.put("signType","MD5");//时间戳
	          
				
				WXPayUtil wx=new WXPayUtil();
				message.push("appid", r.get("appid"));
				message.push("mch_id", r.get("mch_id"));
				message.push("nonce_str", r.get("nonce_str"));
				message.push("sign", wx.generateSignature(wxData,"703cbd89247fd4eef14f235373e21ec3"));
				message.push("result_code", r.get("result_code"));
				message.push("trade_type", r.get("trade_type"));
				message.push("package", "prepay_id="+r.get("prepay_id"));
				message.push("code_url", r.get("code_url"));
				message.push("timeStamp", String.valueOf(time));
				result.setHtmlBody(message.toJSONString());
			}else{
				throw new MException(this.getClass(),r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}
	
	
	public ServiceResult pagePay(String out_trade_no,String pay_ip) throws MException {
		OutPayOrder order=getOutPayOrder(out_trade_no,"payWxpay");
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", order.getBody());
		data.put("out_trade_no", out_trade_no);
		data.put("device_info", "");
		data.put("fee_type", "CNY");
		data.put("total_fee", String.valueOf(order.getPayAmount()*100).split("\\.")[0]);
		data.put("spbill_create_ip", pay_ip);
		data.put("notify_url", PayData.getPay_webPath()+PayData.getWxpay_notifyUrl());
		data.put("trade_type", "NATIVE");
		data.put("product_id", out_trade_no);
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.unifiedOrder(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				JSONMessage message=new JSONMessage();
				message.push("appid", r.get("appid"));
				message.push("mch_id", r.get("mch_id"));
				message.push("nonce_str", r.get("nonce_str"));
				message.push("sign", r.get("sign"));
				message.push("result_code", r.get("result_code"));
				message.push("trade_type", r.get("trade_type"));
				message.push("prepay_id", r.get("prepay_id"));
				message.push("code_url", r.get("code_url"));
				result.setHtmlBody(message.toJSONString());
			}else{
				throw new MException(this.getClass(),r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}


	public ServiceResult wapPay(String out_trade_no,String pay_ip) throws MException {
		return pagePay(out_trade_no,pay_ip);
	}

	@Override
	public ServiceResult payQuery(String out_trade_no,String trade_no) throws MException {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", out_trade_no);
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.orderQuery(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				if("SUCCESS".equals(r.get("trade_state"))){
					result.setPayAmount(Double.parseDouble(r.get("total_fee"))/100);
					result.setStatus(1);
					result.setMsg(r.get("return_msg"));
				}else{
					result.setStatus(0);
					result.setMsg(r.get("return_msg"));
				}
			}else{
				result.setStatus(0);
				result.setMsg(r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}

    /**
     * 退款
     */
	@Override
	public ServiceResult refund(String out_trade_no, String trade_no,Double refund_amount,Double pay_amount, String refund_reason, String out_request_no) throws MException {
		wxpay=new WXPay(new WxPayConfig(PayData.getWxpay_appID(),PayData.getWxpay_apiKey(),PayData.getWxpay_mchID(),PayData.getWxpay_certLocalPath()),WXPayConstants.SignType.MD5);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", out_trade_no);
		data.put("out_refund_no", out_request_no);
		data.put("total_fee", "1");//String.valueOf(pay_amount*100).split("\\.")[0]
		data.put("refund_fee", "1");//String.valueOf(refund_amount*100).split("\\.")[0]
		data.put("refund_fee_type", "CNY");
		data.put("op_user_id", PayData.getWxpay_mchID());
		data.put("sign_type", "MD5");//签名类型
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.refund(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				if("SUCCESS".equals(r.get("result_code"))){
					result.setPayAmount(Double.parseDouble(r.get("total_fee"))/100);
					result.setRefundAmount(Double.parseDouble(r.get("refund_fee"))/100);
					result.setOutTradeNo(r.get("out_trade_no"));
					result.setOutRequestNo(r.get("out_refund_no"));
					result.setTradeNo(r.get("transaction_id"));
					result.setStatus(1);
				}else{
					result.setMsg(r.get("err_code_des"));
					result.setStatus(0);
				}
			}else{
				result.setStatus(0);
				result.setMsg(r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}

	/*
	 * 小程序退款
	 */
	public ServiceResult weChatPayrefund(String out_trade_no, String trade_no,Double refund_amount,Double pay_amount, String refund_reason, String out_request_no) throws MException {
		wxpay=new WXPay(new WxPayConfig("wxab144ac62dc61d10","703cbd89247fd4eef14f235373e21ec3","1499569722","/wxx/apiclient_cert.p12"),WXPayConstants.SignType.MD5);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", out_trade_no);
		data.put("out_refund_no", out_request_no);
		data.put("total_fee", "1");//String.valueOf(pay_amount*100).split("\\.")[0]
		data.put("refund_fee", "1");//String.valueOf(refund_amount*100).split("\\.")[0]
		data.put("refund_fee_type", "CNY");
		data.put("op_user_id", PayData.getWxpay_mchID());
		data.put("sign_type", "MD5");//签名类型
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.refund(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				if("SUCCESS".equals(r.get("result_code"))){
					result.setPayAmount(Double.parseDouble(r.get("total_fee"))/100);
					result.setRefundAmount(Double.parseDouble(r.get("refund_fee"))/100);
					result.setOutTradeNo(r.get("out_trade_no"));
					result.setOutRequestNo(r.get("out_refund_no"));
					result.setTradeNo(r.get("transaction_id"));
					result.setStatus(1);
				}else{
					result.setMsg(r.get("err_code_des"));
					result.setStatus(0);
				}
			}else{
				result.setStatus(0);
				result.setMsg(r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}

	@Override
	public ServiceResult refundQuery(String out_trade_no, String trade_no, String out_request_no) throws MException {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", out_trade_no);
		data.put("transaction_id", trade_no);
		data.put("out_refund_no", out_request_no);
		ServiceResult result=new ServiceResult();
		try {
			Map<String, String> r = wxpay.refundQuery(data);
			System.out.println(r);
			if("SUCCESS".equals(r.get("return_code"))){
				if("SUCCESS".equals(r.get("result_code"))){
					result.setOutTradeNo(r.get("out_trade_no"));
					result.setStatus(1);
				}else{
					result.setStatus(0);
					result.setMsg(r.get("err_code_des"));
				}
			}else{
				result.setStatus(0);
				result.setMsg(r.get("return_msg"));
			}
		} catch (Exception e) {
			throw new MException(this.getClass(),e.getMessage());
		}
		return result;
	}

	public static void main(String[] a) throws MException{
		ServiceResult result=new WxPayService().wapPay("20170707132238967001", "10.10.10.10");
//		ServiceResult result=new WxPayService().payQuery("20170707132238967001",null);
//		ServiceResult result=new WxPayService().refundQuery("20170707132238967001","","20170707132238967001");
//		new WxPayService().refund("201707061010001", null, 10.0, 10.0, "", "201707061010001");
		System.out.println(result);
	}
	@Override
	public ActionResult Accounts(String out_trade_no, String Account,
			String Money) throws MException, AlipayApiException {
		// TODO Auto-generated method stub
		return null;
	}
	
	  /**
     * 微信提现
     * @return
     * @throws Exception
     */

	public JSONMessage WxPaywithdrawals() throws Exception {
	System.out.println("微信提现----------");
		
		JSONMessage jsm = new JSONMessage();
		Date date=new Date();
//		String openid = member.getOpenid();
//        String valueOf = String.valueOf(Double.valueOf(money)*100);
//        String substring = valueOf.substring(0,valueOf.indexOf("."));
		String pay_id=GenerateID.generatePrimaryKey();	// 订单号
		TreeMap<String, String> parms = new TreeMap<String, String>(); 
		parms.put("mch_appid",PayData.getWxpay_appID());//企业公众号appid
		parms.put("mchid", PayData.getWxpay_mchID());//微信支付分配的商户号
		parms.put("nonce_str", pay_id);//随机字符串，不长于32位
		parms.put("amount","1");//企业付款金额，单位为分
		parms.put("desc", "提现款额");//企业付款描述信息
		parms.put("spbill_create_ip","39.106.46.31");//调用接口的机器Ip地址
		parms.put("openid", "");//用户openid
		parms.put("check_name", "NO_CHECK");//NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名,OPTION_CHECK：针对已实名认证的用户才校验真实姓名
//	    parms.put("re_user_name", "mch_appid");//如果check_name设置为FORCE_CHECK或OPTION_CHECK，则必填用户真实姓名
		parms.put("sign", SignTools.buildRequestMysign(parms));//签名
		
		String resultXML = HttpClientCustomSSL.httpClientResult(parms);//转账
		//交易结果处理
		Map<String, Object> resultMap =  XMLUtil.doXMLParse(resultXML);
        String return_code = (String) resultMap.get("return_code");
        String result_code = (String) resultMap.get("result_code");
        
        if (return_code.equalsIgnoreCase("SUCCESS") && result_code.equalsIgnoreCase("SUCCESS")){
        	//交易成功
//        	member.setBalance(member.getBalance()-Double.valueOf(money));
//        	ModelUpdateUtil.updateModel(member,new String[]{"balance"});
//        	getService(MemberService.class).withdraw(Double.valueOf(money),member,"3");	// 提现
             System.out.println("成功");
             jsm.push("status", 1);
             jsm.push("message", "操作成功！");
        }else{ 
        	//转账失败
        	// TODO: handle exception
        	System.out.println("失败");
        	jsm.push("status", 0);
          jsm.push("message", "操作失败！");
        }
	return jsm;
}


}

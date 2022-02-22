package m.pay.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.pay.PayData;
import m.pay.model.OutPayOrder;
import m.pay.service.PayService;
import m.pay.service.WxPayService;
import m.system.exception.MException;
import m.system.json.JsonReader;
import m.system.lang.HtmlBodyContent;
import m.system.util.JSONMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



@ActionMeta(name="payWxpay")
public class WxPayAction extends PayAction {
	private String out_trade_no;
	private String openid;
    /**
     *微信支付的回调
     */
	public HtmlBodyContent notifyPage(){
		try {
			Map<String,String> map=requestToXml(getRequest());
			out_trade_no=map.get("out_trade_no");
			PayService.WxPayDoneNotify(out_trade_no);
			System.out.println(out_trade_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(out_trade_no);
		PayService.WxPayDoneNotify(out_trade_no);
		return new HtmlBodyContent("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
	}
	public static Map<String,String> requestToXml(HttpServletRequest request) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder = domfac.newDocumentBuilder();
		Document doc=dombuilder.parse(request.getInputStream());
		Element root=doc.getDocumentElement();
		NodeList nodeList=root.getChildNodes();
		Map<String,String> map = new HashMap<String,String>();
		if(nodeList!=null){
			for(int i=0;i<nodeList.getLength();i++){
				Node node=nodeList.item(i);
				map.put(node.getNodeName(),node.getTextContent());
			}
		}
		return map;
	}
	public ActionResult returnPage() throws MException{
		ActionResult result=new ActionResult("pay/wxpay/returnPage");
		OutPayOrder order=PayService.getOutPayOrder(out_trade_no,"payWxpay");
		result.setHtmlBody(new StringBuffer(PayData.getPay_webPath()).append(order.getOutUrl()).toString());
		return result;
	}
	/**
	 * app支付
	 */
	@Override
	public JSONMessage appPay() throws MException {
		JSONMessage data=new JSONMessage();
		JsonReader json=new JsonReader(getService(WxPayService.class).appPay(getOut_trade_no(), getPayIp()).getHtmlBody());
		data.push("app_id",json.get(String.class,"appid"));
		data.push("partner_id",json.get(String.class,"mch_id"));
		data.push("nonce_str",json.get(String.class,"nonce_str"));
		data.push("sign",json.get(String.class,"sign"));
		data.push("prepay_id",json.get(String.class,"prepay_id"));
		data.push("package_value","Sign=WXPay");
		data.push("time_stamp",Calendar.getInstance().getTimeInMillis()/1000);
		data.push("status","1");
		data.push("message","获取成功");
		return data;
	}
	
	
	
	
	/**
	 *微信提现
	 */
	public JSONMessage WxPaywithdrawals() throws Exception {
		JSONMessage data=new JSONMessage();
		getService(WxPayService.class).WxPaywithdrawals();
		data.push("status","1");
		data.push("message","获取成功");
		return data;
	}
	
	
	@Override
	public ActionResult pagePay() throws MException {
		ActionResult result=new ActionResult("pay/wxpay/pagePay");
		result.setHtmlBody(getService(WxPayService.class).pagePay(getOutTradeNo(), getPayIp()).getHtmlBody());
		return result;
	}
	@Override
	public ActionResult wapPay() throws MException {
		ActionResult result=new ActionResult("pay/wxpay/wapPay");
		result.setHtmlBody(getService(WxPayService.class).wapPay(getOut_trade_no(), getPayIp()).getHtmlBody());
		return result;
	}
	public ActionResult toWxPay() throws MException, UnsupportedEncodingException{
		ActionResult result=new ActionResult("pay/wxpay/toWxPay");
		result.setHtmlBody(getService(WxPayService.class).getSnsapiUrl(getOutTradeNo()));
		return result;
	}
	
	private String code;
	private String state;
	
	public ActionResult doWxPay() throws MException, Exception{ 
		ActionResult result=new ActionResult("pay/wxpay/doWxPay");
		OutPayOrder order=WxPayService.getOutPayOrder(state,"payWxpay");
		result.setHtmlBody(getService(WxPayService.class).wxPay(code, order, getPayIp()).getHtmlBody());
		result.setPower(order);
		return result;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	@Override
	public JSONMessage Accounts(){
		return null;
	}
}

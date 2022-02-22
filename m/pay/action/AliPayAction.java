package m.pay.action;

import com.alipay.api.AlipayApiException;
import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.pay.PayData;
import m.pay.action.PayAction;
import m.pay.model.OutPayOrder;
import m.pay.service.AliPayService;
import m.pay.service.PayService;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;
import m.system.util.JSONMessage;

@ActionMeta(name="payAlipay")
public class AliPayAction extends PayAction {
	private String out_trade_no;
	private String Account;
	private String Money;
	
	/**
	 * 回调方法
	 */
	public HtmlBodyContent notifyPage(){
		PayService.AlipayPayDoneNotify(out_trade_no);
		return new HtmlBodyContent("success");
	}
	public ActionResult returnPage() throws MException{
		ActionResult result=new ActionResult("pay/alipay/returnPage");
		OutPayOrder order=PayService.getOutPayOrder(out_trade_no,"payAlipay");
		result.setHtmlBody(new StringBuffer(PayData.getPay_webPath()).append(order.getOutUrl()).toString());
		return result;
	}
	
	/**
	 * 支付宝APP支付
	 * @throws MException 
	 */
	@Override
	public JSONMessage appPay() throws Exception{
		JSONMessage json=new JSONMessage();
		json.push("data",getService(AliPayService.class).appPay(out_trade_no).getHtmlBody());
		json.push("status","1");
		json.push("message","获取成功");
		return json;
	}
	
	/**
	 * 页面支付
	 */
	@Override
	public ActionResult pagePay() throws MException {
		ActionResult result=new ActionResult("pay/alipay/pagePay");
		result.setHtmlBody(getService(AliPayService.class).pagePay(out_trade_no).getHtmlBody());
		return result;
	}
	@Override
	public ActionResult wapPay() throws MException {
		ActionResult result=new ActionResult("pay/alipay/wapPay");
		result.setHtmlBody(getService(AliPayService.class).wapPay(getOutTradeNo()).getHtmlBody());
		return result;
	}
	/**
	 * 支付宝转账提现
	 * @throws MException 
	 * @throws AlipayApiException 
	 */
	@Override
	public JSONMessage Accounts() throws MException, AlipayApiException {
		JSONMessage json=new JSONMessage();
		json.push("status", getService(AliPayService.class).Accounts(out_trade_no,Account,Money).getPower());
		return json;

	}
	
//	public static Map<String,String> requestToXml(HttpServletRequest request) throws ParserConfigurationException, SAXException, IOException {
//		Map<String,String> params = new HashMap<String,String>();
//		Map requestParams = request.getParameterMap();
//		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//		    String name = (String) iter.next();
//		    String[] values = (String[]) requestParams.get(name);
//		    String valueStr = "";
//		    for (int i = 0; i < values.length; i++) {
//		        valueStr = (i == values.length - 1) ? valueStr + values[i]
//		                    : valueStr + values[i] + ",";
//		  	}
//		    //乱码解决，这段代码在出现乱码时使用。
//			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//			params.put(name, valueStr);
//		}
//		//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
//		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
//		boolean flag = AlipaySignature.rsaCheckV1(params, alipaypublicKey, charset,"RSA2")
//	}
	
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getAccount() {
		return Account;
	}
	public void setAccount(String account) {
		Account = account;
	}
	public String getMoney() {
		return Money;
	}
	public void setMoney(String money) {
		Money = money;
	}
	
}


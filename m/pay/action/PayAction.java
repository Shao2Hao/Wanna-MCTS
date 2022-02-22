package m.pay.action;

import com.alipay.api.AlipayApiException;

import m.common.action.Action;
import m.common.action.ActionResult;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;
import m.system.util.JSONMessage;

public abstract class PayAction extends Action{
	private String outTradeNo;
	
	/**
	 * 接收通知方法
	 * @return
	 */
	public abstract HtmlBodyContent notifyPage();
	/**
	 * 回调页面
	 * @return
	 */
	public abstract ActionResult returnPage() throws MException;

	/**
	 * h5付款
	 * @return
	 */
	public abstract ActionResult wapPay() throws MException;
	/**
	 * 网站付款
	 * @return
	 */
	public abstract ActionResult pagePay() throws MException;
	/**
	 * app付款
	 * @return
	 * @throws MException 
	 */
	public abstract JSONMessage appPay() throws Exception;

	/**
	 *转账提现
	 * @return
	 * @throws MException
	 * @throws AlipayApiException 
	 */
	public abstract JSONMessage Accounts() throws MException, AlipayApiException;
	
	
	public String getPayIp(){
		String ip = getRequest().getHeader("x-forwarded-for"); 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getHeader("Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getHeader("WL-Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getRemoteAddr(); 
		} 
		return ip;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	
	
}

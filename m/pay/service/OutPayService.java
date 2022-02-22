package m.pay.service;

import m.pay.model.OutPayOrder;

public interface OutPayService {
	/**
	 * 获取订单信息
	 * @param outTradeNo
	 * @return
	 */
	public OutPayOrder getOutPayOrder(String outTradeNo,String paymentMethod);
	/**
	 * 支付宝支付完成后收到的通知
	 * @param outTradeNo
	 */
	public String AlipayPayDoneNotify(String outTradeNo);
	/**
	 * 微信支付完成后收到的通知
	 * @param outTradeNo
	 */
	public String WxPayDoneNotify(String outTradeNo);
}

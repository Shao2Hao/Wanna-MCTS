package m.pay.model;

public class OutPayOrder {

	//商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
	private String outTradeNo;
	//支付币种订单金额
	private Double payAmount;
	//对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body
	private String body;
	
	private String outUrl;
	
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public Double getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOutUrl() {
		return outUrl;
	}
	public void setOutUrl(String outUrl) {
		this.outUrl = outUrl;
	}
}

package m.pay.service;

import java.util.UUID;

import m.common.action.ActionResult;
import m.pay.PayData;
import m.pay.model.OutPayOrder;
import m.pay.service.PayService;
import m.pay.util.ServiceResult;
import m.system.exception.MException;
import m.system.json.JsonReader;
import m.system.util.StringUtil;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;

public class AliPayService extends PayService {
	private static AlipayClient client;
	public AliPayService(){
		PayData.init();
		if(null==client){
			client = new DefaultAlipayClient(PayData.getAlipay_url(), PayData.getAlipay_appID(), 
				PayData.getAlipay_rsaPrivateKey(), PayData.getAlipay_format(), PayData.getAlipay_charset(), 
				PayData.getAlipay_alipayPublicKey(),PayData.getAlipay_signType());
		}
	}
	public ServiceResult wapPay(String out_trade_no) throws MException {
		OutPayOrder order=getOutPayOrder(out_trade_no,"payAlipay");
		AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();// 封装请求支付信息
		AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
		//订单编号
		model.setOutTradeNo(out_trade_no);
		//订单标题
		model.setSubject(order.getBody());
		//订单总金额，单位为元，精确到小数点后两位
		model.setTotalAmount(order.getPayAmount().toString());
		//订单描述
		model.setBody(order.getBody());
		//该笔订单允许的最晚付款时间，逾期将关闭交易
		model.setTimeoutExpress("20m");
		//销售产品码
		model.setProductCode("QUICK_WAP_WAY");
		alipay_request.setBizModel(model);
	    // 设置异步通知地址
	    alipay_request.setNotifyUrl(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_notifyUrl()).toString());
	    // 设置同步地址
	    alipay_request.setReturnUrl(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_returnUrl()).toString());  
		try {
			ServiceResult result=new ServiceResult();
			AlipayTradeWapPayResponse response=client.pageExecute(alipay_request);
			result.setHtmlBody(response.getBody());
			result.setStatus(1);
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}
	}
	public ServiceResult pagePay(String out_trade_no) throws MException {
		OutPayOrder order=getOutPayOrder(out_trade_no,"payAlipay");
		AlipayTradePagePayRequest alipay_request=new AlipayTradePagePayRequest();// 封装请求支付信息
		AlipayTradePagePayModel model=new AlipayTradePagePayModel();
		model.setOutTradeNo(out_trade_no);
		model.setSubject(order.getBody());
		model.setTotalAmount(order.getPayAmount().toString());
		model.setBody(order.getBody());
		model.setTimeoutExpress("20m");
		model.setProductCode("FAST_INSTANT_TRADE_PAY");
		alipay_request.setBizModel(model);
	    // 设置异步通知地址
	    alipay_request.setNotifyUrl(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_notifyUrl()).toString());
	    // 设置同步地址
	    alipay_request.setReturnUrl(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_returnUrl()).toString());  
		try {
			ServiceResult result=new ServiceResult();
			AlipayTradePagePayResponse response=client.pageExecute(alipay_request);
			result.setHtmlBody(response.getBody());
			result.setStatus(1);
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}
	}
	/**
	 * app支付
	 * @param out_trade_no
	 * @return
	 * @throws MException
	 */
	public ServiceResult appPay(String out_trade_no) throws Exception {
		OutPayOrder order=getOutPayOrder(out_trade_no,"payAlipay");
		System.out.print(out_trade_no);
		AlipayTradeAppPayRequest alipay_request = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		
		//对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body
		model.setBody(order.getBody());//order.getBody()
		
		//商品的标题/交易标题/订单标题/订单关键字等
		model.setSubject(order.getBody());//order.getBody()
		
		//商户网站唯一订单号
		model.setOutTradeNo(out_trade_no);
		
		//该笔订单允许的最晚付款时间，逾期将关闭交易(20分钟)
		model.setTimeoutExpress("20m");
		
		//订单总金额，单位为元，精确到小数点后两位
		model.setTotalAmount(order.getPayAmount().toString());//order.getPayAmount().toString()
		 
		//销售产品码，商家和支付宝签约的产品码
		model.setProductCode("QUICK_MSECURITY_PAY");
		
		alipay_request.setBizModel(model);
	    
		// 设置异步通知地址
		//notifyUrl支付宝服务器主动通知商户服务器里指定的页面http/https路径。
	    alipay_request.setNotifyUrl(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_notifyUrl()).toString());
	    System.out.println(new StringBuffer(PayData.getPay_webPath()).append(PayData.getAlipay_notifyUrl()).toString());
		try {
			ServiceResult result=new ServiceResult();
			AlipayTradeAppPayResponse response = client.sdkExecute(alipay_request);
			result.setHtmlBody(response.getBody());
			System.out.print(response.getBody());
			result.setStatus(1);
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new MException(this.getClass(),e.getMessage());
		}
	}

	@Override
	public ServiceResult payQuery(String out_trade_no,String trade_no) throws MException {
		AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
		AlipayTradeQueryModel model=new AlipayTradeQueryModel();
	    model.setOutTradeNo(out_trade_no);
	    model.setTradeNo(trade_no);
	    alipay_request.setBizModel(model);
		try {        
			ServiceResult result=new ServiceResult();
			AlipayTradeQueryResponse alipay_response = client.execute(alipay_request);
			JsonReader json=new JsonReader(alipay_response.getBody()).get(JsonReader.class, "alipay_trade_query_response");
			if(null==json) throw new MException(this.getClass(),"请求异常!");
			if("10000".equals(json.get(String.class,"code"))){
				result.setMsg(json.get(String.class,"msg"));
				result.setOutTradeNo(json.get(String.class,"out_trade_no"));
				result.setPayAmount(Double.parseDouble(json.get(String.class,"total_amount")));
				result.setTradeNo(json.get(String.class,"trade_no"));
				result.setStatus(1);
			}else{
				result.setMsg(json.get(String.class,"sub_msg"));
				result.setStatus(0);
			}
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}
	}
    /**
     * 退款接口
     */
	@Override
	public ServiceResult refund(String out_trade_no,String trade_no,Double refund_amount,Double pay_amount,String refund_reason,String out_request_no) throws MException {
		AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model=new AlipayTradeRefundModel();
		//订单支付时传入的商户订单号,不能和 trade_no同时为空。
		model.setOutTradeNo(out_trade_no);
		//支付宝交易号，和商户订单号不能同时为空
//		model.setTradeNo("2018022621001004210500070745");
		//需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
		model.setRefundAmount("0.01");
		//退款的原因说明
//		model.setRefundReason(refund_reason);
		//标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
//		model.setOutRequestNo(out_request_no);
		
//		model.setRefundReason(refundReason)
		alipay_request.setBizModel(model);
		try {
			ServiceResult result=new ServiceResult();
			AlipayTradeRefundResponse alipay_response =client.execute(alipay_request);
			JsonReader json=new JsonReader(alipay_response.getBody()).get(JsonReader.class, "alipay_trade_refund_response");
			if(null==json) throw new MException(this.getClass(),"请求异常!");
			if("10000".equals(json.get(String.class,"code"))){
				//网关返回码描述（公共响应参数）
				result.setMsg(json.get(String.class,"msg"));
				//商户订单号
				result.setOutTradeNo(json.get(String.class,"out_trade_no"));
				//退款总金额
				result.setRefundAmount(Double.parseDouble(json.get(String.class,"refund_fee")));
				//支付宝交易号
				result.setTradeNo(json.get(String.class,"trade_no"));
				//本次退款是否发生了资金变化
				String fund_change=json.get(String.class,"fund_change");
				if("Y".equals(fund_change)){
					result.setStatus(1);
				}else{
					result.setMsg("订单已退款!");
					result.setStatus(0);
				}
			}else{
				//业务返回码描述（公共响应参数）
				result.setMsg(json.get(String.class,"sub_msg"));
				result.setStatus(0);
			}
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}
	}
	/**
	 * 转账提现
	 */
	@Override
	public ActionResult Accounts(String out_trade_no,String Account,String Money) throws AlipayApiException {
		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
		AlipayFundTransToaccountTransferModel  model=new AlipayFundTransToaccountTransferModel();
		String uuid=UUID.randomUUID().toString();
		model.setOutBizNo(uuid);//订单号
	    model.setPayeeType("ALIPAY_LOGONID");//固定值
		model.setPayeeAccount(Account);//转账收款账户
		model.setAmount(Money);//转账金额
		model.setPayerShowName("易优帮教");//名称
		model.setPayerRealName("");//账户真实名称
		model.setRemark("提现");//备注
		request.setBizModel(model);
		ActionResult result=new ActionResult("");
		AlipayFundTransToaccountTransferResponse response = client.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
			result.setPower("1");
		} else {
			System.out.println("调用失败");
			result.setPower(response.getSubMsg());
		}
		return result;
	}
	@Override
	public ServiceResult refundQuery(String out_trade_no,String trade_no,String out_request_no) throws MException {
		AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();
		AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
		model.setOutTradeNo(out_trade_no);
		model.setTradeNo(trade_no);
		model.setOutRequestNo(out_request_no);
		alipay_request.setBizModel(model);
		try {
			ServiceResult result=new ServiceResult();
			AlipayTradeFastpayRefundQueryResponse alipay_response=client.execute(alipay_request);
			JsonReader json=new JsonReader(alipay_response.getBody()).get(JsonReader.class, "alipay_trade_fastpay_refund_query_response");
			if(null==json) throw new MException(this.getClass(),"请求异常!");
			if("10000".equals(json.get(String.class,"code"))){
				result.setMsg(json.get(String.class,"msg"));
				result.setOutTradeNo(json.get(String.class,"out_trade_no"));
				String refund_amount=json.get(String.class,"refund_amount");
				if(!StringUtil.isSpace(refund_amount))
					result.setRefundAmount(Double.parseDouble(refund_amount));
				result.setTradeNo(json.get(String.class,"trade_no"));
				if(null==result.getRefundAmount()){
					result.setMsg("无退款信息!");
					result.setStatus(0);
				}else{
					result.setStatus(1);
				}
			}else{
				result.setMsg(json.get(String.class,"sub_msg"));
				result.setStatus(0);
			}
			return result;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			throw new MException(this.getClass(),e.getMessage());
		}
	}
	public static void main(String[] a) throws MException{
//		AliPayService service=new AliPayService();
//		ServiceResult result=service.wapPay("20170626001", "付款200元", 200.0, "200元商品!");
//		ServiceResult result=service.appPay("20170626002", "付款200元", 200.0, "200元商品!");
//		System.out.println(result.getHtmlBody());
//		ServiceResult result=service.payQuery("2017626001011", null);
//		ServiceResult result=service.close("201762601", null);
//		ServiceResult result=service.refund("20170626001", null,100.0,"","2017062600101");
//		ServiceResult result=service.refundQuery("20170629132629160001", null,"20170629132629160001");
//		System.out.println("---------");
	}
}

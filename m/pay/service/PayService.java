package m.pay.service;

import java.util.ArrayList;
import java.util.List;

import com.alipay.api.AlipayApiException;

import m.common.action.ActionResult;
import m.common.service.Service;
import m.pay.PayData;
import m.pay.model.OutPayOrder;
import m.pay.util.ServiceResult;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.ClassUtil;

public abstract class PayService extends Service {
	/**
	 * 获取支付服务类
	 * @param payType:payWxpay微信  payAlipay支付宝
	 * @return
	 * @throws MException
	 */
	public static PayService get(String payType) throws MException{
		if("payWxpay".equals(payType)){
			return RuntimeData.getService(WxPayService.class);
		}else if("payAlipay".equals(payType)){
			return RuntimeData.getService(AliPayService.class);
		}else{
			throw new MException(PayService.class,"支付类型错误!");
		}
	}
	private static List<OutPayService> outServiceList=null;
	private static void initServiceList(){
		if(null==outServiceList){
			try {
				outServiceList=new ArrayList<OutPayService>();
				String[] strs=PayData.getPay_serviceClass().split(",");
				for(String s : strs){
					outServiceList.add((OutPayService) ClassUtil.newInstance(s));
				}
			} catch (MException e) {
				System.out.println("初始化支付订单服务类失败!");
				outServiceList=null;
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取商户订单信息
	 * @param outTradeNo
	 * @return
	 * @throws MException
	 */
	public static OutPayOrder getOutPayOrder(String outTradeNo,String paymentMethod){
		initServiceList();
		OutPayOrder order=null;
		for(OutPayService service : outServiceList){
			order=service.getOutPayOrder(outTradeNo,paymentMethod);
			if(null!=order) break;
		}
		return order;
	}
	public static void AlipayPayDoneNotify(String out_trade_no){
		initServiceList();
		for(OutPayService service : outServiceList){
			service.AlipayPayDoneNotify(out_trade_no);
		}
	}
	public static void WxPayDoneNotify(String out_trade_no){
		initServiceList();
		for(OutPayService service : outServiceList){
			service.WxPayDoneNotify(out_trade_no);
		}
	}
	/**
	 * 付款查询
	 * @param out_trade_no 商家订单号
	 * @param trade_no 交易号
	 * @return
	 */
	public abstract ServiceResult payQuery(String out_trade_no,String trade_no) throws MException;
	/**
	 * 退款
	 * @param out_trade_no 商家订单号
	 * @param trade_no 交易号
	 * @param refund_amount 退款金额
	 * @param pay_amount 订单金额
	 * @param refund_reason 退款说明
	 * @param out_request_no 退款号, 一个订单退多次,保证唯一
	 * @return
	 * @throws MException
	 */
	public abstract ServiceResult refund(String out_trade_no,String trade_no,Double refund_amount,Double pay_amount,String refund_reason,String out_request_no) throws MException ;
	/**
	 * 退款查询
	 * @param out_trade_no 商家订单号
	 * @param trade_no 交易号
	 * @param out_request_no 退款号
	 * @return
	 * @throws MException
	 */
	public abstract ServiceResult refundQuery(String out_trade_no,String trade_no,String out_request_no) throws MException ;
	/**
	 * 转账提现
	 * @param out_trade_no
	 * @param trade_no
	 * @param out_request_no
	 * @return
	 * @throws MException
	 * @throws AlipayApiException 
	 */
	public abstract ActionResult Accounts(String out_trade_no, String Account, String Money) throws MException, AlipayApiException ;

	

}

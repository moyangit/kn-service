package com.tsn.serv.pay.controller.stripe;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("stripe")
public class StripeController {
	
	private static final Logger logger = LoggerFactory.getLogger(StripeController.class);

	@PostMapping("trade")
	public Response<?> trade(@RequestBody PayParam param) {

		try {
			
			Stripe.apiKey = Env.getVal("fasthw_stripe_key");
			
			String successCallBack = Env.getVal("fasthw_stripe_success_callback");
			
			String cancelCallBack = Env.getVal("fasthw_stripe_cancel_callback");
			
			// 创建一个结账会话
			SessionCreateParams params = SessionCreateParams.builder()
			// 指定付款方式
			.addPaymentMethodType(
					SessionCreateParams.PaymentMethodType.CARD)
			// 指定结账模式
			.setMode(SessionCreateParams.Mode.PAYMENT)
			// 设置支付成功页面
			.setSuccessUrl(successCallBack)
			// 设置支付取消页面
			.setCancelUrl(cancelCallBack)
			// 定义订单项
			.addLineItem(SessionCreateParams.LineItem.builder()
			// 数量
			.setQuantity(1L)
			.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
				// 货币
				.setCurrency("usd")
				// 金额
				.setUnitAmount((long)Double.parseDouble(param.getTotalAmount()) * 100)
				.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(param.getSubject()).build())// 商品名称
				.build()).build()).build();
			Session session = Session.create(params);
			HashMap<String, String> responseData = new HashMap<String, String>();
			responseData.put("stripeSid", session.getId());

			return Response.ok(responseData);
		} catch (Exception e) {
			logger.error("stripe trade fail, e = {}", e);
			return Response.retn(ResultCode.UNKNOW_ERROR);
		}
	}
	
	 /**
     * 检索订单
     * @param sessionId
     * @return
     */
    @GetMapping("/query")
    public Response<?> retrieve(String sId) {
    	Stripe.apiKey = Env.getVal("fasthw_stripe_key");

        try {
            // Map<String, Object> params = new HashMap<>();
            // params.put("limit", 5);
            // 获取订单项商品信息
            // LineItemCollection lineItems = session.listLineItems(params);
            // 根据sessionId获取最后的操作信息
            Session session = Session.retrieve(sId);
            // 订单信息
            @SuppressWarnings("unchecked")
			Map<String, String> map = JsonUtils.jsonToPojo(session.getLastResponse().body(), Map.class);
            /**
             * lineItems : 订单项信息
             * amount_total : 金额
             * currency : 货币
             * customer_details : 用户信息
             * payment_status : 支付状态
             *          paid : The payment funds are available in your account.
             *          unpaid : The payment funds are not yet available in your account.
             *          no_payment_required : The Checkout Session is in setup mode and doesn’t require a payment at this time.
             *
             */
            return Response.ok(map);
        } catch (StripeException e) {
        	logger.error(e.toString(), e);
        }

        return Response.ok();
    }
	
	public static class PayParam {
		
		private String outTradeNo;
		
		private String subject;
		
		private String totalAmount;
		
		private String timeout;

		public String getOutTradeNo() {
			return outTradeNo;
		}

		public void setOutTradeNo(String outTradeNo) {
			this.outTradeNo = outTradeNo;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(String totalAmount) {
			this.totalAmount = totalAmount;
		}

		public String getTimeout() {
			return timeout;
		}

		public void setTimeout(String timeout) {
			this.timeout = timeout;
		}
		
	}

}

package com.zpstudio.datacenter.db.weixin.pay;

public class PayReqEx {
	/*
	公众账号ID appid String(32) 是 wx8888888888888888 微信分配的公众账号ID 
	商户号 partnerid String(32) 是 1900000109 微信支付分配的商户号 
	预支付交易会话ID prepayid String(32) 是 WX1217752501201407033233368018 微信返回的支付交易会话ID 
	扩展字段 package String(128) 是 Sign=WXPay 暂填写固定值Sign=WXPay 
	随机字符串 noncestr String(32) 是 5K8264ILTKCH16CQ2502SI8ZNMTM67VS 随机字符串，不长于32位。推荐随机数生成算法 
	时间戳 timestamp String(10) 是 1412000000 时间戳，请见接口规则-参数规定 
	签名 sign String(32) 是 C380BEC2BFD727A4B6845133519F3AD6 签名，详见签名生成算法 
	*/
	String appId;
	String partnerId;
	String prepayId;
	String nonceStr;
	String timeStamp;
	String packageValue;
	String sign;
	
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the partnerId
	 */
	public String getPartnerId() {
		return partnerId;
	}

	/**
	 * @param partnerId the partnerId to set
	 */
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	/**
	 * @return the prepayId
	 */
	public String getPrepayId() {
		return prepayId;
	}

	/**
	 * @param prepayId the prepayId to set
	 */
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	/**
	 * @return the nonceStr
	 */
	public String getNonceStr() {
		return nonceStr;
	}

	/**
	 * @param nonceStr the nonceStr to set
	 */
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the packageValue
	 */
	public String getPackageValue() {
		return packageValue;
	}

	/**
	 * @param packageValue the packageValue to set
	 */
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}

	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}


	
}

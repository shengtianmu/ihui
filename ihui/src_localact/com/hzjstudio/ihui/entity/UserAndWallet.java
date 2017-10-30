package com.hzjstudio.ihui.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAndWallet {

	/**
	 * 内部类实现单例模式 延迟加载，减少内存开销
	 * 
	 * @author xuzhaohu
	 * 
	 */
	private static class SingletonHolder {
		private static UserAndWallet instance = new UserAndWallet();
	}

	/**
	 * 私有的构造函数
	 */
	private UserAndWallet() {

	}

	public static UserAndWallet getInstance() {
		return SingletonHolder.instance;
	}

	protected void method() {
		System.out.println("SingletonInner");
	}

	public int id;// 用户id
	public String phone_number;// 用户电话
	public String password;// 密碼
	public String sex;// 性别
	public int age;// 年龄
	public String name;// 姓名
	public String model_number;// 型号
	public String country;// 国家
	public String province;// 省
	public String city;// 城市
	public double credit;// 余额
	public int security_level;// 安全等级
	public String display_resolution;// 显示屏幕分辨率
	public String system_version;// 系统版本
	public String url = "http://m.02727.cn:8080//portraits/13482750440.png";// 用户的头像连接
	public int donate;// 捐赠
	public String registertime;// 注册时间
	public String birthday;// 出生日期
	// 以下是wallet
	public String wallet_phone_number;// 钱包的电话号码
	public int wallet_total_credit;// 钱包总积分
	public int wallet_total_donate;// 钱包总捐款
	public int wallet_register_credit;// 钱包信贷登记
	public int wallet_unlock_credit;// 钱包解锁信贷
	public int wallet_attention_credit;// 钱包关注信贷
	public int wallet_referral_credit;// 钱包推荐信用
	public int wallet_exchange_credit;// 钱包交换信用
	public int wallet_reward_credit;// 钱包奖励信贷
	public int wallet_punish_credit;// 钱包惩罚信贷
	public int wallet_notify_credit;// 钱包通知信用
	public int wallet_version;// 钱包版本

	/**
	 * 解析出UserAndWallet对象
	 * 
	 * @param jsonString
	 * @return
	 */

	public static UserAndWallet parse(String jsonString) {
		UserAndWallet userAndWallet = UserAndWallet.getInstance();
		try {
			System.out.println(jsonString);
			JSONObject jsonobj_user = new JSONObject(jsonString);
			jsonobj_user = jsonobj_user.getJSONObject("userAndWallet");
			jsonobj_user = jsonobj_user.getJSONObject("me");
			userAndWallet.id = jsonobj_user.getInt("id");
			userAndWallet.phone_number = jsonobj_user.getString("phone_number");
			userAndWallet.sex = jsonobj_user.getString("sex");
			userAndWallet.age = jsonobj_user.getInt("age");
			userAndWallet.password = jsonobj_user.getString("password");
			userAndWallet.name = jsonobj_user.getString("name");
			userAndWallet.model_number = jsonobj_user.getString("model_number");
			userAndWallet.country = jsonobj_user.getString("country");
			userAndWallet.city = jsonobj_user.getString("city");
			userAndWallet.credit = jsonobj_user.getDouble("credit");
			userAndWallet.security_level = jsonobj_user
					.getInt("security_level");
			userAndWallet.display_resolution = jsonobj_user
					.getString("display_resolution");
			userAndWallet.system_version = jsonobj_user
					.getString("system_version");
			userAndWallet.url = "http://m.02727.cn:8080/"+jsonobj_user.getString("url")+"?";
			userAndWallet.donate = jsonobj_user.getInt("donate");
			userAndWallet.registertime = jsonobj_user.getString("registertime");
			userAndWallet.birthday = jsonobj_user.getString("birthday");

			JSONObject jsonobj_wallet = new JSONObject(jsonString);
			jsonobj_wallet = jsonobj_wallet.getJSONObject("wallet");
			userAndWallet.wallet_phone_number = jsonobj_wallet
					.getString("phone_number");
			userAndWallet.wallet_total_credit = jsonobj_wallet
					.getInt("total_credit");
			userAndWallet.wallet_total_donate = jsonobj_wallet
					.getInt("total_donate");
			userAndWallet.wallet_register_credit = jsonobj_wallet
					.getInt("register_credit");
			userAndWallet.wallet_unlock_credit = jsonobj_wallet
					.getInt("unlock_credit");
			userAndWallet.wallet_attention_credit = jsonobj_wallet
					.getInt("attention_credit");
			userAndWallet.wallet_referral_credit = jsonobj_wallet
					.getInt("referral_credit");
			userAndWallet.wallet_exchange_credit = jsonobj_wallet
					.getInt("exchange_credit");
			userAndWallet.wallet_reward_credit = jsonobj_wallet
					.getInt("reward_credit");
			userAndWallet.wallet_punish_credit = jsonobj_wallet
					.getInt("punish_credit");
			userAndWallet.wallet_notify_credit = jsonobj_wallet
					.getInt("notify_credit");
			userAndWallet.wallet_version = jsonobj_wallet.getInt("version");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userAndWallet;

	}

	@Override
	public String toString() {
		return "UserAndWallet [id=" + id + ", phone_number=" + phone_number
				+ ", password=" + password + ", sex=" + sex + ", age=" + age
				+ ", name=" + name + ", model_number=" + model_number
				+ ", country=" + country + ", province=" + province + ", city="
				+ city + ", credit=" + credit + ", security_level="
				+ security_level + ", display_resolution=" + display_resolution
				+ ", system_version=" + system_version + ", url=" + url
				+ ", donate=" + donate + ", registertime=" + registertime
				+ ", birthday=" + birthday + ", wallet_phone_number="
				+ wallet_phone_number + ", wallet_total_credit="
				+ wallet_total_credit + ", wallet_total_donate="
				+ wallet_total_donate + ", wallet_register_credit="
				+ wallet_register_credit + ", wallet_unlock_credit="
				+ wallet_unlock_credit + ", wallet_attention_credit="
				+ wallet_attention_credit + ", wallet_referral_credit="
				+ wallet_referral_credit + ", wallet_exchange_credit="
				+ wallet_exchange_credit + ", wallet_reward_credit="
				+ wallet_reward_credit + ", wallet_punish_credit="
				+ wallet_punish_credit + ", wallet_notify_credit="
				+ wallet_notify_credit + ", wallet_version=" + wallet_version
				+ "]";
	}

}

package com.zpstudio.util;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

/**
 * 实现微信分享功能的核心类
 * @author Administrator
 *
 */
public class WeixinShareManager{
	
	private static final int THUMB_SIZE = 150;
	/**
	 * 文字
	 */
	public static final int WEIXIN_SHARE_WAY_TEXT = 1;
	/**
	 * 图片
	 */
	public static final int WEIXIN_SHARE_WAY_PIC = 2;
	/**
	 * 链接
	 */
	public static final int WEIXIN_SHARE_WAY_WEBPAGE = 3;
	/**
	 * 会话
	 */
	public static final int WEIXIN_SHARE_TYPE_SESSION = SendMessageToWX.Req.WXSceneSession;  
	/**
	 * 朋友圈
	 */
	public static final int WEIXIN_SHARE_TYPE_TIMELINE = SendMessageToWX.Req.WXSceneTimeline;
	private static WeixinShareManager instance;
	private static String weixinAppId;
	private IWXAPI wxApi;
	private Context context;
	
	private WeixinShareManager(Context context , String weixinAppId){
		this.context = context;
		//初始化数据
		this.weixinAppId = weixinAppId;
		//初始化微信分享代码
		if(weixinAppId != null){
			initWeixinShare(context);
		}
	}
	
	/**
	 * 获取WeixinShareManager实例
	 * 非线程安全，请在UI线程中操作
	 * @return
	 */
	public static WeixinShareManager getInstance(Context context){
		if(instance == null){
			instance = new WeixinShareManager(context, IhuiClientApi.OPEN_WEIXIN_APP_ID);
		}
		return instance;
	}
	
	private void initWeixinShare(Context context){
		wxApi = WXAPIFactory.createWXAPI(context, weixinAppId, true);
		wxApi.registerApp(weixinAppId);
	}
	
	/**
	 * 通过微信分享
	 * @param shareWay 分享的方式（文本、图片、链接）
	 * @param shareType 分享的类型（朋友圈，会话）
	 */
	public boolean shareByWeixin(ShareContent shareContent, int shareType){
		switch (shareContent.getShareWay()) {
		case WEIXIN_SHARE_WAY_TEXT:
			return shareText(shareType, shareContent);
		case WEIXIN_SHARE_WAY_PIC:
			break;
		case WEIXIN_SHARE_WAY_WEBPAGE:
			return shareWebPage(shareType, shareContent);
		}
		return false;
	}
	
	private abstract class ShareContent{
		protected abstract int getShareWay();
		protected abstract String getContent();
		protected abstract String getTitle();
		protected abstract String getURL();
		protected abstract byte[] getPicResource();
		
	}
	
	/**
	 * 设置分享文字的内容
	 * @author Administrator
	 *
	 */
	public class ShareContentText extends ShareContent{
		private String content;
		
		/**
		 * 构造分享文字类
		 * @param text 分享的文字内容
		 */
		public ShareContentText(String content){
			this.content = content;
		}

		@Override
		protected String getContent() {

			return content;
		}

		@Override
		protected String getTitle() {
			return null;
		}

		@Override
		protected String getURL() {
			return null;
		}

		@Override
		protected byte[] getPicResource() {
			return null;
		}

		@Override
		protected int getShareWay() {
			return WEIXIN_SHARE_WAY_TEXT;
		}
		
	}
	
	/**
	 * 设置分享图片的内容
	 * @author Administrator
	 *
	 */
	public class ShareContentPic extends ShareContent{
		private byte[] picResource;
		public ShareContentPic(byte[] picResource){
			this.picResource = picResource;
		}
		
		@Override
		protected String getContent() {
			return null;
		}

		@Override
		protected String getTitle() {
			return null;
		}

		@Override
		protected String getURL() {
			return null;
		}

		@Override
		protected byte[] getPicResource() {
			return picResource;
		}

		@Override
		protected int getShareWay() {
			return WEIXIN_SHARE_WAY_PIC;
		}
	}
	
	/**
	 * 设置分享链接的内容
	 * @author Administrator
	 *
	 */
	public class ShareContentWebpage extends ShareContent{
		private String title;
		private String content;
		private String url;
		private byte[] picResource;
		public ShareContentWebpage(String title, String content, 
				String url, byte[] picResource){
			this.title = title;
			this.content = content;
			this.url = url;
			this.picResource = picResource;
		}

		@Override
		protected String getContent() {
			return content;
		}

		@Override
		protected String getTitle() {
			return title;
		}

		@Override
		protected String getURL() {
			return url;
		}

		@Override
		protected byte[] getPicResource() {
			return picResource;
		}

		@Override
		protected int getShareWay() {
			return WEIXIN_SHARE_WAY_WEBPAGE;
		}
		
	}
	
	/*
	 * 分享文字
	 */
	private boolean shareText(int shareType, ShareContent shareContent) {
		String text = shareContent.getContent();
		//初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;
		//用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = text;
		//构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		//transaction字段用于唯一标识一个请求
		req.transaction = buildTransaction("textshare");
		req.message = msg;
		//发送的目标场景， 可以选择发送到会话 WXSceneSession 或者朋友圈 WXSceneTimeline。 默认发送到会话。
		req.scene = shareType;
		return wxApi.sendReq(req);
	}

	
	/*
	 * 分享链接
	 */
	private boolean shareWebPage(int shareType, ShareContent shareContent) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = shareContent.getURL();
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = shareContent.getTitle();
		msg.description = shareContent.getContent();
		
		msg.thumbData = shareContent.getPicResource();
		
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = shareType;
		return wxApi.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	
}

package com.hzjstudio.ihui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzjstudio.ihui.utils.CircularImage;
import com.hzjstudio.ihui.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnLogin;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnPortraitChanged;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;
import com.zpstudio.ui.util.DialogUtil;
import com.zpstudio.ui.util.imagepicker.ActImagePicker;

public class Fragment4 extends Fragment implements OnClickListener {
	final static String TAG = "Fragment4";
	/* 请求识别码 */
	private final static int FILECHOOSER_RESULTCODE = 1;

	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
	private static int output_X = 300;
	private static int output_Y = 300;

	private TextView tv_textView3;
	private CircularImage user_portrait;
	private TextView textview_name;
	private TextView textview_shoujihao;
	private ImageView imageview_xinxi;
	private RelativeLayout rl_quanbudingdan;
//	private RelativeLayout rl_ic_wode;
	private RelativeLayout rel_layout_wode;
//	private RelativeLayout rl_jifenmingxi;
	private RelativeLayout rl_tuijianzhuce;
	private RelativeLayout rl_wentifankui;
	private RelativeLayout rl_genhuanzhanghao;
	private RelativeLayout rl_guanyuaihui;

	private RelativeLayout rl_indiana;
	private RelativeLayout rl_record;
	private RelativeLayout rl_receiving;
//	private RelativeLayout rl_details;
	// UISwitchButton switch1;
	private View view;

	public final static String SHARED_MAIN_XML = "ps.xml";

	User mUser = null;
	Wallet mWallet = null;
	Dialog mLoadingDlg = null;
	ListenerOnUpdateUserAndWallet listener = new ListenerOnUpdateUserAndWallet() {

		@Override
		public void onSuccess(User user, Wallet wallet) {
			// TODO Auto-generated method stub
			mUser = user;
			mWallet = wallet;
			updateView();
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub

		}

	};
	private RelativeLayout rl_quanbudingdan2;
	private RelativeLayout rl_tjm ;
	private LinearLayout ll_tjm;
	private CustomDialog dialog;
	private EditText dialog_integral_et;
	private TextView tv_tjm2;

	public static Fragment4 newInstance() {
		Fragment4 fragmentlist = new Fragment4();
		return fragmentlist;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.com_hzjstudio__ihui_fragment4,
					container, false);
			init(view);
		} else {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}

		return view;
	}

	/**
	 * 将数据显示到控件上
	 */
	private void updateView() {
		textview_name.setText(mUser.getName());
		textview_shoujihao.setText(mUser.getDisplayPhoneNumber());
		tv_textView3.setText(mUser.getDisplayPhoneNumber());
		ImageLoader.getInstance()
				.displayImage(Config.getFullUrl(getActivity(), mUser.getUrl()),
						user_portrait);
		boolean status = IhuiClientApi.getInstance(getActivity())
				.getScreenSaverSetting();
		// switch1.setChecked(status);
		if ("未绑定".equals(mUser.getDisplayPhoneNumber())) {
			ll_tjm.setVisibility(View.GONE);
		}else {
			ll_tjm.setVisibility(View.VISIBLE);
			if (mUser.getReferee_id()!=0) {
				tv_tjm2.setText(mUser.getReferee_invitation_code());
			}
			
			
		}
		
	}

	private void init(View view) {

		mUser = IhuiClientApi.getInstance(getActivity()).getUser();
		mWallet = IhuiClientApi.getInstance(getActivity()).getWallet();
		tv_tjm2 = (TextView) view.findViewById(R.id.tv_tjm2);
		tv_textView3 = (TextView) view.findViewById(R.id.tv_textView3);
		user_portrait = (CircularImage) view.findViewById(R.id.user_portrait);
		user_portrait
				.setImageResource(R.drawable.com_hzjstudio__ihui_default_avatar);
		textview_name = (TextView) view.findViewById(R.id.textview_name);
		textview_shoujihao = (TextView) view
				.findViewById(R.id.textview_shoujihao);
		imageview_xinxi = (ImageView) view.findViewById(R.id.imageview_xinxi);
		rl_quanbudingdan = (RelativeLayout) view
				.findViewById(R.id.rl_quanbudingdan);
		rl_quanbudingdan2 = (RelativeLayout) view
				.findViewById(R.id.rl_quanbudingdan2);
//		rl_ic_wode = (RelativeLayout) view.findViewById(R.id.rl_ic_wode);
		rel_layout_wode = (RelativeLayout) view
				.findViewById(R.id.rel_layout_wode);
//		rl_jifenmingxi = (RelativeLayout) view
//				.findViewById(R.id.rl_jifenmingxi);
		rl_tuijianzhuce = (RelativeLayout) view
				.findViewById(R.id.rl_tuijianzhuce);
		rl_wentifankui = (RelativeLayout) view
				.findViewById(R.id.rl_wentifankui);
		rl_genhuanzhanghao = (RelativeLayout) view
				.findViewById(R.id.rl_genhuanzhanghao);
		rl_guanyuaihui = (RelativeLayout) view
				.findViewById(R.id.rl_guanyuaihui);

		rl_indiana = (RelativeLayout) view.findViewById(R.id.rl_indiana);
		rl_record = (RelativeLayout) view.findViewById(R.id.rl_record);
		rl_receiving = (RelativeLayout) view.findViewById(R.id.rl_receiving);
		
		rl_tjm = (RelativeLayout) view.findViewById(R.id.rl_tjm);
		ll_tjm= (LinearLayout) view.findViewById(R.id.ll_tjm);
//		rl_details = (RelativeLayout) view.findViewById(R.id.rl_details);

		// switch1 = (UISwitchButton)view
		// .findViewById(R.id.switch1);
		user_portrait.setOnClickListener(this);
		imageview_xinxi.setOnClickListener(this);
//		textview_shoujihao.setOnClickListener(this);
		rl_quanbudingdan.setOnClickListener(this);
		rl_quanbudingdan2.setOnClickListener(this);
//		rl_ic_wode.setOnClickListener(this);
		rel_layout_wode.setOnClickListener(this);
//		rl_jifenmingxi.setOnClickListener(this);
		rl_tuijianzhuce.setOnClickListener(this);
		rl_wentifankui.setOnClickListener(this);
		rl_genhuanzhanghao.setOnClickListener(this);
		rl_guanyuaihui.setOnClickListener(this);

		rl_indiana.setOnClickListener(this);
		rl_record.setOnClickListener(this);
		rl_receiving.setOnClickListener(this);
		rl_tjm.setOnClickListener(this);
//		rl_details.setOnClickListener(this);

		// switch1.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// IhuiClientApi.getInstance(getActivity()).enableScreenSaver(isChecked);
		// }
		//
		// });
		updateView();
		IhuiClientApi.getInstance(getActivity()).registerListener(listener);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_portrait: {
			ActImagePicker.pickImage(this, FILECHOOSER_RESULTCODE, output_X,
					output_Y, 100);
			break;
		}
		case R.id.imageview_xinxi:
			Intent intent = new Intent(getActivity(), UserXinXi.class);
			startActivity(intent);
			break;

//		case R.id.rl_ic_wode:
//			IhuiClientApi.getInstance(getActivity()).redirect(
//					"http://m.02727.cn/mallshop/myboby.php?");
//			break;

		case R.id.rl_indiana:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://02727.com.cn/mallshop/indiana_record.php?");
			break;

		case R.id.rl_record:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://02727.com.cn/mallshop/lucky_indiana.php?");
			break;

		case R.id.rl_receiving:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.com.cn/mallshop/tb_adress.php?");
			break;
		case R.id.rl_tjm:
			if (mUser.getReferee_id()==0) {
				dialog = new CustomDialog(getActivity());
				dialog_integral_et = (EditText) dialog.getEditText();//方法在CustomDialog中实现
			    dialog.setOnPositiveListener(new OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            //dosomething youself
			        	dialog.dismiss();
			        }
			    });
			    dialog.setOnNegativeListener(new OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	dialog.dismiss();
						if (dialog_integral_et.getText().toString().trim().equals("")) {
							Toast.makeText(getActivity(), "不能为空", 100).show();
						}else {
							
							IhuiClientApi.getInstance(getActivity()).setInviterlink(dialog_integral_et.getText().toString().trim(), new ListenerOnLogin() {
								
								@Override
								public void onSuccess(User user, Wallet wallet) {
									// TODO Auto-generated method stub
									tv_tjm2.setText(mUser.getReferee_invitation_code());
									Toast.makeText(getActivity(), "设置成功", 100).show();
								}
								
								@Override
								public void onFail(String msg) {
									// TODO Auto-generated method stub
									Toast.makeText(getActivity(), "设置失败", 100).show();
								}
							});
						}
			        }
			    });
			    dialog.show();
			}
			
			break;

//		case R.id.rl_details:
//
//			break;

		case R.id.rel_layout_wode:
			startActivity(new Intent(getActivity(), UserXinXi.class));
			break;

		case R.id.rl_guanyuaihui:
			startActivity(new Intent(getActivity(), GuanYu.class));
			break;

		case R.id.rl_tuijianzhuce:
			IhuiClientApi.getInstance(getActivity()).shareBySms(getActivity());
			break;

		case R.id.rl_quanbudingdan:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://m.02727.cn/mallshop/myorder.php?");
			break;
		case R.id.rl_quanbudingdan2:
			getActivity().finish();
			Intent intent2 = new Intent(getActivity(), BinDingActivity.class);
			startActivity(intent2);
			break;

//		case R.id.rl_jifenmingxi:
//			IhuiClientApi.getInstance(getActivity()).redirect(
//					"http://m.02727.cn:8080/webim/web_incomedetails.jsp");
//			break;

		case R.id.rl_wentifankui:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.con.cn/mallshop/feedback.php?");
			break;

		case R.id.rl_genhuanzhanghao:

			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
					.setTitle("提示")
					.setMessage("更换账号")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									clearData();
									getActivity().finish();
									final Intent localIntent = new Intent(getActivity(), LoginActivity.class);
									startActivity(localIntent);
									// System.exit(0);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).create(); // 创建对话框
			alertDialog.show(); // 显示对话框
			break;

		default:
			break;
		}
	}

	/**
	 * 清除APP数据
	 */
	private void clearData() {

		IhuiClientApi.getInstance(getActivity()).deleteContext();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (Activity.RESULT_OK == resultCode) {
				// result = Uri.parse("file://" +
				// intent.getStringExtra("single_path"));

				String path = intent.getStringExtra("single_path");
				uploadPortrait(path);
			} else {
				// showToast("error");
			}

		}

	}

	private void uploadPortrait(String path) {
		// TODO Auto-generated method stub
		showLoadingDlg(this.getString(R.string.uploadingportrait));
		IhuiClientApi.getInstance(getActivity()).changePortraitAsync(path,
				new ListenerOnPortraitChanged() {

					@Override
					public void onSuccess(User user) {
						// TODO Auto-generated method stub
						dismissLoadingDlg();
						showToast(getString(R.string.uploadedportrait));
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-Sgenerated method stub
						dismissLoadingDlg();
						showToast(getString(R.string.failuploadportrait));
					}

				});
	}

	public void onDestroy() {

		IhuiClientApi.getInstance(getActivity()).unregisterListener(listener);
		super.onDestroy();
	}

	void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	private void showLoadingDlg(String msg) {
		if (null == mLoadingDlg) {
			mLoadingDlg = DialogUtil.createLoadingDialog(getActivity(), msg);
		}
		mLoadingDlg.show();
	}

	private void dismissLoadingDlg() {
		mLoadingDlg.dismiss();
	}
	
}

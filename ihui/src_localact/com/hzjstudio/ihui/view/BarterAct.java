package com.hzjstudio.ihui.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Bater;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnPublishBater;
import com.zpstudio.ui.util.DialogUtil;
import com.zpstudio.ui.util.imagepicker.ActImagePicker;

public class BarterAct extends Activity implements OnClickListener {
	private EditText et_biaoti;
	private EditText et_miaoshu;
	private EditText et_jiage;
	private EditText et_dizhi;
	private EditText et_lianxifangshi;
	private Button btn_fabu;
	private TextView fabu_fanhui;

	Dialog mLoadingDlg = null;

	private GridView mImageSelector;
	private GridAdapter mImageSelectorAdp;

	/* 请求识别码 */
	private final static int FILECHOOSER_RESULTCODE = 1;

	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
	private static int output_X = 0;
	private static int output_Y = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_barter);

		init();
		listen();
	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private void init() {

		et_biaoti = (EditText) findViewById(R.id.et_biaoti);
		et_miaoshu = (EditText) findViewById(R.id.et_miaoshu);
		et_jiage = (EditText) findViewById(R.id.et_jiage);
		et_dizhi = (EditText) findViewById(R.id.et_dizhi);
		et_lianxifangshi = (EditText) findViewById(R.id.et_lianxifangshi);
		btn_fabu = (Button) findViewById(R.id.btn_fabu);
		fabu_fanhui = (TextView) findViewById(R.id.fabu_fanhui);

		mImageSelector = (GridView) findViewById(R.id.noScrollgridview);
		mImageSelectorAdp = new GridAdapter(this);
		mImageSelector.setAdapter(mImageSelectorAdp);

	}

	private void listen() {
		fabu_fanhui.setOnClickListener(this);
		btn_fabu.setOnClickListener(this);
		mImageSelector.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == mImageSelectorAdp.getCount() - 1) {
					ActImagePicker.pickImage(BarterAct.this,
							FILECHOOSER_RESULTCODE, output_X, output_Y, 100,
							arg2);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fabu_fanhui:
			finish();
			break;

		case R.id.btn_fabu: {
			String title = et_biaoti.getText().toString();
			String content = et_miaoshu.getText().toString();
			String price = et_jiage.getText().toString();
			String address = et_dizhi.getText().toString();
			String contact = et_lianxifangshi.getText().toString();
			List<String> medias = mImageSelectorAdp.getSelectedList();
			boolean bValidated = true;
			if (TextUtils.isEmpty(title)) {
				showToast("请填写标题");
				bValidated = false;
			} else if (TextUtils.isEmpty(content)) {
				showToast("请填写描述");
				bValidated = false;
			} else if (TextUtils.isEmpty(price)) {
				showToast("请填写价格");
				bValidated = false;
			} else if (TextUtils.isEmpty(address)) {
				showToast("请填写地址");
				bValidated = false;
			} else if (TextUtils.isEmpty(contact)) {
				showToast("请填写联系方式");
				bValidated = false;
			} else if (medias.size() != 3) {
				showToast("请继续添加图片");
				bValidated = false;
			}

			if (bValidated) {
				btn_fabu.setClickable(false);
				IhuiClientApi.getInstance(this).publishBaterAsync(title,
						content, medias, Double.parseDouble(price), address,
						contact, new ListenerOnPublishBater() {

							@Override
							public void onSuccess(Bater bater) {
								// TODO Auto-generated method stub
								showToast("上传成功");
								btn_fabu.setClickable(true);
								finish();
							}

							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								showToast(msg);
								btn_fabu.setClickable(true);
							}

						});
			}

			break;
		}

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (Activity.RESULT_OK == resultCode) {
				// result = Uri.parse("file://" +
				// intent.getStringExtra("single_path"));

				String path = intent.getStringExtra("single_path");
				String uri = "file://" + path;
				if (mImageSelectorAdp.getSelectedSize() < 3) {
					mImageSelectorAdp.add(uri);
				}
			} else {
				// showToast("error");
			}

		}

	}

	private void showLoadingDlg(String msg) {
		if (null == mLoadingDlg) {
			mLoadingDlg = DialogUtil.createLoadingDialog(BarterAct.this, msg);
		}
		mLoadingDlg.show();
	}

	private void dismissLoadingDlg() {
		mLoadingDlg.dismiss();
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public class GridAdapter extends BaseAdapter {
		public static final String TAG = "GridAdapter";
		private LayoutInflater inflater; // 视图容器

		public List<String> list = new ArrayList<String>();

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			list.add("drawable://" + R.drawable.com_hzjstudio__ihui_ic3);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int arg0) {

			return list.get(arg0);
		}

		public long getItemId(int arg0) {

			return 0;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i(TAG, "getView(" + position + " , " + (null != convertView)
					+ ") of " + (list.size() + 1));
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater
						.inflate(
								R.layout.com_hzjstudio__ihui_item_published_grida,
								null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ImageLoader.getInstance().displayImage(list.get(position),
					holder.image);

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		public void add(String uri) {
			list.add((list.size() - 1), uri);
			notifyDataSetChanged();

		}

		public int getSelectedSize() {
			return list.size() - 1;
		}

		public List<String> getSelectedList() {
			return list.subList(0, list.size() - 1);
		}
	}

}

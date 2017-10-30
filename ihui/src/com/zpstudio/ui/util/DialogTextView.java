package com.zpstudio.ui.util;

import com.zpstudio.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogTextView extends Dialog {
	private Context context;
	String mContent;
	TextView textView;
	ScrollView scrollView;
	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel();
	}

	public DialogTextView(Context context, String content) {
		super(context, R.style.Dialog_Fullscreen);
		this.context = context;
		mContent = content;
		init();
	}

	public void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_text_view, null);
		setContentView(view);

		textView = (TextView) findViewById(R.id.content);
		textView.setText(mContent);
		//textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		textView.setLongClickable(true);
		textView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				share(mContent);
				return true;
			}

		});
		
		scrollView = (ScrollView) findViewById(R.id.scrollview);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.smoothScrollTo(0, textView.getBottom());
				}
		});
	}

	private void share(String msg) {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, "debug");
		share.putExtra(Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(share, "·¢ËÍ"));
	}

	public void scroll2Bottom(final ScrollView scroll, final View inner) {

		Handler handler = new Handler();

		handler.post(new Runnable() {

			@Override
			public void run() {

				// TODO Auto-generated method stub

				if (scroll == null || inner == null) {

					return;

				}

				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();

				if (offset < 0) {

					offset = 0;

				}

				scroll.scrollTo(0, offset);

			}

		});

	}

}

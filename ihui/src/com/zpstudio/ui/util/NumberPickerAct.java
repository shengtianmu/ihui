package com.zpstudio.ui.util;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.zpstudio.R;

public class NumberPickerAct extends Activity {
	public static final String RESULT_NUMBER = "number";
	
	WheelView wvNumber = null;
	
	Handler mHandler = new Handler();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.numberpicker);
        
        wvNumber = (WheelView) findViewById(R.id.picker);
        
        
        wvNumber.setVisibleItems(10);
        wvNumber.setCyclic(true);
        wvNumber.setViewAdapter(new NumericWheelAdapter(this,0,100));
        wvNumber.setCurrentItem(20);
        
        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					
					public void run() {
						
						Bundle bundle = new Bundle();             
						bundle.putInt("number",  wvNumber.getCurrentItem());   
						
						NumberPickerAct.this.setResult(RESULT_OK,NumberPickerAct.this.getIntent().putExtras(bundle));
						NumberPickerAct.this.finish();
					}
				}, 400);
				
			}
		});
        
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //���ÿ��
        getWindow().setAttributes(lp); 
    }
}


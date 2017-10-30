package com.hzjstudio.ihui.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;

import com.zpstudio.R;

public class CustomDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
 
    public CustomDialog(Context context) {
        super(context,R.style.dialog);
        setCustomDialog();
    }
 
    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_integral, null);
        editText = (EditText) mView.findViewById(R.id.paopao_integral_et);
        positiveButton = (Button) mView.findViewById(R.id.button_noll);
        negativeButton = (Button) mView.findViewById(R.id.button_ys);
        super.setContentView(mView);
    }
    
    public View getEditText(){
        return editText;
    }
     
     @Override
    public void setContentView(int layoutResID) {
    }
     
     
    public void setContentView(View view, LayoutParams params) {
    	
    }
 
    @Override
    public void setContentView(View view) {
    }
 
    /**
     * 确定键监听器
     * @param listener
     */ 
    public void setOnPositiveListener(View.OnClickListener listener){ 
        positiveButton.setOnClickListener(listener); 
    } 
    /**
     * 取消键监听器
     * @param listener
     */ 
    public void setOnNegativeListener(View.OnClickListener listener){ 
        negativeButton.setOnClickListener(listener); 
    }
}


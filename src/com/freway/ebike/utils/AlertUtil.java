package com.freway.ebike.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.freway.ebike.R;

public class AlertUtil {
	public static AlertUtil alertUtil;
	private static Context context;
	private AlertDialog dialog;
	private AlertUtil(){
	}
	public static AlertUtil getInstance(Context ctx){
		context=ctx;
		if(alertUtil==null){
			alertUtil=new AlertUtil();
		}
		return alertUtil;
	}
	/**弹出一个两个选择按钮的对话框*/
	public void alertChoice(String message,String leftText,String rightText,OnClickListener leftClick,OnClickListener rightClick){
		AlertDialog.Builder builder=new Builder(context);
		View view=LayoutInflater.from(context).inflate(R.layout.layout_dilog_btn_left_right, null);
		TextView title=(TextView) view.findViewById(R.id.dialog_title);
		TextView left=(TextView) view.findViewById(R.id.dialog_left);
		TextView right=(TextView) view.findViewById(R.id.dialog_right);
		title.setText(message);
		left.setText(leftText);
		right.setText(rightText);
		left.setOnClickListener(leftClick);
		right.setOnClickListener(rightClick);
		dialog=builder.setView(view).create();
		dialog.show();
//		builder.setMessage(message).setNegativeButton(context.getString(R.string.no), noClick).setPositiveButton(context.getString(R.string.yes), yesClick).create().show();
	}
	
	/**弹出一个两个选择按钮的对话框*/
	public void alertConfirm(String message,String confirmText,OnClickListener confirmClick){
		AlertDialog.Builder builder=new Builder(context);
		View view=LayoutInflater.from(context).inflate(R.layout.layout_dilog_btn_left_right, null);
		TextView title=(TextView) view.findViewById(R.id.dialog_title);
		TextView left=(TextView) view.findViewById(R.id.dialog_left);
		TextView right=(TextView) view.findViewById(R.id.dialog_right);
		title.setText(message);
		left.setText(leftText);
		right.setText(rightText);
		left.setOnClickListener(leftClick);
		right.setOnClickListener(rightClick);
		dialog=builder.setView(view).create();
		dialog.show();
//		builder.setMessage(message).setNegativeButton(context.getString(R.string.no), noClick).setPositiveButton(context.getString(R.string.yes), yesClick).create().show();
	}
	
	 public void dismiss(){
		 if(dialog!=null){
			 dialog.dismiss();
		 }
	 }
}

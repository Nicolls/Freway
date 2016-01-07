package com.freway.ebike.utils;

import android.app.Activity;
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
	private AlertDialog dialog=null;
	public interface AlertClick{
		void onClick(AlertDialog dialog,View v);
	}
	private AlertUtil(){
		
	}
	/**
	 * 获取统一对话框实例
	 * @param ctx 要弹出对话框的activity
	 * */
	public static AlertUtil getInstance(){
		if(alertUtil==null){
			alertUtil=new AlertUtil();
		}
		return alertUtil;
	}
	/**弹出一个三个选择按钮的对话框*/
	public void alertThree(Activity context,String message,String leftText,String middleText,String rightText,final AlertClick leftClick,final AlertClick middleClick,final AlertClick rightClick){
		if(dialog!=null){
			dialog.dismiss();
		}
		AlertDialog.Builder builder=new Builder(context);
		View view=LayoutInflater.from(context).inflate(R.layout.layout_dilog_btn_left_middle_right, null);
		TextView title=(TextView) view.findViewById(R.id.dialog_title);
		TextView left=(TextView) view.findViewById(R.id.dialog_left);
		TextView middle=(TextView) view.findViewById(R.id.dialog_middle);
		TextView right=(TextView) view.findViewById(R.id.dialog_right);
		title.setText(message);
		left.setText(leftText);
		middle.setText(middleText);
		right.setText(rightText);
		left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				leftClick.onClick(dialog,v);
			}
		});
		middle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				middleClick.onClick(dialog,v);
			}
		});
		right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rightClick.onClick(dialog,v);
			}
		});
		dialog=builder.setView(view).create();
		dialog.show();
	}
	/**弹出一个两个选择按钮的对话框*/
	public void alertChoice(Activity context,String message,String leftText,String rightText,final AlertClick leftClick,final AlertClick rightClick,boolean cancelable){
		if(dialog!=null){
			dialog.dismiss();
		}
		AlertDialog.Builder builder=new Builder(context);
		View view=LayoutInflater.from(context).inflate(R.layout.layout_dilog_btn_left_right, null);
		TextView title=(TextView) view.findViewById(R.id.dialog_title);
		TextView left=(TextView) view.findViewById(R.id.dialog_left);
		TextView right=(TextView) view.findViewById(R.id.dialog_right);
		title.setText(message);
		left.setText(leftText);
		right.setText(rightText);
		left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				leftClick.onClick(dialog,v);
			}
		});
		right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rightClick.onClick(dialog,v);
			}
		});
		dialog=builder.setView(view).create();
		dialog.show();
	}
	
	/**弹出一个确认按钮的对话框*/
	public void alertConfirm(Activity context,String message,String confirmText,final AlertClick confirmClick){
		if(dialog!=null){
			dialog.dismiss();
		}
		AlertDialog.Builder builder=new Builder(context);
		View view=LayoutInflater.from(context).inflate(R.layout.layout_dilog_confirm, null);
		TextView title=(TextView) view.findViewById(R.id.dialog_title);
		TextView textView=(TextView) view.findViewById(R.id.dialog_text);
		title.setText(message);
		textView.setText(confirmText);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmClick.onClick(dialog,v);
			}
		});
		dialog=builder.setView(view).create();
		dialog.show();
	}
}

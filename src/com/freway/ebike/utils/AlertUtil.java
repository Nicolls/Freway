package com.freway.ebike.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class AlertUtil {
	public static  void alertNormal(Context context,String message,OnClickListener yesClick,OnClickListener noClick){
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle("tip").setMessage(message).setNegativeButton("no", noClick).setPositiveButton("yes", yesClick).create().show();
	}
}

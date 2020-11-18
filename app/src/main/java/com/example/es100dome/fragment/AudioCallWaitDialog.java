package com.example.es100dome.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.es100.MyState;
import com.es100.db.ContactDbManager;
import com.es100.jni.MstApp;
import com.example.es100dome.R;
import com.example.es100dome.widget.CircleImageView;
import com.ifreecomm.debug.MLog;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class AudioCallWaitDialog extends Dialog {
	public final static String TAG = "AudioCallWaitDialog";
	Context mContext;
	View view;
	TextView callTip;
	Button btStopCall;
	TextView nameTextView;
	String mIp;
	ContactDbManager mContactDbManager;
	ImageView back;
	CircleImageView ivIcon;
	String iconPath;
	private String temp164;
	private static MyState sta = MyState.getInstance();
	private HashMap<String, String> e164map;
	private Disposable timeOutDisposable;
	private String text;
	private String name;
	public AudioCallWaitDialog(Context context,String callText,String showName) {
		super(context, com.es100.R.style.dialogWindowAnim);
		mContext = context;
		text = callText;
		name = showName;

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindow();
		view = LayoutInflater.from(mContext).inflate(R.layout.dailog_audio_call_wait, null);
		setContentView(view);
		mContactDbManager = new ContactDbManager(mContext);
		callTip = (TextView)findViewById(R.id.call_status_textview);
		btStopCall = (Button)view.findViewById(R.id.bt_stop);
		nameTextView = (TextView)view.findViewById(R.id.name_textview);
		back = (ImageView)view.findViewById(R.id.back);
		ivIcon = (CircleImageView)view.findViewById(R.id.people_imageview);
		MLog.e(TAG,"name"+name);
		nameTextView.setText(name);
//		callTip.setText(text);
		btStopCall.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MLog.e("LOGCAT", "aaaaaaa:stopcall:"+sta.h323callstate+" aaaa:"+sta.callId+"sta.callIp:"+sta.callIp);
//				if(sta.h323callstate==4){
//					return;
//				}
				MstApp.getInstance().uISendMsg.Drop(sta.callId,false);
				sta.StartCall = false;
				sta.StartDrop = true;
				dismiss();
			}
		});
		back.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(sta.h323callstate==4) {
					return;
				}
				MstApp.getInstance().uISendMsg.Drop(sta.callId,true);
				MLog.e("aaa", "aaaaaaa:back:"+sta.h323callstate+" aaaa:"+sta.callId);
				sta.StartCall = false;
				sta.StartDrop = true;	
//				MainActivity.mHandler.post(MainActivity.mCheckDropTimeOut);
				//dismiss();
				dismiss();
			}
		});
		startTimeOut();
	}

	private void setWindow() {
		Window dialogWindow = getWindow();
		if(dialogWindow != null) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
			dialogWindow.setAttributes(lp);
		}

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(sta.h323callstate==4)
//				return false;
//			if(sta.audioCallWaitDialog!=null)
//				sta.audioCallWaitDialog.setMessage("正在挂断...");
//			MstApp.getInstance().uISendMsg.Drop(sta.callId,true);
//			sta.StartCall = false;
//			sta.StartDrop = true;
////			MainActivity.mHandler.post(MainActivity.mCheckDropTimeOut);
//			//dismiss();
//			sta.audioCallWaitDialog.dismiss();
//			sta.audioCallWaitDialog = null;
//		}
//		return super.onKeyUp(keyCode, event);
//	}

	private void startTimeOut() {
		stopTimeOut();
		timeOutDisposable = Single.timer(15, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(aLong -> {
//					if(sta.h323callstate==4){
//						return;
//					}
					MstApp.getInstance().uISendMsg.Drop(sta.callId,true);
					sta.StartCall = false;
					sta.StartDrop = true;
					dismiss();
				}, Throwable:: printStackTrace);
	}

	private void stopTimeOut() {
		if(timeOutDisposable != null) {
			if(!timeOutDisposable.isDisposed()) {
				timeOutDisposable.dispose();
			}
			timeOutDisposable = null;
		}
	}

	@Override
	public void dismiss() {
		stopTimeOut();
		super.dismiss();
	}
}

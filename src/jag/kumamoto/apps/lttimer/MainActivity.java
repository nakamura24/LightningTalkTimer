/* Copyright (C) 2013 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jag.kumamoto.apps.lttimer;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity {
	private static final String Tag = "MainActivity";
	private static final String mTimerFormat = "%d:%02d";
	private static final int mPeriod = 5;	// minutes
	private CountDownTimer mCountDownTimer = null;
	private Ringtone mRingtone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_main);
			TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
			String timer_text = String.format(mTimerFormat, mPeriod, 0);
			textView_timer.setText(timer_text);

			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
			
	        //キャッチされない例外により、スレッドが突然終了したときや、  
	        //このスレッドに対してほかにハンドラが定義されていないときに  
	        //呼び出されるデフォルトのハンドラを設定します。  
	        Thread.setDefaultUncaughtExceptionHandler(new ErrorReportClass(this));  
	    } catch (Exception e) {
			Log.e(Tag, "onCreate");
			ErrorReportClass.LogException(this, e);
		}
	}

	public void onClickStartButton(View view) {
		Log.i(Tag, "onClickOkButton");
		try {
			// カウントダウンする
			mCountDownTimer = new CountDownTimer(mPeriod * 60000, 1000) {
				TextView textView_timer = (TextView) findViewById(R.id.textView_timer);

				// カウントダウン処理
				public void onTick(long millisUntilFinished) {
					try {
						String timer_text = String.format(mTimerFormat,
								millisUntilFinished / 60000,
								(millisUntilFinished / 1000) % 60);
						textView_timer.setText(timer_text);
						if((int)(millisUntilFinished/1000) == 30){
							mRingtone.play();
						}
					} catch (Exception e) {
						Log.e(Tag, "onTick");
						ErrorReportClass.LogException(MainActivity.this, e);
					}
				}

				// カウントが0になった時の処理
				public void onFinish() {
					try {
						String timer_text = String.format(mTimerFormat, 0, 0);
						TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
						textView_timer.setText(timer_text);

						mRingtone.play();
					} catch (Exception e) {
						Log.e(Tag, "onFinish");
						ErrorReportClass.LogException(MainActivity.this, e);
					}
				}
			};
			mCountDownTimer.start();
		} catch (Exception e) {
			Log.e(Tag, "onClickStartButton");
			ErrorReportClass.LogException(this, e);
		}
	}

	public void onClickStopButton(View view) {
		Log.i(Tag, "onClickOkButton");
		try {
			if (mCountDownTimer != null)
				mCountDownTimer.cancel();
			mCountDownTimer = null;
			TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
			String timer_text = String.format(mTimerFormat, mPeriod, 0);
			textView_timer.setText(timer_text);
		} catch (Exception e) {
			Log.e(Tag, "onClickStopButton");
			ErrorReportClass.LogException(this, e);
		}
	}
	  
    @Override  
    protected void onStart() {  
        super.onStart();  
        ErrorReportClass.SendBugReportDialog(this.getApplicationContext());  
    }
}

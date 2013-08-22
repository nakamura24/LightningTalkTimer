/* Copyright (C) 2013 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jag.kumamoto.apps.lttimer;

import java.util.Locale;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity {
	private static final String Tag = "MainActivity";
	private static final String mTimerFormat = "%d:%02d";
	private static final int mPeriod = 300; // sec
	private static final int mPeriodPreAlarm = 30; // sec
	private boolean mTimerEnable = false;
	private CountDownTimer mCountDownTimer = null;
	private Ringtone mRingtone = null;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		public void run() {
			Log.i(Tag, "run");
			try {
				if (!mRingtone.isPlaying()) {
					// アラームを鳴らす
					mRingtone.play();
					mTimerEnable = false;
				}
				// 2回鳴らす
				if (mTimerEnable)
					mHandler.postDelayed(mRunnable, 100);
			} catch (Exception e) {
				Log.e(Tag, e.getMessage());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Tag, "onCreate");
		super.onCreate(savedInstanceState);
		try {
			// 時間を表示
			setContentView(R.layout.activity_main);
			TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
			String timer_text = String.format(Locale.US, mTimerFormat,
					(int) (mPeriod / 60), mPeriod % 60);
			textView_timer.setText(timer_text);

			// キャッチされない例外により、スレッドが突然終了したときや、
			// このスレッドに対してほかにハンドラが定義されていないときに
			// 呼び出されるデフォルトのハンドラを設定します。
			Thread.setDefaultUncaughtExceptionHandler(new ErrorReportClass(this));
		} catch (Exception e) {
			ErrorReportClass.LogException(this, e);
		}
	}

	public void onClickStartButton(View view) {
		Log.i(Tag, "onClickOkButton");
		try {
			// Keep screen on
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// 通知音を取得
			Uri uri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
					uri);

			// タイマーの準備
			mCountDownTimer = new CountDownTimer(mPeriod * 1000, 100) {
				TextView textView_timer = (TextView) findViewById(R.id.textView_timer);

				// カウントダウン処理
				public void onTick(long millisUntilFinished) {
					try {
						// 時間を再表示
						String timer_text = String.format(Locale.US,
								mTimerFormat,
								(int) (millisUntilFinished / 60000),
								(int) (millisUntilFinished / 1000) % 60);
						textView_timer.setText(timer_text);

						if ((int) (millisUntilFinished / 1000) == mPeriodPreAlarm) {
							// 予鈴を鳴らす
							if (!mRingtone.isPlaying())
								mRingtone.play();
						}
					} catch (Exception e) {
						ErrorReportClass.LogException(MainActivity.this, e);
					}
				}

				// カウントが0になった時の処理
				public void onFinish() {
					Log.i(Tag, "onFinish");
					try {
						String timer_text = String.format(Locale.US,
								mTimerFormat, 0, 0);
						TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
						textView_timer.setText(timer_text);

						// アラームを鳴らす
						mRingtone.play();
						// 2回鳴らす
						mHandler.postDelayed(mRunnable, 100);
						// Keep screen off
						getWindow().clearFlags(
								WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					} catch (Exception e) {
						ErrorReportClass.LogException(MainActivity.this, e);
					}
				}
			};

			// タイマーの開始
			mCountDownTimer.start();
			mTimerEnable = true;
		} catch (Exception e) {
			ErrorReportClass.LogException(this, e);
		}
	}

	public void onClickStopButton(View view) {
		Log.i(Tag, "onClickOkButton");
		try {
			mTimerEnable = false;
			// Keep screen off
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			// タイマーの停止
			if (mCountDownTimer != null)
				mCountDownTimer.cancel();
			mCountDownTimer = null;

			// 時間を再表示
			TextView textView_timer = (TextView) findViewById(R.id.textView_timer);
			String timer_text = String.format(Locale.US, mTimerFormat,
					(int) (mPeriod / 60), mPeriod % 60);
			textView_timer.setText(timer_text);
		} catch (Exception e) {
			ErrorReportClass.LogException(this, e);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 前回異常終了していたとき、エラーレポートを送信する
		ErrorReportClass.SendBugReportDialog(this.getApplicationContext());
	}
}

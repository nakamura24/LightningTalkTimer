/* Copyright (C) 2013 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jag.kumamoto.apps.lttimer;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class ErrorReportClass implements UncaughtExceptionHandler {
	private static final String TAG = "ErrorReportClass";
	private static Context mContext = null;
	private static final UncaughtExceptionHandler mDefaultHandler = Thread
			.getDefaultUncaughtExceptionHandler();
	private static final String ErrorReportKey = "ErrorReport";
	private static final String MailAddress = "zmf42190@gmail.com";

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public ErrorReportClass(Context context) {
		mContext = context.getApplicationContext();
	}

	/**
	 * キャッチされない例外によって指定されたスレッドが終了したときに呼び出されます
	 */
	@Override
	public void uncaughtException(Thread thread, final Throwable ex) {
		Log.i(TAG, "uncaughtException");
		try {
			String report = String.format("DEVICE: %s\n", Build.DEVICE);
			report += String.format("MODEL: %s\n", Build.MODEL);
			report += String.format("VERSION.SDK: %d\n", Build.VERSION.SDK_INT);
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(),
							PackageManager.GET_META_DATA);
			report += String.format("VERSION: %s\n", packageInfo.versionName);
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				report += String.format("%s:%d:%s\n",
						ex.getStackTrace()[i].getFileName(),
						ex.getStackTrace()[i].getLineNumber(),
						ex.getStackTrace()[i].getMethodName());
			}
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			Editor editer = sharedPreferences.edit();
			editer.putString(ErrorReportKey, report);
			editer.commit();
			Log.e(TAG, report);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		mDefaultHandler.uncaughtException(thread, ex);
	}

	public static void LogException(Context context, Exception ex) {
		Log.i(TAG, "LogException");
		try {
			context = context.getApplicationContext();
			String report = String.format("DEVICE: %s\n", Build.DEVICE);
			report += String.format("MODEL: %s\n", Build.MODEL);
			report += String.format("VERSION.SDK: %d\n", Build.VERSION.SDK_INT);
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(),
							PackageManager.GET_META_DATA);
			report += String.format("VERSION: %s\n", packageInfo.versionName);
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				report += String.format("%s:%d:%s\n",
						ex.getStackTrace()[i].getFileName(),
						ex.getStackTrace()[i].getLineNumber(),
						ex.getStackTrace()[i].getMethodName());
			}
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			Editor editer = sharedPreferences.edit();
			editer.putString(ErrorReportKey, report);
			editer.commit();
			Log.e(TAG, report);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public static void SendBugReportDialog(final Context context) {
		Log.i(TAG, "SendBugReportDialog");
		try {
			// バグレポートの内容を読み込みます。
			final SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			final String report = sharedPreferences.getString(ErrorReportKey, null);
			if (report == null) {
				return;
			}
			Log.d(TAG, report);

			// AlertDialogを表示します。
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(R.string.common_text_error);
			alert.setMessage(R.string.common_text_bug_report);
			alert.setPositiveButton(R.string.common_text_send,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SendBugReport(context, report);
							Editor editer = sharedPreferences.edit();
							editer.putString(ErrorReportKey, null);
							editer.commit();
						}
					});
			alert.setNegativeButton(R.string.common_text_cancel, null);
			alert.show();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * バグレポートの内容をメールで送信します。
	 * 
	 * @param bugreport
	 */
	private static void SendBugReport(Context context, String report) {
		Log.i(TAG, "SendBugReport");
		try {
			// メールで送信します。
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + MailAddress));
			intent.putExtra(Intent.EXTRA_SUBJECT, "【BugReport】"
					+ R.string.app_name);
			intent.putExtra(Intent.EXTRA_TEXT, report);
			context.startActivity(intent);
		} catch (Exception e) {
		}
	}
}

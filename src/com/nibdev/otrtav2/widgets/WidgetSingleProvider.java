package com.nibdev.otrtav2.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.activities.ActivityAllOff;
import com.nibdev.otrtav2.activities.ActivityWidgetSingleSettings;
import com.nibdev.otrtav2.service.OTRTAService;

public class WidgetSingleProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, WidgetSingleProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			updateAppWidget(context, appWidgetManager, widgetId);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		SharedPreferences widgetPrefs = context.getSharedPreferences(ActivityWidgetSingleSettings.class.getSimpleName(), Context.MODE_PRIVATE);
		for (int appWidgetId : appWidgetIds){
			if (widgetPrefs.contains("WIDGET_" + appWidgetId)){
				widgetPrefs.edit().remove("WIDGET_" + appWidgetId).commit();
			}
		}
	}

	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
		SharedPreferences widgetPrefs = context.getSharedPreferences(ActivityWidgetSingleSettings.class.getSimpleName(), Context.MODE_PRIVATE);
		if (widgetPrefs.contains("WIDGET_" + appWidgetId)){
			
			Gson gson = new Gson();
			SingleWidgetConfiguration config = gson.fromJson(widgetPrefs.getString("WIDGET_" + appWidgetId, ""), SingleWidgetConfiguration.class);

			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_single);
			Intent clickIntent = null;
			PendingIntent pIntent = null;

			int type = config.getType();
			if (type == SingleWidgetConfiguration.TYPE_CODE){
				updateViews.setImageViewResource(R.id.ib_widget, R.drawable.ic_action_code_dark);
				clickIntent = new Intent(context, OTRTAService.class);
				clickIntent.putExtra("ACTION", OTRTAService.ACTION_SENDCODE);
				clickIntent.putExtra("ID", config.getAllocationID());
				pIntent = PendingIntent.getService(context, config.getWidgetID(), clickIntent, Intent.FLAG_ACTIVITY_NEW_TASK);


			}else if (type == SingleWidgetConfiguration.TYPE_SCRIPT){
				updateViews.setImageViewResource(R.id.ib_widget, R.drawable.ic_action_scripts_dark);
				clickIntent = new Intent(context, OTRTAService.class);
				clickIntent.putExtra("ACTION", OTRTAService.ACTION_SENDSCRIPT);
				clickIntent.putExtra("ID", config.getAllocationID());
				pIntent = PendingIntent.getService(context, config.getWidgetID(), clickIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

			}else if (type == SingleWidgetConfiguration.TYPE_LAYOUTSTART){
				updateViews.setImageViewResource(R.id.ib_widget, R.drawable.ic_action_layout_dark);

			}else if (type == SingleWidgetConfiguration.TYPE_ALLOFF){
				updateViews.setImageViewResource(R.id.ib_widget, R.drawable.ic_action_off_dark);
				clickIntent = new Intent(context, ActivityAllOff.class);
				pIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
			}

			updateViews.setTextViewText(R.id.tv_top, config.getTopText());
			updateViews.setTextViewText(R.id.tv_bottom, config.getBottomText());

			updateViews.setViewVisibility(R.id.tv_top, (config.getTopText().isEmpty() ? View.GONE : View.VISIBLE));
			updateViews.setViewVisibility(R.id.tv_bottom, (config.getBottomText().isEmpty() ? View.GONE : View.VISIBLE));

			updateViews.setOnClickPendingIntent(R.id.ib_widget, pIntent);

			appWidgetManager.updateAppWidget(appWidgetId, updateViews);
		}else{

		}
	}


} 



package pl.mateusz.datasavertoggle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DataSaverWidget extends AppWidgetProvider {

    private static final String DATA_SAVER_ACTION = "android.settings.DATA_SAVER_SETTINGS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_data_saver);

            Intent intent = new Intent(DATA_SAVER_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    1001,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

package pw.jfrodriguez.farmacopapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Juanfran on 09/05/2016.
 */
public class mybroadcast extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent)
    {
        if(!backgroundService.Started) {
            Intent serviceIntent = new Intent(context, backgroundService.class);
            startWakefulService(context, serviceIntent);
        }
    }

    public static void StartServiceFromActivity(Context context){
        final Context thecontext = context;
        if(!backgroundService.Started) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Intent serviceIntent = new Intent(thecontext, backgroundService.class);
                    startWakefulService(thecontext, serviceIntent);
                }
            }.start();
        }
    }
}

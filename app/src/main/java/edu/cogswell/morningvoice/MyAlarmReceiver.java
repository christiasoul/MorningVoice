package edu.cogswell.morningvoice;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Christian on 4/10/2017.
 */

public class MyAlarmReceiver extends BroadcastReceiver  {
    @Override
    public void onReceive(Context context, Intent intent){
        MainActivity.run();
    }
}

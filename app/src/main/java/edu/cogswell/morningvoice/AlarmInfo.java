package edu.cogswell.morningvoice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Christian on 4/21/2017.
 */

public class AlarmInfo {

    boolean [] days = new boolean[7];
    private ArrayList<Calendar> alarmList;
    private TimePicker myPick;
    private AlarmManager alarmManage;
    private Intent myIntent;
    private Context myContext;
    private ArrayList<Intent> alarmIntents;
    private ArrayList<PendingIntent> alarmPending;


    public AlarmInfo(TimePicker timePick , AlarmManager setManage, Context setContext){
        alarmList = new ArrayList<Calendar>();
        myPick = timePick;
        alarmManage = setManage;
        myContext = setContext;
        myIntent = new Intent(myContext, MyAlarmReceiver.class);
        alarmIntents = new ArrayList<Intent>();
        alarmPending = new ArrayList<PendingIntent>();
        for (int i = 0; i < 7; i++){days[i] = false;}
    }

    public boolean setAlarm(){
        try {

            for (int i = 0; i < alarmIntents.size();i++) {
                alarmManage.cancel(alarmPending.get(i));
            }

            alarmList.clear();
            alarmIntents.clear();
            int alarmCur = 0;
            for (int i = 0; i < 7; i++) {
                if (days[i]) {
                    alarmList.add(Calendar.getInstance());
                    alarmList.get(alarmCur).set(Calendar.HOUR_OF_DAY, myPick.getCurrentHour());
                    alarmList.get(alarmCur).set(Calendar.MINUTE, myPick.getCurrentMinute());
                    alarmList.get(alarmCur).set(Calendar.DAY_OF_WEEK, i + 1);
                    alarmIntents.add(new Intent(myContext, MyAlarmReceiver.class));

                    alarmPending.add(PendingIntent.getBroadcast(myContext, 0, alarmIntents.get(alarmCur), 0));
                    alarmManage.setRepeating(AlarmManager.RTC_WAKEUP, alarmList.get(alarmCur).getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7, alarmPending.get(alarmCur) );
                    alarmCur++;
                }
            }

            return true;

        }catch(Exception ex){
            return false;
        }
    }

    public void flipDay(int dayNum){
        days[dayNum] ^= true;
    }

}

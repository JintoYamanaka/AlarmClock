package com.example.alarmclock.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alarmclock.listcomponent.ListItem;

import java.util.ArrayList;
import java.util.Calendar;

public class Util {

    // アラームのデータを取得
    public static ListItem getAlarmsByID(int alarmID, SQLiteOpenHelper helper){

        ArrayList<ListItem> data = new ArrayList<>();

        ListItem item = null;
        try(SQLiteDatabase db = helper.getReadableDatabase()) {

            String[] cols ={"alarmid","name","alarttime"};
            String[] params = {String.valueOf(alarmID)};

            Cursor cs = db.query("alarms",cols,"alarmid = ?",params,
                    null,null,"alarmid",null);
            cs.moveToFirst();
            item = new ListItem();
            item.setAlarmID(cs.getInt(0));
            item.setMusicName(cs.getString(1));
            item.setTime(cs.getString(2));
        }
        return item;
    }

    // アラームをセット
    public static void setAlarm(Context context, ListItem item){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getHour()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(item.getMinute()));
        calendar.set(Calendar.SECOND, 0);

        // 現在時刻を取得
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());

        // 比較
        int diff = calendar.compareTo(nowCalendar);

        // 日付を設定
        if(diff <= 0){
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }

    }
}

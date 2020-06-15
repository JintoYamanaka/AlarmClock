package com.example.alarmclock.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.alarmclock.R;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.util.DatabaseHelper;
import com.example.alarmclock.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private TimePicker timePicker = null;
    private DatabaseHelper helper = null;
    private Spinner musicNameSpinner = null;
    private int reqCode = -1;
    Intent retnIntent = null;
    int currentApiVersion = Build.VERSION.SDK_INT;
    private static int MENU_DELETE_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // タイムピッカーを取得
        timePicker = findViewById(R.id.time_picker);

        // 曲名を取得
        musicNameSpinner = findViewById(R.id.selectMusicText);

        // Map<Keyの型, 値の型>となる。
        Map<String, String> musicData = new HashMap<String, String>() {
            {
                put("シンプル", "simple");
                put("さざ波", "wave");
                put("エレキギター", "guitar");
            }
        };
        List<String> spinnerItems = new ArrayList<>(musicData.keySet()); // musicDataのキーを取得

        // ArrayAdapter
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // spinner に adapter をセット
        musicNameSpinner.setAdapter(adapter); // 初期値は0番目の要素

        // ヘルパーの準備
        helper = DatabaseHelper.getInstance(InputActivity.this);

        // キャンセルボタンの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInput);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });

        // 保存、削除ボタンの設定
        toolbar.inflateMenu(R.menu.edit_menu);

        // 新規 or 編集を取得
        Intent intent = getIntent();
        reqCode = intent.getIntExtra(getString(R.string.request_code),-1);
        int alarmID = -1;

        if(reqCode == ConfirmationActivity.EDIT_REQ_CODE){
            // 編集モード
            // 削除ボタンを追加する
            Menu menu = toolbar.getMenu();
            menu.add(0,MENU_DELETE_ID,2, R.string.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            // 編集前のデータを取得
            alarmID = intent.getIntExtra(getString(R.string.alarm_id),-1);
            ListItem item = Util.getAlarmsByID(alarmID, helper);
            musicNameSpinner.setSelection(spinnerItems.indexOf(item.getMusicName()));

            if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                timePicker.setHour(Integer.parseInt(item.getHour()));
                timePicker.setMinute(Integer.parseInt(item.getMinute()));
            } else {
                timePicker.setCurrentHour(Integer.parseInt(item.getHour()));
                timePicker.setCurrentMinute(Integer.parseInt(item.getMinute()));
            }

        }else {
            // 新規
            // 何もしない
        }

        final int alarmIDForMenu = alarmID;

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_save) {

                    // アラーム設定処理
                    // 設定時刻を取得
                    int hour;
                    int minute;
                    if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        hour = timePicker.getHour();
                        minute = timePicker.getMinute();

                    } else {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    // データ登録 or 更新
                    // TODO DB登録後にエラーが発生した場合の考慮が必要
                    int requestCode = -1;

                    // 曲名の設定
                    String musicName = (String)musicNameSpinner.getSelectedItem();

                    // 時刻登録の準備
                    String alarmTime = String.format("%02d", hour) + ":"
                            + String.format("%02d", minute);

                    if(reqCode == ConfirmationActivity.EDIT_REQ_CODE){
                        // 編集
                        // データ更新処理
                        requestCode = alarmIDForMenu;
                        try(SQLiteDatabase db = helper.getWritableDatabase()){
                            ContentValues cv = new ContentValues();
                            cv.put("name",musicName);
                            cv.put("alarttime", alarmTime);
                            String[] params = {String.valueOf(requestCode)};
                            db.update("alarms",cv,"alarmid = ?",params);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else {
                        // 新規
                        // データ登録
                        try(SQLiteDatabase db = helper.getWritableDatabase()){
                            ContentValues cv = new ContentValues();
                            cv.put("name",musicName);
                            cv.put("alarttime", alarmTime);
                            requestCode = (int)db.insert("alarms",null,cv);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    // アラームの設定
                    ListItem listItem = new ListItem();
                    listItem.setAlarmID(requestCode);
                    listItem.setMusicName(musicName);
                    listItem.setTime(alarmTime);
                    Util.setAlarm(InputActivity.this, listItem);

                    Toast.makeText(InputActivity.this, R.string.alarm_save_msg,Toast.LENGTH_SHORT).show();

                }else if(id == MENU_DELETE_ID){

                    // 編集
                    // アラーム削除処理
                    Intent receiveIntent = getIntent();
                    int alarmID = receiveIntent.getIntExtra(getString(R.string.alarm_id),-1);

                    // データ削除処理
                    try(SQLiteDatabase db = helper.getWritableDatabase()){
                        String[] params = {String.valueOf(alarmID)};
                        db.delete("alarms","alarmid = ?",params);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Toast.makeText(InputActivity.this, R.string.alarm_delete_msg,Toast.LENGTH_SHORT).show();
                }

                retnIntent = new Intent();
                setResult(RESULT_OK, retnIntent);
                finish();
                return true;
            }
        });
    }
}

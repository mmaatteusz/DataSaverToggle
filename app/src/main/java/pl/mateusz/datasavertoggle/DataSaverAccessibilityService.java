package pl.mateusz.datasavertoggle;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Locale;

public class DataSaverAccessibilityService extends AccessibilityService {

    private static final String DATA_SAVER_ACTION = "android.settings.DATA_SAVER_SETTINGS";
    private static volatile DataSaverAccessibilityService instance;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile Boolean requestedState;
    private volatile long requestStartedAt;
    private volatile boolean processing;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {}

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (requestedState == null || processing || event == null) return;
        CharSequence pkg = event.getPackageName();
        if (pkg == null || !"com.android.settings".contentEquals(pkg)) return;

        long age = System.currentTimeMillis() - requestStartedAt;
        if (age > 6000) {
            requestedState = null;
            return;
        }

        processing = true;
        handler.postDelayed(() -> {
            try {
                tryApplyRequestedState();
            } finally {
                processing = false;
            }
        }, 180);
    }

    private void tryApplyRequestedState() {
        Boolean target = requestedState;
        if (target == null) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        AccessibilityNodeInfo switchNode = findLikelyMasterSwitch(root);
        if (switchNode == null) return;

        boolean current = switchNode.isChecked();
        if (current != target) {
            boolean clicked = switchNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (!clicked) {
                AccessibilityNodeInfo p = switchNode.getParent();
                while (p != null && !p.isClickable()) p = p.getParent();
                if (p != null) p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        requestedState = null;
        handler.postDelayed(() -> performGlobalAction(GLOBAL_ACTION_BACK), 450);
    }

    private AccessibilityNodeInfo findLikelyMasterSwitch(AccessibilityNodeInfo root) {
        ArrayDeque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        AccessibilityNodeInfo best = null;
        int bestScore = Integer.MIN_VALUE;

        while (!queue.isEmpty()) {
            AccessibilityNodeInfo n = queue.removeFirst();
            int score = scoreNode(n);
            if (score > bestScore) {
                bestScore = score;
                best = n;
            }

            for (int i = 0; i < n.getChildCount(); i++) {
                AccessibilityNodeInfo child = n.getChild(i);
                if (child != null) queue.addLast(child);
            }
        }

        return bestScore >= 40 ? best : null;
    }

    private int scoreNode(AccessibilityNodeInfo node) {
        if (!node.isCheckable()) return Integer.MIN_VALUE;

        int score = 10;
        String cls = safe(node.getClassName());
        String id = safe(node.getViewIdResourceName());
        String text = (safe(node.getText()) + " " + safe(node.getContentDescription())).toLowerCase(Locale.ROOT);

        if (cls.toLowerCase(Locale.ROOT).contains("switch")) score += 35;
        if (id.contains("switch_widget") || id.contains("switch_bar") || id.endsWith(":id/switch_text")) score += 80;
        if (containsDataSaverWords(text)) score += 60;

        AccessibilityNodeInfo p = node.getParent();
        for (int i = 0; i < 2 && p != null; i++, p = p.getParent()) {
            String parentText = collectDirectText(p).toLowerCase(Locale.ROOT);
            if (containsDataSaverWords(parentText)) score += 45;
        }
        return score;
    }

    private boolean containsDataSaverWords(String s) {
        return s.contains("oszczędz") || s.contains("włącz teraz") || s.contains("wlacz teraz") ||
                s.contains("data saver") || s.contains("turn on now");
    }

    private String collectDirectText(AccessibilityNodeInfo node) {
        StringBuilder out = new StringBuilder();
        append(out, node.getText());
        append(out, node.getContentDescription());
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo c = node.getChild(i);
            if (c != null) {
                append(out, c.getText());
                append(out, c.getContentDescription());
            }
        }
        return out.toString();
    }

    private void append(StringBuilder sb, CharSequence value) {
        if (!TextUtils.isEmpty(value)) sb.append(' ').append(value);
    }

    private String safe(CharSequence value) {
        return value == null ? "" : value.toString();
    }

    public static boolean isConnected() {
        return instance != null;
    }

    public static boolean requestToggle(boolean enabled) {
        DataSaverAccessibilityService service = instance;
        if (service == null) return false;
        service.requestedState = enabled;
        service.requestStartedAt = System.currentTimeMillis();
        service.openDataSaverSettingsInternal();
        return true;
    }

    public static void openDataSaverSettings(Context context) {
        Intent direct = new Intent(DATA_SAVER_ACTION);
        direct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(direct);
        } catch (Exception directFailed) {
            try {
                Intent fallback = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallback);
            } catch (Exception ignored) {
                Toast.makeText(context, "Nie udało się otworzyć ustawień transmisji danych.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openDataSaverSettingsInternal() {
        Intent direct = new Intent(DATA_SAVER_ACTION);
        direct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(direct);
        } catch (Exception e) {
            requestedState = null;
            Toast.makeText(this, "Ten firmware nie udostępnia bezpośredniego ekranu Oszczędzania danych.", Toast.LENGTH_LONG).show();
            openDataSaverSettings(this);
        }
    }
}

package pl.mateusz.datasavertoggle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView status;
    private TextView serviceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshState();
    }

    private View buildUi() {
        int pad = dp(24);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(36), pad, pad);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Oszczędzanie danych");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(20, 20, 20));
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap(dp(0)));

        TextView subtitle = new TextView(this);
        subtitle.setText("Samsung / Android — szybki przełącznik");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(90, 90, 90));
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = matchWrap(dp(0));
        subLp.topMargin = dp(8);
        root.addView(subtitle, subLp);

        status = new TextView(this);
        status.setTextSize(24);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams stateLp = matchWrap(dp(0));
        stateLp.topMargin = dp(42);
        stateLp.bottomMargin = dp(28);
        root.addView(status, stateLp);

        Button enable = bigButton("WŁĄCZ");
        enable.setOnClickListener(v -> requestDataSaver(true));
        root.addView(enable, buttonLp());

        Button disable = bigButton("WYŁĄCZ");
        disable.setOnClickListener(v -> requestDataSaver(false));
        LinearLayout.LayoutParams disableLp = buttonLp();
        disableLp.topMargin = dp(12);
        root.addView(disable, disableLp);

        serviceStatus = new TextView(this);
        serviceStatus.setTextSize(14);
        serviceStatus.setTextColor(Color.rgb(100, 100, 100));
        serviceStatus.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams svcLp = matchWrap(dp(0));
        svcLp.topMargin = dp(34);
        root.addView(serviceStatus, svcLp);

        Button accessibility = smallButton("Włącz / sprawdź usługę dostępu");
        accessibility.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(this, "Nie udało się otworzyć ustawień ułatwień dostępu.", Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout.LayoutParams accLp = buttonLp();
        accLp.topMargin = dp(10);
        root.addView(accessibility, accLp);

        Button manual = smallButton("Otwórz Oszczędzanie danych ręcznie");
        manual.setOnClickListener(v -> DataSaverAccessibilityService.openDataSaverSettings(this));
        LinearLayout.LayoutParams manualLp = buttonLp();
        manualLp.topMargin = dp(10);
        root.addView(manual, manualLp);

        TextView note = new TextView(this);
        note.setText("Usługa dostępu działa tylko po naciśnięciu WŁĄCZ/WYŁĄCZ i tylko na ekranie Ustawień systemu.");
        note.setTextSize(12);
        note.setTextColor(Color.rgb(120, 120, 120));
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams noteLp = matchWrap(dp(0));
        noteLp.topMargin = dp(24);
        root.addView(note, noteLp);

        return root;
    }

    private void requestDataSaver(boolean enabled) {
        if (!DataSaverAccessibilityService.isConnected()) {
            Toast.makeText(this, "Najpierw włącz usługę „Przełączanie oszczędzania danych”.", Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception ignored) { }
            return;
        }

        boolean accepted = DataSaverAccessibilityService.requestToggle(enabled);
        if (!accepted) {
            Toast.makeText(this, "Usługa nie jest jeszcze gotowa. Włącz ją ponownie w Ułatwieniach dostępu.", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshState() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        int value = cm.getRestrictBackgroundStatus();
        boolean enabled = value != ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;

        status.setText(enabled ? "WŁĄCZONE" : "WYŁĄCZONE");
        status.setTextColor(enabled ? Color.rgb(0, 115, 70) : Color.rgb(175, 35, 35));

        boolean service = DataSaverAccessibilityService.isConnected();
        serviceStatus.setText(service ? "Usługa: gotowa" : "Usługa: wymaga jednorazowego włączenia");
        serviceStatus.setTextColor(service ? Color.rgb(0, 115, 70) : Color.rgb(150, 90, 0));
    }

    private Button bigButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(19);
        b.setAllCaps(false);
        b.setMinHeight(dp(60));
        return b;
    }

    private Button smallButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setMinHeight(dp(48));
        return b;
    }

    private LinearLayout.LayoutParams buttonLp() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams matchWrap(int bottomMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = bottomMargin;
        return lp;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

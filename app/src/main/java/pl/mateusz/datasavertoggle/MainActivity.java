package pl.mateusz.datasavertoggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String DATA_SAVER_ACTION = "android.settings.DATA_SAVER_SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
    }

    private android.view.View buildUi() {
        int pad = dp(24);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(42), pad, pad);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Oszczędzanie danych");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(20, 20, 20));
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView info = new TextView(this);
        info.setText("Dodaj widget „Oszczędzanie danych” na ekran główny.\nJedno tapnięcie otworzy bezpośrednio systemową zakładkę.");
        info.setTextSize(17);
        info.setTextColor(Color.rgb(80, 80, 80));
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        infoLp.topMargin = dp(24);
        root.addView(info, infoLp);

        Button open = new Button(this);
        open.setText("OTWÓRZ OSZCZĘDZANIE DANYCH");
        open.setTextSize(17);
        open.setAllCaps(false);
        open.setMinHeight(dp(58));
        open.setOnClickListener(v -> openDataSaverSettings());
        LinearLayout.LayoutParams openLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        openLp.topMargin = dp(36);
        root.addView(open, openLp);

        TextView hint = new TextView(this);
        hint.setText("Widget: przytrzymaj pusty obszar ekranu głównego → Widgety → Data Saver.");
        hint.setTextSize(14);
        hint.setTextColor(Color.rgb(110, 110, 110));
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        hintLp.topMargin = dp(28);
        root.addView(hint, hintLp);

        return root;
    }

    private void openDataSaverSettings() {
        Intent direct = new Intent(DATA_SAVER_ACTION);
        try {
            startActivity(direct);
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
            } catch (Exception ignored) {
                Toast.makeText(this, "Nie udało się otworzyć ustawień transmisji danych.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

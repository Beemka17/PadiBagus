package app.ij.mlwithtensorflowlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    TextView jdul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        jdul=findViewById(R.id.jdul);

        String text = "<font color=#0F9942>Padi</font><font color=#F8C13F>Bagus</font>";
        jdul.setText(Html.fromHtml(text));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Cobamenu.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
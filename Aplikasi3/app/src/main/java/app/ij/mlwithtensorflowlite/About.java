package app.ij.mlwithtensorflowlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class About extends AppCompatActivity {

    private EditText etEmail;
    private EditText etSubject;
    private EditText etMessage;
    private Button btnSend;

    private ImageView wa_logo, ig_logo, li_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        etEmail = findViewById(R.id.etEmail);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        wa_logo = findViewById(R.id.wa_logo);
        ig_logo = findViewById(R.id.ig_logo);
        li_logo = findViewById(R.id.linked);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tentang Aplikasi");
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        String email = etEmail.getText().toString();
        String subject = etSubject.getText().toString();
        String message = etMessage.getText().toString();

        if (email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(About.this,"Mohon isi form dengan lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "bmkusuma17@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL,"bmkusuma17@gmail.com");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "From: " + email + "\n\n" + message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // Handle error jika tidak ada aplikasi email
        }
    }

    public void openig(View view) {
        String url = "https://www.instagram.com/b__emka/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openwa(View view) {
        String url = "https://wa.link/ykfd4w";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openli(View view) {
        String url = "https://www.linkedin.com/in/bagus-kusuma/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
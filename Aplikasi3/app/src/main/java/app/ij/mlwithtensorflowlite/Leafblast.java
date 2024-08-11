package app.ij.mlwithtensorflowlite;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.text.Html;

import app.ij.mlwithtensorflowlite.databinding.ActivityLeafblastBinding;

public class Leafblast extends AppCompatActivity {

    private ActivityLeafblastBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLeafblastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Leafblast");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textViewUnlistedItems = findViewById(R.id.ul);

        String htmlText = "<ul>" +
                "<li>Penggunaan varietas padi yang tahan, terhadap penyakit ini seperti Inpari 21, Inpari 22, Inpari 26, Inpari 27, Inpago 4, Inpago 5, Inpago 6, Inpago 7, Inpago 8</li>" +
                "<li>Jarak tanam yang tidak terlalu rapat atau sistem legowo sangat dianjurkan untuk membuat kondisi lingkungan tidak menguntungkan bagi patogen penyebab penyakit</li>" +
                "<li>Penggunaan Fungisida Envoy 80 WP yang merupakan fungisida sistemik dengan dua bahan aktif mancozeb dan trisiklazol,sangat ampuh mengendalikan blast daun dan potong leher daun pada tanaman padi.</li>" +
                "</ul>";
        //textViewUnlistedItems.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
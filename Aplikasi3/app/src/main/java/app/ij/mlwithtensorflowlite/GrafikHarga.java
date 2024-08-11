package app.ij.mlwithtensorflowlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrafikHarga extends AppCompatActivity {

    private static final String URLS = "https://webapi.bps.go.id/v1/api/list/model/data/lang/ind/domain/0000/var/295/key/17a46cefa35f981c950288804603342d";
    private RequestQueue requestQueue;
    private LineChart lineChart;
    private Button go;
    private static final String TAG = "grafik harga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik_harga);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Grafik Harga Beras");
        }

        lineChart = findViewById(R.id.lineChart);
        go = findViewById(R.id.go);
        requestQueue = Volley.newRequestQueue(this);

        fetchLatestRicePrice();
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite();
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

    private void fetchLatestRicePrice() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            JsonObject dataContent = jsonObject.getAsJsonObject("datacontent");

                            if (dataContent == null) {
                                throw new Exception("datacontent tidak ditemukan di dalam JSON");
                            }

                            // Prepare data for the chart
                            List<Entry> entries = new ArrayList<>();
                            double previousValue = 0.0;
                            String latestKey = null;
                            double latestValue = 0.0;

                            Iterator<Map.Entry<String, JsonElement>> iterator = dataContent.entrySet().iterator();
                            int index = 0;

                            // Create an array for month labels
                            String[] months = {
                                    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                            };
                            List<String> monthLabels = new ArrayList<>();

                            while (iterator.hasNext()) {
                                Map.Entry<String, JsonElement> entry = iterator.next();
                                String key = entry.getKey();
                                // Check if the key starts with "12950124" to filter data for the current year
                                if (key.startsWith("12950124")) {
                                    previousValue = latestValue;
                                    latestKey = key;
                                    latestValue = entry.getValue().getAsDouble();

                                    entries.add(new Entry(index, (float) latestValue));
                                    // Add corresponding month label
                                    int monthNumber = Integer.parseInt(key.substring(key.length() - 1));
                                    monthLabels.add(months[monthNumber - 1]);

                                    index++;
                                }
                            }

                            if (latestKey == null) {
                                throw new Exception("Tidak ada data dalam datacontent");
                            }



                            // Update LineChart
                            LineDataSet dataSet = new LineDataSet(entries, "Harga Beras");
                            dataSet.setColor(Color.BLUE);
                            dataSet.setValueTextColor(Color.BLACK);
                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);
                            Description description = new Description();
                            description.setText("Pergerakan Harga Beras");
                            lineChart.setDescription(description);

                            // Set month labels on X axis
                            XAxis xAxis = lineChart.getXAxis();
                            xAxis.setValueFormatter(new CustomValueFormatter(monthLabels));
                            xAxis.setGranularity(1f); // Set the interval to 1 (for month labels)
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                            lineChart.invalidate(); // refresh

                        } catch (Exception e) {
                            Log.e(TAG, "Gagal menguraikan data: ", e);
                            Toast.makeText(GrafikHarga.this, "Gagal menguraikan data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Gagal mendapatkan data: ", error);
                Toast.makeText(GrafikHarga.this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private void openWebsite() {
        String url = "https://www.bps.go.id/id/statistics-table/2/Mjk1IzI=/rata-rata-harga-beras-di-tingkat-perdagangan-besar--grosir--indonesia.html";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
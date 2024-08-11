package app.ij.mlwithtensorflowlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.os.Bundle;

public class Cobamenu extends AppCompatActivity {
    private TextView result_info;
    private TextView result_info2;
    private TextView result_info3;
    private ImageView image_cuaca;
    private ImageView ivTrendIcon;
    private TextView city;
    private TextView tvRicePrice;
    private TextView tvLastMonth;
    private RequestQueue requestQueue;
    private static final String URLS = "https://webapi.bps.go.id/v1/api/list/model/data/lang/ind/domain/0000/var/295/key/17a46cefa35f981c950288804603342d";
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    Button start_ml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobamenu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        result_info= findViewById(R.id.result_info);
        result_info2= findViewById(R.id.result_info2);
        result_info3= findViewById(R.id.result_info3);
        city= findViewById(R.id.city);
        image_cuaca= findViewById(R.id.image_cuaca);
        start_ml=findViewById(R.id.ml_start);
        tvRicePrice = findViewById(R.id.harga);
        tvLastMonth = findViewById(R.id.bulan);
        ivTrendIcon = findViewById(R.id.ivTrendIcon);
        requestQueue = Volley.newRequestQueue(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        fetchLatestRicePrice();
        ///String cityname = city.getText().toString();
        //Log.e("cityname",cityname);
        //String cityname1 = cityname.replace("Kecamatan","");
        String cobaja = "Purwakarta";
        //String key= "20a0b53984ecbce68b9776ebc01a0f9f";
        //String url= "https://api.openweathermap.org/data/2.5/weather?q=" + cityname + "&lang=id&&appid="+key+"&units=metric";

        //Log.e("url",url);
        //new GetURLData().execute(url);

        start_ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Cobamenu.this, MainActivity.class));
            }
        });
    }

    private String getLocation(){
        final String[] citynama = {"Not Found"};
        String key= "20a0b53984ecbce68b9776ebc01a0f9f";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null){



                                try {
                                    Geocoder geocoder = new Geocoder(Cobamenu.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    citynama[0] = addresses.get(0).getLocality();
                                    String cityname1 = citynama[0].replace("Kecamatan","");
                                    city.setText(addresses.get(0).getLocality());
                                    Log.e("result", citynama[0]);
                                    String key= "20a0b53984ecbce68b9776ebc01a0f9f";
                                    String url= "https://api.openweathermap.org/data/2.5/weather?q=" + cityname1 + "&lang=id&&appid="+key+"&units=metric";
                                    new GetURLData().execute(url);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    });


        }else {

            askPermission();


        }

        return citynama[0];

    }

    private void askPermission() {

        ActivityCompat.requestPermissions(Cobamenu.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                getLocation();

            }else {


                Toast.makeText(Cobamenu.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();

            }



        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class GetURLData extends AsyncTask<String, String, String>{
        String cityname = city.getText().toString();
        String key= "20a0b53984ecbce68b9776ebc01a0f9f";
        String url= "https://api.openweathermap.org/data/2.5/weather?q=" + cityname + "&lang=id&&appid="+key+"&units=metric";

        protected void onPreExecute(){
            super.onPreExecute();
            result_info.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader =  new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line="";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection !=null)
                    connection.disconnect();
                try {
                    if(reader!=null)
                        reader.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result){
            Double n = null;
            super.onPostExecute(result);
            try {
                JSONObject jsonObject= new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("weather");
                JSONObject object = array.getJSONObject(0);
                String description = object.getString("description");
                String id1 = object.getString("id");
                int newid = Integer.parseInt (id1);

                //result_info.setText(""+ jsonObject.getJSONObject("main").getString("temp"));
                JSONObject main=jsonObject.getJSONObject("main");
                String temp=main.getString("temp");
                String hum=main.getString("humidity");
                result_info.setText(temp+"°C");
                result_info2.setText(""+ description);
                result_info3.setText("Kelembapan: "+hum+"%");
                n=jsonObject.getJSONObject("main").getDouble("temp");

                if(newid==800) {
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.clear_sky);
                }
                else if(newid>=200 && newid<=232){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.thunderstorm);
                }
                else if(newid>=300 && newid<=321){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.showe);
                }
                else if(newid>=500 && newid<=504){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.rain);
                }
                else if(newid== 511){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.snow);
                }
                else if(newid>=520 && newid<=531){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.showe);
                }
                else if(newid>=600 && newid<=622){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.snow);
                }
                else if(newid>=701 && newid<=781){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.mist);
                }
                else if(newid==801){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.few_clouds);
                }
                else if(newid==802){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.scattered);
                }
                else if(newid>=803){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.broken);
                }
                else if(newid>=804){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.broken);
                }

                //if(n>=22 && n<=28 && (newid==800 || newid==801 || newid == 802 || newid ==803 || newid==804)) result_info3.setText("Hari Yang Cerah Untuk Bertani");
                //else if(n<22) result_info3.setText("Hari Yang Cukup Dingin Untuk Bertani");
               // else if(n>28) result_info3.setText("Hari Yang Cukup Panas Untuk Bertani");
                //else result_info3.setText("Suhu Hari Ini Cukup Baik, Namun Cuacanya Buruk");



            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    }

    public void bs_page(View view) {
        Intent intent = new Intent(Cobamenu.this, Brownspot.class);
        startActivity(intent);
    }

    public void hispa_page(View view) {
        Intent intent = new Intent(Cobamenu.this, Hispa.class);
        startActivity(intent);
    }

    public void lb_page(View view) {
        Intent intent = new Intent(Cobamenu.this, Leafblast.class);
        startActivity(intent);
    }

    public void grafik(View view){
        Intent intent = new Intent(Cobamenu.this,GrafikHarga.class);
        startActivity(intent);
    }

    public void cuaca_detail(View view){
        Intent intent = new Intent(Cobamenu.this,PengaruhCuaca.class);
        startActivity(intent);
    }

    public void about(View view){
        Intent intent = new Intent(Cobamenu.this,About.class);
        startActivity(intent);
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
                            // Iterate to find the latest key-value pair
                            String latestKey = null;
                            double latestValue = 0.0;
                            double previousValue = 0.0;
                            Iterator<Map.Entry<String, com.google.gson.JsonElement>> iterator = dataContent.entrySet().iterator();

                            while (iterator.hasNext()) {
                                Map.Entry<String, com.google.gson.JsonElement> entry = iterator.next();
                                previousValue = latestValue;
                                latestKey = entry.getKey();
                                latestValue = entry.getValue().getAsDouble();
                            }

                            String lastMonth = getLastMonth(latestKey);
                            tvRicePrice.setText("Rp. " + latestValue);
                            tvLastMonth.setText("Harga Perbulan : " + lastMonth);
                            // Update color and icon based on the value comparison
                            if (latestValue > previousValue) {
                                tvRicePrice.setTextColor(Color.parseColor("#1cbd57"));
                                ivTrendIcon.setImageResource(R.drawable.ic_upward);
                            } else if (latestValue < previousValue) {
                                tvRicePrice.setTextColor(Color.parseColor("#e01102"));
                                ivTrendIcon.setImageResource(R.drawable.baseline_call_received_24);
                            } else {
                                tvRicePrice.setTextColor(Color.BLACK);
                                ivTrendIcon.setImageResource(android.R.color.transparent); // No icon if no change
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Cobamenu.this, "Gagal menguraikan data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Cobamenu.this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private String getLastMonth(String latestKey) {
        // Assuming the latestKey format is "12950112X" where X is the month number
        int monthNumber = Integer.parseInt(latestKey.substring(latestKey.length() - 1));
        String[] months = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        return months[monthNumber - 1];
    }

    public void openig() {
        String url = "https://www.instagram.com/b__emka/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openwa() {
        String url = "https://wa.link/ykfd4w";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openli() {
        String url = "https://www.linkedin.com/in/bagus-kusuma/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
package app.ij.mlwithtensorflowlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Glide;

import javax.net.ssl.HttpsURLConnection;

public class PengaruhCuaca extends AppCompatActivity {

    private TextView result_info;
    private TextView result_info2;
    private TextView result_info3;
    private ImageView image_cuaca;
    private TextView city;
    private TextView pengaruh_lb, pengaruh_bs, pengaruh_hp;
    private final static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaruh_cuaca);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pengaruh Cuaca");
        }

        result_info= findViewById(R.id.result_info);
        result_info2= findViewById(R.id.result_info2);
        result_info3= findViewById(R.id.result_info3);
        city= findViewById(R.id.city);
        image_cuaca= findViewById(R.id.image_cuaca);
        pengaruh_bs = findViewById(R.id.pengaruh_bs);
        pengaruh_lb = findViewById(R.id.pengaruh_lb);
        pengaruh_hp = findViewById(R.id.pengaruh_hp);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

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

    private String getLocation(){
        final String[] citynama = {"Not Found"};
        String key= "PAKE API KEY LU SENDIRI MEK";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null){



                                try {
                                    Geocoder geocoder = new Geocoder(PengaruhCuaca.this, Locale.getDefault());
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

        ActivityCompat.requestPermissions(PengaruhCuaca.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                getLocation();

            }else {


                Toast.makeText(PengaruhCuaca.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();

            }



        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class GetURLData extends AsyncTask<String, String, String> {
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
                String icon = object.getString("icon");
                String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";

                String id1 = object.getString("id");
                int newid = Integer.parseInt (id1);

                //result_info.setText(""+ jsonObject.getJSONObject("main").getString("temp"));
                JSONObject main=jsonObject.getJSONObject("main");
                String temp=main.getString("temp");
                String hum=main.getString("humidity");
                //float newhum = Float.parseFloat(hum);
                float newtemp = Float.parseFloat(temp);
                int newhum = Integer.parseInt(hum);
                //int newtemp = Integer.parseInt(temp);
                result_info.setText(temp+"°C");
                result_info2.setText(description+" / ");
                result_info3.setText("Kelembapan: "+hum+"%");
                n=jsonObject.getJSONObject("main").getDouble("temp");

                if (newtemp>=25 && newtemp<=27 && newhum>=89){
                    pengaruh_bs.setText("Bakteri Bipolaris oryzae yang menyebabkan penyakit Brownspot pada Padi berkembang biak dan menyerang dengan cepat pada suhu dan kelembapan saat ini. Segera periksa kondisi kesehatan padi Anda!");
                    pengaruh_bs.setBackgroundResource(R.drawable.rounded_red);
                } else if (newtemp>=25 && newtemp<=30 && newhum>=70) {
                    pengaruh_hp.setText("Hama Hispa berkembang biak dan menyerang dengan cepat pada suhu dan kelembapan saat ini. Segera periksa kondisi kesehatan padi Anda!");
                    pengaruh_hp.setBackgroundResource(R.drawable.rounded_red);
                } else if (newtemp>=26 && newtemp<=27 && newhum==95) {
                    pengaruh_lb.setText("Bakteri Pyricula oryzae berkembang biak dan menyerang dengan cepat pada suhu dan kelembapan saat ini. Segera periksa kondisi kesehatan padi Anda!");
                    pengaruh_lb.setBackgroundResource(R.drawable.rounded_red);
                }

                if(newid==800) {
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.clear_sky_100);
                }
                else if(newid>=200 && newid<=232){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.thunderstorm_100);
                }
                else if(newid>=300 && newid<=321){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.showe_100);
                }
                else if(newid>=500 && newid<=504){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.rain_100);
                }
                else if(newid== 511){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.snow_100);
                }
                else if(newid>=520 && newid<=531){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.showe_100);
                }
                else if(newid>=600 && newid<=622){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.snow_100);
                }
                else if(newid>=701 && newid<=781){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.mist_100);
                }
                else if(newid==801){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.few_clouds_100);
                }
                else if(newid==802){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.scattered_100);
                }
                else if(newid>=803){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.broken_100);
                }
                else if(newid>=804){
                    image_cuaca.setVisibility(View.VISIBLE);
                    image_cuaca.setImageResource(R.drawable.broken_100);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    }
}

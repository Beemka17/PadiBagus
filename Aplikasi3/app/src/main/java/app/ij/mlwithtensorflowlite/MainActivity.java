/*
 * Created by ishaanjav
 * github.com/ishaanjav
 */

package app.ij.mlwithtensorflowlite;

import  androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.ij.mlwithtensorflowlite.ml.ModelpadiNew;
import app.ij.mlwithtensorflowlite.ml.Modelpadicnn;
import app.ij.mlwithtensorflowlite.ml.Modelpadicnn2;
import app.ij.mlwithtensorflowlite.ml.Modelsayaterbaru;
import app.ij.mlwithtensorflowlite.ml.Modelpadi;


public class MainActivity extends AppCompatActivity {

    Button camera, gallery, detail;
    ImageView imageView;
    TextView result;
    TextView desc;
    TextView poh;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        detail = findViewById(R.id.testButtonId);

        result = findViewById(R.id.result);
        desc = findViewById(R.id.desc);
        imageView = findViewById(R.id.imageView);
        poh = findViewById(R.id.classified);

        detail.setVisibility(View.GONE);
        poh.setVisibility(View.GONE);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
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

    public void classifyImage(Bitmap image){

        try {
            //Modelsayaterbaru model = Modelsayaterbaru.newInstance(getApplicationContext());
           // Modelpadi model = Modelpadi.newInstance(getApplicationContext());
            //ModelpadiNew model = ModelpadiNew.newInstance(getApplicationContext());
            Modelpadicnn2 modelnew = Modelpadicnn2.newInstance(getApplicationContext());

            // Creates inputs for reference.
            //TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 180, 180, 3}, DataType.FLOAT32);
            //TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            //Modelsayaterbaru.Outputs outputs = model.process(inputFeature0);
            //TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //Modelpadi.Outputs outputs = model.process(inputFeature0);
            //ModelpadiNew.Outputs outputs = model.process(inputFeature0);
            Modelpadicnn2.Outputs outputs = modelnew.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"BrownSpot", "Healthy", "Hispa", "LeafBlast", "No Object"};
            String[] desk = {
                    "Tanaman terjangkit penyakit Brownspot",
                    "Kondisi tanaman sehat",
                    "Tanaman terjangkit penyakit Hispa",
                    "Tanaman terjangkit penyakit Leafblast",
                    "Mohon masukkan foto daun padi"
            };
            result.setText(classes[maxPos]);
            desc.setText(desk[maxPos]);
            final String pler=classes.toString();

            if(classes[maxPos].equals("Hispa")) {
                poh.setVisibility(View.VISIBLE);
                detail.setVisibility(View.VISIBLE);
                detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, Hispa.class));
                    }
                });
            }

            else if(classes[maxPos].equals("BrownSpot")) {
                poh.setVisibility(View.VISIBLE);
                detail.setVisibility(View.VISIBLE);
                detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, Brownspot.class));
                    }
                });
            }

            else if(classes[maxPos].equals("LeafBlast")) {
                poh.setVisibility(View.VISIBLE);
                detail.setVisibility(View.VISIBLE);
                detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, Leafblast.class));
                    }
                });
            }

            else if(classes[maxPos].equals("Healthy")) {
                poh.setVisibility(View.VISIBLE);
                detail.setVisibility(View.GONE);
            }

            else if(classes[maxPos].equals("No Object")) {
                poh.setVisibility(View.VISIBLE);
                detail.setVisibility(View.GONE);
            }

            // Releases model resources if no longer used.

//            result.setText(outputFeature0.getFloatArray()[0] + "\n"+outputFeature0.getFloatArray()[1]);
            modelnew.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
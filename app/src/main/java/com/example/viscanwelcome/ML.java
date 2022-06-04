package com.example.viscanwelcome;

import static java.lang.Integer.parseInt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.viscanwelcome.ml.Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ML extends AppCompatActivity {
    TextView result, confidence;
    FloatingActionButton camera, folder;
   // TextView confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;
    private static int req_code = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ml);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
      //  picture = findViewById(R.id.button);
        camera = findViewById(R.id.cameraButton);
        folder = findViewById(R.id.folderButton);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                someActivityResultLauncher.launch(iCamera);
                req_code = 0;

            }
        });

        folder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(gallery);
                req_code =1;

            }
        });
    }
  @RequiresApi(api = Build.VERSION_CODES.N)
 /* public void classifyImage1(Bitmap bitmap){
      String s = null;
      try {
          Model model = Model.newInstance(getApplicationContext());
        //  Model model = Model.newInstance(context);
          // Creates inputs for reference.
          TensorImage image = TensorImage.fromBitmap(bitmap);

          // Runs model inference and gets result.
          Model.Outputs outputs = model.process(image);
          List<Category> probability = outputs.getProbabilityAsCategoryList();
          System.out.println("Output value is:"+outputs);

          float maxConfidence = 0.0F;
          int maxPos = 1;
          float value;
          System.out.println("probability size :"+probability.size());
          for(int i = 0; i < probability.size(); i++){
              if(!(probability.get(i).toString().contains("-"))) {
                  String[] val0 = (probability.get(i).toString().split("="));

                  String[] val = val0[2].split("\\)");
                  if (val[0].contains("(score")) {
                      val = val0[3].split("\\)");
                  }

                  value = Float.parseFloat(val[0]);
                  System.out.println("Value is:" + value);

                  if (value > maxConfidence) {
                      maxConfidence = value;
                      maxPos = i;
                  }

              }
          }
          System.out.println("probability size :"+probability.size());
          String[] val1 = (probability.get(maxPos).toString().split("="));
          String output = val1[0].substring(11).replace(" (displayName","");
          String[] val2 = val1[2].split("\\)");
          s = "The name of wine is \""+ output + " and probability is " + val2[0] ;
          confidence.setText(s);

          // Releases model resources if no longer used.
          model.close();
      } catch (  IOException e) {
          System.out.println("The exception is:"+e);
          confidence.setText("Image is not clear or the object can't be identified as Wine bottle label. Try again!!!");
          // TODO Handle the exception
      }
  } */

  public void classifyImage(Bitmap image){
      try {
          Model model = Model.newInstance(getApplicationContext());

          // Creates inputs for reference.
          TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
          ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
          byteBuffer.order(ByteOrder.nativeOrder());

          // get 1D array of 224 * 224 pixels in image
          int [] intValues = new int[imageSize * imageSize];
          image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

          // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
          int pixel = 0;
          for(int i = 0; i < imageSize; i++){
              for(int j = 0; j < imageSize; j++){
                  int val = intValues[pixel++]; // RGB
                  byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                  byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                  byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
              }
          }

          inputFeature0.loadBuffer(byteBuffer);

          // Runs model inference and gets result.
          Model.Outputs outputs = model.process(inputFeature0);
          TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
          float[] confidences = outputFeature0.getFloatArray();
          // find the index of the class with the biggest confidence.
          int maxPos = 0;
          float maxConfidence = 0;
          for(int i = 0; i < confidences.length; i++){
              if(confidences[i] > maxConfidence){
                  maxConfidence = confidences[i];
                  maxPos = i;
              }
          }
          String[] classes = {"Club des sommeliers malbec", "casa defra 1404", "Larentis espumante brut", "Dominic hentall fiano", " san pedro gato negro merlot"};
          result.setText(classes[maxPos]);

          String s = "";
          if(confidences[maxPos] > 0.7)
          {
            //  String s2 = classes[maxPos];
//              String[] classes = {"Club des sommeliers malbec", "casa defra 1404", "Larentis espumante brut", "Dominic hentall fiano", " san pedro gato negro merlot"};
//              result.setText(classes[3]);
              s = classes[maxPos];
              result.setText(s);
      //        s = confidences[maxPos] *100 +"%";
              confidence.setText(confidences[maxPos] *100 +"%");
          }
          else
          {
              s = "Image is not clear or the object can't be identified as Wine bottle label. Try again!!!";
              result.setText(s);
              confidence.setText(" - ");
          }

//          for(int i = 0; i < classes.length; i++){
//              s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
//          }
          result.setText(s);


          // Releases model resources if no longer used.
          model.close();
      } catch (IOException e) {
          // TODO Handle the exception
      }
  }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && req_code==1) {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        Bitmap image = null;
                        try {
                            image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int dimension = Math.min(image.getWidth(), image.getHeight());
                        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                        imageView.setImageBitmap(image);

                        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                        //    bitmap.compress(Bitmap.CompressFormat.JPEG,80,image);
                        classifyImage(image);

                    }
                    else if (result.getResultCode() == Activity.RESULT_OK && req_code==0) {
                           Intent data = result.getData();
                           Bitmap image = (Bitmap) data.getExtras().get("data");
                        int dimension = Math.min(image.getWidth(), image.getHeight());
                        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                        imageView.setImageBitmap(image);

                        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    //    bitmap.compress(Bitmap.CompressFormat.JPEG,80,image);
                        classifyImage(image);

                    }
                  //
                    //  super.onActivityResult(requestCode, resultCode, data);
                }
            });
}
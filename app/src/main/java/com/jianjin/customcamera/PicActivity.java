package com.jianjin.customcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jianjin.camera.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PicActivity extends Activity implements ProgressRequestBody.UploadCallbacks {

    byte[] byteArray;
    String PicId;
    Handler myHandler;
    String Result;
    String Confidence;
    String fileName;
    ApiService apiService;
    ApiService2 apiService2;

    ApiService3 apiService3;
    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pic);
      //  final TextView t6 = (TextView) findViewById(R.id.textView);
      //  t6.setText("Click Upload & Please Wait!");
      //  t6.setVisibility(View.VISIBLE);




        final ImageView imageView = (ImageView) findViewById(R.id.image_view);
        final String imgPath = getIntent().getStringExtra("imgUri");
        final Bitmap bitmap = BitmapFactory.decodeFile(imgPath);


        final Uri sampleUri = Uri.parse(imgPath);
        //Bitmap newResolution = getResizedBitmap(bitmap);


        imageView.setImageBitmap(bitmap);
        final Button fabUpload = (Button) findViewById(R.id.fabUpload);
        final Button button = (Button) findViewById(R.id.button);
        fabUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // cameraContainer.takePicture(CameraActivity.this);
                //t6.setText("Please wait...");

                multipartImageUpload(bitmap);

                fabUpload.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

                finish();
              //  startActivity(getIntent());



            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // cameraContainer.takePicture(CameraActivity.this);
                //t6.setText("Please wait...");

                multipartImageUpload1(bitmap);
                finish();



            }
        });

       // t6.setText("Please wait!");
       // t6.setVisibility(View.VISIBLE);
        // multipartImageUpload(sampleUri);


    }





    private void multipartImageUpload(Bitmap bitmap) {
    //    final TextView t6 = (TextView) findViewById(R.id.textView);
       // t6.setText("Please wait...");
        //t6.setVisibility(View.VISIBLE);

        initRetrofitClient1();
        try {

            String mParentFilePath = FileUtils.getPhotoPathForLockWallPaper();


            String mImagePath = mParentFilePath + File.separator
                    + new DateFormat().format("yyyyMMddHHmmss",
                    new Date()).toString()
                    + ".jpg";
         //   Bitmap bitmap1 = getResizedBitmap(bitmap);
         //   Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,720,960,true);




            File file = new File(mImagePath);
           // file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //Bitmap customBit = Bitmap.createScaledBitmap(bitmap,1024, 576, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);

            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            // textView.setTextColor(Color.BLUE);

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

            Call<ResponseBody> req = apiService3.uploadImage(body,name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //  textView.setText("Uploaded Failed!");
                    //  textView.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });




/*
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            RequestBody name = RequestBody.create(MediaType.parse("application/json"), "file");

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();



           // Call<ResponseBody> req = apiService.uploadImage(body,name);
            Call<MyResponse> req = apiService.uploadImage(body,name);


            req.enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                    ImageView imageView = (ImageView) findViewById(R.id.image_view);
                    MyResponse obj = response.body();
                    Log.e("TAG","Path"+ obj.im_path);
                    String subString = obj.im_path;
                    fileName = subString.substring(subString.lastIndexOf("/") + 1);
                    Picasso.get().load("http://10.50.2.119:5000/classified/"+fileName).into(imageView);
                    imageView.setVisibility(View.VISIBLE);


                 //   Log.e("TAG", "response 33: "+response.headers().toString());
                  //  Log.e("TAG", "response 33: "+response.body());
                   // Log.e("TAG", "response 33: "+response.toString());
                    Log.e("TAG", "response 33: "+new Gson().toJson(response.body()));
                    Log.e("TAG", "response 33: "+ response.raw().toString());
                  //  Log.e("TAG", "response 33: "+new Gson().toJson(response));


                }


                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    //  textView.setText("Uploaded Failed!");
                    //  textView.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
*/
            /*
            OkHttpClient client = new OkHttpClient.Builder().build();
            apiService3 = new Retrofit.Builder().baseUrl("http://10.50.2.119:5000").client(client).build().create(ApiService3.class);

            Call call = apiService3.getData();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "response 33: "+new Gson().toJson(response.body()) );
                }


                @Override
                public void onFailure(Call call, Throwable t) {
                    //  textView.setText("Uploaded Failed!");
                    //  textView.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

**/


            //post delay
//fetchResult();






        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void multipartImageUpload1(Bitmap bitmap) {
        //    final TextView t6 = (TextView) findViewById(R.id.textView);
        // t6.setText("Please wait...");
        //t6.setVisibility(View.VISIBLE);

        initRetrofitClient2();
        try {

            String mParentFilePath = FileUtils.getPhotoPathForLockWallPaper();


            String mImagePath = mParentFilePath + File.separator
                    + new DateFormat().format("yyyyMMddHHmmss",
                    new Date()).toString()
                    + ".jpg";
            //   Bitmap bitmap1 = getResizedBitmap(bitmap);
             //    Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,960,720,true);




            File file = new File(mImagePath);
            // file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //Bitmap customBit = Bitmap.createScaledBitmap(bitmap,1024, 576, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);

            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            // textView.setTextColor(Color.BLUE);

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

            Call<ResponseBody> req = apiService2.uploadImage(body,name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //  textView.setText("Uploaded Failed!");
                    //  textView.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

            //post delay
//fetchResult();




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void someMethod(String thisValue){
        String getValue = thisValue;

    }

    private void fetchResult(){



    try {
            Thread.sleep(9000);
    }catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ConnectionClass con = new ConnectionClass();
        Connection conn = null;

        String ConnURL;
        try {
            java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            String classs = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://10.50.0.40:1433;databaseName=StripClub;user=sa;password=Immuno207!";
            conn = DriverManager.getConnection(ConnURL);
            String SPsql = "EXEC sp_FetchStripInfo ?";
            PreparedStatement ps = conn.prepareStatement(SPsql);
            ps.setEscapeProcessing(true);
            ps.setString(1, PicId);
            ps.executeQuery();
            // String query = "INSERT INTO tblStrip (NodeJsID,TestedFlag) values (8,0)";
            // Statement stmt = conn.createStatement();
            ResultSet rs  = ps.getResultSet();

            while(rs.next()){
                Result = rs.getString("Result");
                Confidence = rs.getString("Confidence");
                final TextView t1 = (TextView) findViewById(R.id.textView3);
                final TextView t2 = (TextView) findViewById(R.id.textView4);
              //  final TextView t6 = (TextView) findViewById(R.id.textView);
                final Button fabUpload = (Button) findViewById(R.id.fabUpload);

                if(Result == "0") {
                    t1.setText("Result is Negative!" + " " + "Confidence Level: " +Confidence);
                    t1.setTextColor(Color.GREEN);
                    //t2.setText(Confidence);
                }
                else if (Result == "1"){
                    t1.setText("Result is Positive!" + " " + "Confidence Level: " +Confidence);
                    t1.setTextColor(Color.RED);
                    //t2.setText(Confidence);
                }
                else{
                    t1.setText("Connection Timed Out!");
                    t1.setTextColor(Color.RED);
                }
                fabUpload.setVisibility(View.GONE);
          //      t6.setVisibility(View.GONE);
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.GONE);
                //someMethod(PicId);

                  //  Toast.makeText(getApplicationContext(), Result + ":" + Confidence + " ", Toast.LENGTH_SHORT).show();

            }
            //stmt.executeUpdate(query);

        }
        catch (SQLException se)
        {
            Log.e("SQLException", se.getMessage());
        }
        catch (ClassNotFoundException e) {
        }
        catch (Exception e) {
            Log.e("3RR03", e.getMessage());
        }

    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) 960) / width;
        // create a matrix for the manipulation
        float scaleHeight = ((float) 720) / height;
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false );
        image.recycle();
        return resizedBitmap;
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //change the ip to yours.
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.50.2.119:5000").addConverterFactory(GsonConverterFactory.create(gson)).build();
        apiService = retrofit.create(ApiService.class);
    }

    private void initRetrofitClient1() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        //change the ip to yours.
        apiService3 = new Retrofit.Builder().baseUrl("http://10.50.0.70:3000").client(client).build().create(ApiService3.class);
    }

    private void initRetrofitClient2() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        //change the ip to yours.
        apiService2 = new Retrofit.Builder().baseUrl("http://10.50.0.70:3000").client(client).build().create(ApiService2.class);
    }

    private void getJson(){
       // OkHttpClient client = new OkHttpClient.Builder().build();
        //apiService3 = new Retrofit.Builder().baseUrl("http://10.50.2.119:5000").client(client).build().create(ApiService3.class);

    }

    @Override
    public void onProgressUpdate(int percentage) {
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void uploadStart() {

    }


}

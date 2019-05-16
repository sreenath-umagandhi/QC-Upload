package com.jianjin.customcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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

import com.jianjin.camera.utils.FileUtils;

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

public class PicActivity extends Activity implements ProgressRequestBody.UploadCallbacks {

    byte[] byteArray;
    String PicId;
    Handler myHandler;
    String Result;
    String Confidence;
    ApiService apiService;
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
        ImageView imageView = (ImageView) findViewById(R.id.image_view);




        final String imgPath = getIntent().getStringExtra("imgUri");
        final Bitmap bitmap = BitmapFactory.decodeFile(imgPath);


        final Uri sampleUri = Uri.parse(imgPath);
        //Bitmap newResolution = getResizedBitmap(bitmap);

        imageView.setImageBitmap(bitmap);
        Button fabUpload = (Button) findViewById(R.id.fabUpload);
        Button button = (Button) findViewById(R.id.button);
        fabUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // cameraContainer.takePicture(CameraActivity.this);
                //t6.setText("Please wait...");

                multipartImageUpload(bitmap);



            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // cameraContainer.takePicture(CameraActivity.this);
                //t6.setText("Please wait...");

                multipartImageUpload1(bitmap);



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

        initRetrofitClient();
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

            Call<ResponseBody> req = apiService.uploadImage(body,name);
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

    private void multipartImageUpload1(Bitmap bitmap) {
        //    final TextView t6 = (TextView) findViewById(R.id.textView);
        // t6.setText("Please wait...");
        //t6.setVisibility(View.VISIBLE);

        initRetrofitClient();
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
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload1", file.getName(), fileBody);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload1");

            Call<ResponseBody> req = apiService.uploadImage(body,name);
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
            Log.e("safiya", se.getMessage());
        }
        catch (ClassNotFoundException e) {
        }
        catch (Exception e) {
            Log.e("error", e.getMessage());
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

        //change the ip to yours.
        apiService = new Retrofit.Builder().baseUrl("http://10.50.0.70:3000").client(client).build().create(ApiService.class);
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

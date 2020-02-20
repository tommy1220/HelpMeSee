package com.example.vizassist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vizassist.imagepipeline.ImageActions;
import com.example.vizassist.utilities.HttpUtilities;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String UPLOAD_HTTP_URL = "http://173.255.117.247:8080/vizassist/annotate";

    private static final int IMAGE_CAPTURE_CODE = 1;
    private static final int SELECT_IMAGE_CODE = 2;


    private static final int CAMERA_PERMISSION_REQUEST = 1001;

    private MainActivityUIController mainActivityUIController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityUIController = new MainActivityUIController(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityUIController.resume();
    }

    @Override
    //how to interact with users' selection? Here the parem item tells me which item user has selected
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {//see which menu user has clicked
            case R.id.action_capture://take photo
                mainActivityUIController.updateResultView(getString(R.string.result_placeholder));//update the view first (replace previous results displayed on the screen, if any)
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//check if user has permission to take photo
                    mainActivityUIController.askForPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST);
                }
                else {
                    ImageActions.startCameraActivity(this, IMAGE_CAPTURE_CODE);
                }
                break;

            case R.id.action_gallery://if user clicked gallery menu item
                mainActivityUIController.updateResultView(getString(R.string.result_placeholder));//覆盖之前的结果with a placeHolder first
                ImageActions.startGalleryActivity(this, SELECT_IMAGE_CODE);//then start a new activity: 在相册里选图片
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    /**
     * This function is used when activity2 is done finishing its job and call back to activity1
     * @param data Intent data is what passes back to activity
     * @param resultCode indicates whether activity2 succeeds finishing its jobs --> "OK" or "result_error", etc
     * @param requestCode differentiates whether the callbacker activity2 is from gallery or from camera capturing,
     *                    this is the same requestCode when activity1 passes to activity2 when invoking the funciton
     *                    startActivityForResult(int requestCode, ....)
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == IMAGE_CAPTURE_CODE) {//if the data is coming back from image capturing
                bitmap = (Bitmap) data.getExtras().get("data");//Bundle: similar to hashMap, using "data" as key to get value
                mainActivityUIController.updateImageViewWithBitmap(bitmap);//update the image with the data
            }
            else if (requestCode == SELECT_IMAGE_CODE) {//if the data is coming back from picture selection
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            selectedImage);//use Android SDK's function to fetch the content
                    mainActivityUIController.updateImageViewWithBitmap(bitmap);
                }
                catch (IOException e) {//because i'm using a functino to fetch data, so it's likely not able to fetch
                    mainActivityUIController.showErrorDialogWithMessage(R.string.reading_error_message);
                }
            }

            if (bitmap != null) {//use a seperate one-time discardable thread only to upload the image, which might take like 2 sec (heavy duty), so better let it run on background
                final Bitmap bitmapToUpload = bitmap;//once task sent out, not changable --> final
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadImage(bitmapToUpload);
                    }
                });
                thread.start();
            }
        }
    }

    /**
     * The POST method to upload image
     */
    private void uploadImage(Bitmap bitmap) {
        try {
            HttpURLConnection conn = HttpUtilities.makeHttpPostConnectionToUploadImage(bitmap, UPLOAD_HTTP_URL);//bitmap: upload what; UPLOAD_HTTP_URL: upload to where
            conn.connect();//similar to press the Enter button on a browser
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mainActivityUIController.updateResultView(HttpUtilities.parseOCRResponse(conn));//parse the useful information from the OCR response and update it to view (replace that "no recognition ..." String)
            }
            else {
                mainActivityUIController.showInternetError();
            }
        }
        /**
         * When http connection request returns a response code that is not HTTP_OK (200): conn.getResponseCode() != HttpURLConnection.HTTP_OK
         *
         * WHen Exceptions were caught during Image commpressino (a bad image file);
         * http transmission (lost connection in the middle);
         * result parsing (JSON format problem or server malfunctioned and returned unreadable message)
         */
        catch (ClientProtocolException e) {
            e.printStackTrace();
            mainActivityUIController.showInternetError();
        }
        catch (IOException e) {
            e.printStackTrace();
            mainActivityUIController.showInternetError();
        }
        catch (JSONException e) {
            e.printStackTrace();
            mainActivityUIController.showInternetError();
        }
    }

}
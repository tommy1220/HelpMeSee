package com.example.vizassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Controller of main activity.
 */
public class MainActivityUIController {
    private final Activity activity;
    private final Handler mainThreadHandler;

    private TextView resultView;
    private ImageView imageView;

    public MainActivityUIController(Activity activity) {
        this.activity = activity;
        //because only main thread can touch the VIEW, so when OCRReponse are retrieved, it needs to be sent to mainTHread via the Handler
        this.mainThreadHandler = new Handler(Looper.getMainLooper());//因为后台线程对view的操作是不能自己独立post的，要返回到主线成
    }

    public void resume() {
        resultView = activity.findViewById(R.id.resultView);
        imageView = activity.findViewById(R.id.capturedImage);
    }

    //Once we get the result test from our server by reading the http response code, we will post a "message" to the
    //main UI, notice this message can be in the form of Runnable, and notice only mainThread can touch the view
    public void updateResultView(final String text) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {//the only task here is to set the text in resultView
                resultView.setText(text);
            }
        });
    }

    public void updateImageViewWithBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void showErrorDialogWithMessage(int messageStringID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.error_dialog_title);
        builder.setMessage(messageStringID);
        builder.setPositiveButton(R.string.error_dialog_dismiss_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    public void showInternetError() {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //ask users for permissions, say, use camera to capture photos
    public void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            //should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //This is invoked if user has denied the permission before
                //In this case I'm just asking the permission again
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
            else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }
    }


}

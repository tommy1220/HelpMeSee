package com.example.vizassist.imagepipeline;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Actions to get images from either the device back camera or photo gallery
 */
public class ImageActions {

    /**
     * Start the built-in back camera to capture a still image.
     * @param activity origin activity in which the intent will be from.
     * @param requestCode request code to get result when the camera activity is dismissed.
     */

    //NOTE: intent is a "message used to communicate between app components like activities"
    //      say, activity1 goes to activity2 with some messages(intent)

    //here, the intent's target is to take picture, we create intent using System's ACTION_IMAGE_CAPTURE,
    //with this intent, we fire it to start the image taking activity
    public static void startCameraActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult doesn't leave activity1 alone. WHen it finishes activity2, it brings back the result to activity1
        //usually with startActivity, activity1 just doesn't care what comes after anymore
        //requestCode is what differentiates which intent is being passed to activity2, and what info to bring back to activity1
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Start photo gallery image picker to select a saved image.
     * @param activity origin activity in which the intent will be from.
     * @param requestCode request code to get result when the gallery activity is dismissed.
     */
    public static void startGalleryActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }
}

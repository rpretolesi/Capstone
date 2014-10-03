package com.pretolesi.capstone.bitmap;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapFromFileWorkerTask extends AsyncTask<String, Void, Bitmap> 
{
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;

    public BitmapFromFileWorkerTask(ImageView imageView) 
    {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) 
    {
    	String pathName = params[0];
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return BitmapUtil.getInstance().loadSyncBitmapFromFile(pathName,100,100);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) 
    {
        if (imageViewReference != null && bitmap != null) 
        {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) 
            {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}

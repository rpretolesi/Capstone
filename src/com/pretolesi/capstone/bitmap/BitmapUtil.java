package com.pretolesi.capstone.bitmap;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapUtil {

	private static final BitmapUtil INSTANCE = new BitmapUtil();
    
    private BitmapUtil(){ }

    public static BitmapUtil getInstance(){
        return INSTANCE;
    }
/*    
    public void show(){
        System.out.println("Singleon using static initialization in Java");
    }
*/

/*
    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }
*/
    private Bitmap mPlaceHolderBitmap;
    
    public void loadAsyncBitmapFromFile(Context context, int iDefaultImageResIDString, String pathName, int reqWidth, int reqHeight, ImageView imageView) 
    {
        if (cancelPotentialWork(pathName, imageView)) 
        {
            final BitmapFromFileWorkerTask task = new BitmapFromFileWorkerTask(imageView, reqWidth, reqHeight);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(pathName);
        }

        
//        BitmapFromFileWorkerTask task = new BitmapFromFileWorkerTask(imageView, reqWidth, reqHeight);
//        task.execute(pathName);
    }
    
    public Bitmap loadSyncBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public Bitmap loadSyncBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
    {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	    
    }
    
    public boolean cancelPotentialWork(String pathName, ImageView imageView) 
    {
        final BitmapFromFileWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) 
        {
            final String bitmapPathName = bitmapWorkerTask.pathName;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapPathName == "" || bitmapPathName != pathName) 
            {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            }
            else 
            {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    
    private BitmapFromFileWorkerTask getBitmapWorkerTask(ImageView imageView) 
    {
	   if (imageView != null) 
	   {
	       final Drawable drawable = imageView.getDrawable();
	       if (drawable instanceof AsyncDrawable) 
	       {
	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
	           return asyncDrawable.getBitmapWorkerTask();
	       }
	   }
	   return null;
    }
    
    
    public class BitmapFromFileWorkerTask extends AsyncTask<String, Void, Bitmap> 
    {
        private final WeakReference<ImageView> imageViewReference;
        private String pathName;
        private int iImageViewWidth;
        private int iImageViewHeight;

        public BitmapFromFileWorkerTask(ImageView imageView, int iImageViewWidth, int iImageViewHeight) 
        {
            // Use a WeakReference to ensure the ImageView can be garbage collected
        	this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.iImageViewWidth = iImageViewWidth;
            this.iImageViewHeight = iImageViewHeight;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) 
        {
        	pathName = params[0];
        	try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	return BitmapUtil.getInstance().loadSyncBitmapFromFile(pathName, iImageViewWidth, iImageViewHeight);
        }

        // Once complete, see if ImageView is still around and set bitmap.
    /*    
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
    */    
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapFromFileWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    
    static class AsyncDrawable extends BitmapDrawable 
    {
        private final WeakReference<BitmapFromFileWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapFromFileWorkerTask bitmapWorkerTask) 
        {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapFromFileWorkerTask>(bitmapWorkerTask);
        }

        public BitmapFromFileWorkerTask getBitmapWorkerTask() 
        {
            return bitmapWorkerTaskReference.get();
        }
    }    
}

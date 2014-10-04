package com.pretolesi.capstone.bitmap;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

public class BitmapUtil
{
	
	private static final BitmapUtil INSTANCE = new BitmapUtil();
    
    private BitmapUtil()
    { 
    	
    }

    public static BitmapUtil getInstance()
    {
        return INSTANCE;
    }
    
    // Cache on memory
    private LruCache<String, Bitmap> mMemoryCache = null;
    
    public void setMemoryCache()
    {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) 
		{
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) 
	        {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) 
    {
    	if(mMemoryCache != null)
    	{
	        if (getBitmapFromMemCache(key) == null) 
	        {
	            mMemoryCache.put(key, bitmap);
	        }
    	}
    }

    public Bitmap getBitmapFromMemCache(String key) 
    {
    	if(mMemoryCache != null)
    	{
    		return mMemoryCache.get(key);
    	}
    	else
    	{
    		return null;
    	}
    }
    
 
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
    	
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth)
	    {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) 
	        {
	        	inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
    
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) 
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) 
    {

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
    
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> 
    {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<Resources> resourcesReference;
        private int reqWidth, reqHeight;
        private int data = 0;

        public BitmapWorkerTask(Resources res, ImageView imageView, int reqWidth, int reqHeight) 
        {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            resourcesReference = new WeakReference<Resources>(res);
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) 
        {
            data = params[0];
            
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            final Bitmap bitmap = decodeSampledBitmapFromResource(resourcesReference.get(), data, reqWidth, reqHeight);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);

            return bitmap;
        }
/*        
        @Override
        protected Bitmap doInBackground(Integer... params) 
        {
            data = params[0];
            
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return decodeSampledBitmapFromResource(resourcesReference.get(), data, reqWidth, reqHeight);
        }        
 
     protected Bitmap doInBackground(Integer... params) {
        final Bitmap bitmap = decodeSampledBitmapFromResource(
                getResources(), params[0], 100, 100));
        addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        return bitmap;
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
  */
        @Override
        protected void onPostExecute(Bitmap bitmap) 
        {
            if (isCancelled()) 
            {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) 
            {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) 
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    } 
/*    
    public void loadBitmap(Resources res, int resId, ImageView imageView, int reqWidth, int reqHeight) {
        BitmapWorkerTask task = new BitmapWorkerTask(res, imageView, reqWidth, reqHeight);
        task.execute(resId);
    }

    public void loadBitmap(Resources res, int resPlaceHolderId, int resId, ImageView imageView, int reqWidth, int reqHeight) 
    {
        if (cancelPotentialWork(resId, imageView)) 
        {
        	final Bitmap mPlaceHolderBitmap = decodeSampledBitmapFromResource(res, resPlaceHolderId, reqHeight, reqHeight);
            final BitmapWorkerTask task = new BitmapWorkerTask(res, imageView, reqWidth, reqHeight);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(res, mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }     
*/    
    public void loadBitmap(Resources res, int resPlaceHolderId, int resId, ImageView imageView, int reqWidth, int reqHeight) 
    {
        final String imageKey = String.valueOf(resId);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) 
        {
        	imageView.setImageBitmap(bitmap);
        } 
        else 
        {
            if (cancelPotentialWork(resId, imageView)) 
            {
            	final Bitmap mPlaceHolderBitmap = decodeSampledBitmapFromResource(res, resPlaceHolderId, reqHeight, reqHeight);
                final BitmapWorkerTask task = new BitmapWorkerTask(res, imageView, reqWidth, reqHeight);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(res, mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }
        
    }  
    
    public static boolean cancelPotentialWork(int data, ImageView imageView) 
    {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) 
        {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) 
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
    
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) 
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
    
    static class AsyncDrawable extends BitmapDrawable 
    {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) 
        {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() 
        {
            return bitmapWorkerTaskReference.get();
        }
    }    
}

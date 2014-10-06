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
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class BitmapUtil
{
    private BitmapUtil()
    {     	
    }	
    
    private static class LazyHolder 
    {
        private static final BitmapUtil INSTANCE = new BitmapUtil();
    }
    
    public static BitmapUtil getInstance() 
    {
        return LazyHolder.INSTANCE;
    }

    // Placeholder Image
    private Bitmap m_bmpPlaceHolderBitmap = null;
    
    public void setPlaceHolder (Bitmap bmpPlaceHolderBitmap) 
    {
    	m_bmpPlaceHolderBitmap = bmpPlaceHolderBitmap;
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
    
 
    
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
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
    
    public Bitmap decodeSampledBitmapFromFile(Resources res, String strImagePath, int reqWidth, int reqHeight) 
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strImagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(strImagePath, options);
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
         
            final Bitmap bitmap = decodeSampledBitmapFromResource(resourcesReference.get(), data, reqWidth, reqHeight);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);

            return bitmap;
        }

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
    
    class BitmapWorkerTaskFromFile extends AsyncTask<String, Void, Bitmap> 
    {
        private final WeakReference<ImageView> m_imageViewReference;
        private final WeakReference<Resources> m_resourcesReference;
        private int m_reqWidth, m_reqHeight;
        private String m_strImagePath;

        public BitmapWorkerTaskFromFile(Resources res, ImageView imageView, int reqWidth, int reqHeight) 
        {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            m_imageViewReference = new WeakReference<ImageView>(imageView);
            m_resourcesReference = new WeakReference<Resources>(res);
            this.m_reqWidth = reqWidth;
            this.m_reqHeight = reqHeight;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) 
        {
        	m_strImagePath = params[0];
            
            final Bitmap bitmap = decodeSampledBitmapFromFile(m_resourcesReference.get(), m_strImagePath, m_reqWidth, m_reqHeight);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) 
        {
            if (isCancelled()) 
            {
                bitmap = null;
            }

            if (m_imageViewReference != null && bitmap != null) 
            {
                final ImageView imageView = m_imageViewReference.get();
                final BitmapWorkerTaskFromFile bitmapWorkerTaskFromFile = getBitmapWorkerTaskFromFile(imageView);
                if (this == bitmapWorkerTaskFromFile && imageView != null) 
                {
                    imageView.setImageBitmap(bitmap);

                }
            }
        }
    } 


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
                final BitmapWorkerTask task = new BitmapWorkerTask(res, imageView, reqWidth, reqHeight);
                if(m_bmpPlaceHolderBitmap == null)
                {
                	m_bmpPlaceHolderBitmap = decodeSampledBitmapFromResource(res, resPlaceHolderId, reqHeight, reqHeight);
                }
                final AsyncDrawable asyncDrawable = new AsyncDrawable(res, m_bmpPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resId);
            }
        }
        
    }  
    
    public void loadBitmap(Resources res, int resPlaceHolderId, String strImagePath, ImageView imageView, int reqWidth, int reqHeight) 
    {
        final String imageKey = String.valueOf(strImagePath);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) 
        {
        	imageView.setImageBitmap(bitmap);
        } 
        else 
        {
            if (cancelPotentialWorkFromFile(strImagePath, imageView)) 
            {
                final BitmapWorkerTaskFromFile task = new BitmapWorkerTaskFromFile(res, imageView, reqWidth, reqHeight);
                if(m_bmpPlaceHolderBitmap == null)
                {
                	m_bmpPlaceHolderBitmap = decodeSampledBitmapFromResource(res, resPlaceHolderId, reqHeight, reqHeight);
                }
                final AsyncDrawableFromFile asyncDrawable = new AsyncDrawableFromFile(res, m_bmpPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(strImagePath);
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strImagePath);
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
    
    public static boolean cancelPotentialWorkFromFile(String strImagePath, ImageView imageView) 
    {
   	
        final BitmapWorkerTaskFromFile bitmapWorkerTask = getBitmapWorkerTaskFromFile(imageView);

        if (bitmapWorkerTask != null) 
        {
            final String bitmapImagePath = bitmapWorkerTask.m_strImagePath;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapImagePath == "" || bitmapImagePath != strImagePath) 
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
    
    static private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) 
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
    
    static private BitmapWorkerTaskFromFile getBitmapWorkerTaskFromFile(ImageView imageView) 
    {
	   if (imageView != null) 
	   {
	       final Drawable drawable = imageView.getDrawable();
	       if (drawable instanceof AsyncDrawableFromFile) 
	       {
	           final AsyncDrawableFromFile asyncDrawable = (AsyncDrawableFromFile) drawable;
	           return asyncDrawable.getBitmapWorkerTaskFromFile();
	       }
	    }
	    return null;
	}
    
    static class AsyncDrawable extends BitmapDrawable 
    {
        private final WeakReference<BitmapWorkerTask> m_bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) 
        {
            super(res, bitmap);
            m_bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() 
        {
            return m_bitmapWorkerTaskReference.get();
        }
    }    
    
    static class AsyncDrawableFromFile extends BitmapDrawable 
    {
        private final WeakReference<BitmapWorkerTaskFromFile> m_bitmapWorkerTaskReference;

        public AsyncDrawableFromFile(Resources res, Bitmap bitmap, BitmapWorkerTaskFromFile bitmapWorkerTask) 
        {
            super(res, bitmap);
            m_bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTaskFromFile>(bitmapWorkerTask);
        }

        public BitmapWorkerTaskFromFile getBitmapWorkerTaskFromFile() 
        {
            return m_bitmapWorkerTaskReference.get();
        }
    }      
}

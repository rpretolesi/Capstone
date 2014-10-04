package com.pretolesi.capstone;


import java.lang.ref.WeakReference;

import com.pretolesi.capstone.bitmap.BitmapUtil;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);

        Uri u = Uri.parse("file:///storage/emulated/0/Pictures/MyCameraApp/IMG_20140930_120803.jpg");
		String str = u.getPath();

		BitmapUtil.getInstance().setMemoryCache();
		BitmapUtil.getInstance().loadBitmap(this.getResources(), R.drawable.place_holder, R.drawable.test, imageView1,100,100);
		//Bitmap bmp = BitmapUtil.getInstance().decodeSampledBitmapFromResource(this.getResources(), R.drawable.test, 100, 100);
        //imageView1.setImageBitmap(bmp);
        		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
       
}

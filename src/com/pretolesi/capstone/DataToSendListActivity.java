package com.pretolesi.capstone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;


public class DataToSendListActivity extends ListActivity 
{
	  public void onCreate(Bundle savedInstanceState) 
	  {
		    super.onCreate(savedInstanceState);
		    
			List<DataToSendModel> dtsms = getListFiles(new File("/storage/emulated/0/Pictures/MyCameraApp")); 

		    DataToSendAdapter adapter = new DataToSendAdapter(this);
		    setListAdapter(adapter);		  
		    adapter.updateDataToSend(dtsms);
	  }	    
	 private List<DataToSendModel> getListFiles(File parentDir) 
	 {
		ArrayList<DataToSendModel> inDataToSendModel = new ArrayList<DataToSendModel>();
		File[] files = parentDir.listFiles();
		for (File file : files) 
		{
//		    if (file.isDirectory()) 
//		    {
//		        inFiles.addAll(getListFiles(file));
//		    } 
//		    else 
//		    {
		        if(file.getName().endsWith(".jpg"))
		        {
		        	DataToSendModel dtsm = new DataToSendModel();
		        	dtsm.setImageDescriptione(file.getName());
		        	dtsm.setImageUrl(file.getAbsolutePath());
		        	inDataToSendModel.add(dtsm);
		        }
//		    }
		}
		
		return inDataToSendModel;
		
	 }
		 
}

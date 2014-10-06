package com.pretolesi.capstone;

import android.widget.ImageView;

public class DataToSendModel {

	public DataToSendModel() {
		// TODO Auto-generated constructor stub
	}
	
     private  ImageView m_ivImageView;
     private  String m_strImageUrl;
     private  String m_strImageDescription;
      
     /*********** Set Methods ******************/
      
     public void setImageUrl(String strImageUrl)
     {
         this.m_strImageUrl = strImageUrl;
     }
      
     public void setImageDescriptione(String strImageDescription)
     {
         this.m_strImageDescription = strImageDescription;
     }
 
     /*********** Get Methods ****************/
      
     public String getImageUrl()
     {
         return this.m_strImageUrl;
     }
      
     public String getImageDescriptione()
     {
         return this.m_strImageDescription;
     }

}

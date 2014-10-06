package com.pretolesi.capstone;

import java.util.Collections;
import java.util.List;

import com.pretolesi.capstone.bitmap.BitmapUtil;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class DataToSendAdapter extends BaseAdapter 
{
	private List<DataToSendModel> m_dtsm = Collections.emptyList();

    private final Context context;

    // the context is needed to inflate views in getView()
    public DataToSendAdapter(Context context) {
        this.context = context;
    }

    public void updateDataToSend(List<DataToSendModel> dtsm) 
    {
        this.m_dtsm = dtsm;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return m_dtsm.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public DataToSendModel getItem(int position) {
        return m_dtsm.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        if (convertView == null) 
        {
            convertView = LayoutInflater.from(context)
              .inflate(R.layout.data_to_send_list_activity, parent, false);
        }

        ImageView ivImageView = ViewHolder.get(convertView, R.id.imageViewToSend);
        TextView tvTextView = ViewHolder.get(convertView, R.id.textViewToSend);

        DataToSendModel dtsm = getItem(position);

		BitmapUtil.getInstance().loadBitmap(context.getResources(), R.drawable.place_holder, dtsm.getImageUrl(), ivImageView,100,100);

        tvTextView.setText(dtsm.getImageDescriptione());

        return convertView;
        
    }
}

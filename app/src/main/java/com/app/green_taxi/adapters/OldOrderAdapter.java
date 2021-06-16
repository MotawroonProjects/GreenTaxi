package com.app.green_taxi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Current_Order;
import com.app.green_taxi.databinding.CurrentOrderRowBinding;
import com.app.green_taxi.databinding.OldOrderRowBinding;
import com.app.green_taxi.models.CurrentOrderModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class OldOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CurrentOrderModel> list;
    private Context context;
    private LayoutInflater inflater;

    public OldOrderAdapter(List<CurrentOrderModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OldOrderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.old_order_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public  class MyHolder extends RecyclerView.ViewHolder {
        public OldOrderRowBinding binding;
        public MyHolder(@NonNull OldOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }

    }




}

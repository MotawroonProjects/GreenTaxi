package com.app.green_taxi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Current_Order;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Home;
import com.app.green_taxi.databinding.CurrentOrderRowBinding;
import com.app.green_taxi.databinding.NewOrderRowBinding;
import com.app.green_taxi.models.CurrentOrderModel;
import com.app.green_taxi.models.OrderDataModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class CurrentOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CurrentOrderModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment_Current_Order fragment_current_order;

    public CurrentOrderAdapter(List<CurrentOrderModel> list, Context context, Fragment_Current_Order fragment_current_order) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment_current_order = fragment_current_order;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CurrentOrderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.current_order_row, parent, false);
        return new MyHolder(binding,context);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.initMapView();

            myHolder.binding.imageCall.setOnClickListener(v -> {
                fragment_current_order.call(list.get(myHolder.getAdapterPosition()));
            });

            myHolder.binding.imageCall.setOnClickListener(v -> {
                fragment_current_order.whatsApp(list.get(myHolder.getAdapterPosition()));
            });

            myHolder.binding.imageCall.setOnClickListener(v -> {
                fragment_current_order.copy(list.get(myHolder.getAdapterPosition()));
            });

            myHolder.binding.llDone.setOnClickListener(v -> {
                fragment_current_order.finishOrder(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public  class MyHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback{
        public CurrentOrderRowBinding binding;
        private GoogleMap mMap;
        private Context context;
        public MyHolder(@NonNull CurrentOrderRowBinding binding,Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;



        }
        private void initMapView(){
            binding.mapView.onCreate(null);
            binding.mapView.onResume();
            binding.mapView.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (googleMap!=null){
                mMap = googleMap;
                MapsInitializer.initialize(context.getApplicationContext());
                new GoogleMapOptions().liteMode(true);
                LatLng latLng = new LatLng(list.get(getAdapterPosition()).getLatitude(),list.get(getAdapterPosition()).getLongitude());
                addMarker(latLng,list.get(getAdapterPosition()).getAddress());
            }
        }

        private void addMarker(LatLng latLng, String address) {
            mMap.addMarker(new MarkerOptions().title(address).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,8.5f));
        }
    }




}

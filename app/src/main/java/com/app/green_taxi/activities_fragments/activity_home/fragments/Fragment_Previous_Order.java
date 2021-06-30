package com.app.green_taxi.activities_fragments.activity_home.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.HomeActivity;
import com.app.green_taxi.adapters.CurrentOrderAdapter;
import com.app.green_taxi.adapters.OldOrderAdapter;
import com.app.green_taxi.databinding.FragmentOrdersBinding;
import com.app.green_taxi.models.CurrentOrderDataModel;
import com.app.green_taxi.models.CurrentOrderModel;
import com.app.green_taxi.models.UserModel;
import com.app.green_taxi.preferences.Preferences;
import com.app.green_taxi.remote.Api;
import com.app.green_taxi.tags.Tags;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Previous_Order extends Fragment {

    private HomeActivity activity;
    private FragmentOrdersBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private List<CurrentOrderModel> list;
    private OldOrderAdapter adapter;


    public static Fragment_Previous_Order newInstance() {
        return new Fragment_Previous_Order();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
        initView();

        return binding.getRoot();
    }


    private void initView() {
        list = new ArrayList<>();

        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new OldOrderAdapter(list,activity);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getOrders);
        getOrders();
    }





    public void getOrders()
    {
        try {

            Api.getService(Tags.base_url)
                    .getCurrentPreviousOrders(userModel.getData().getId(),"old","desc")
                    .enqueue(new Callback<CurrentOrderDataModel>() {
                        @Override
                        public void onResponse(Call<CurrentOrderDataModel> call, Response<CurrentOrderDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null ) {
                                if (response.body().getStatus()==200){
                                    if (response.body().getData().size()>0){
                                        updateData(response.body().getData());
                                        binding.tvNoData.setVisibility(View.GONE);
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                binding.swipeRefresh.setRefreshing(false);

                            }
                        }

                        @Override
                        public void onFailure(Call<CurrentOrderDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());

                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void updateData(List<CurrentOrderModel> data)
    {
        list.clear();
        adapter.notifyDataSetChanged();
        if (activity.locationModel!=null){
            for (CurrentOrderModel model : data){


                double distance = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",getDistance(new LatLng(activity.locationModel.getLat(),activity.locationModel.getLng()), new LatLng(model.getLatitude(),model.getLongitude()))));
                model.setDistance(distance);
                list.add(model);
            }

            adapter.notifyDataSetChanged();
        }

    }

    private double getDistance(LatLng latLng1, LatLng latLng2) {
        return SphericalUtil.computeDistanceBetween(latLng1, latLng2) / 1000;
    }

}

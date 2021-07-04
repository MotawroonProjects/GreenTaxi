package com.app.green_taxi.activities_fragments.activity_home.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.HomeActivity;
import com.app.green_taxi.adapters.CurrentOrderAdapter;
import com.app.green_taxi.adapters.NewOrderAdapter;
import com.app.green_taxi.databinding.FragmentOrdersBinding;
import com.app.green_taxi.models.CurrentOrderDataModel;
import com.app.green_taxi.models.CurrentOrderModel;
import com.app.green_taxi.models.OrderDataModel;
import com.app.green_taxi.models.StatusResponse;
import com.app.green_taxi.models.UserModel;
import com.app.green_taxi.preferences.Preferences;
import com.app.green_taxi.remote.Api;
import com.app.green_taxi.share.Common;
import com.app.green_taxi.tags.Tags;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Current_Order extends Fragment {

    private HomeActivity activity;
    private FragmentOrdersBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private List<CurrentOrderModel> list;
    private CurrentOrderAdapter adapter;

    public static Fragment_Current_Order newInstance() {
        return new Fragment_Current_Order();
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
        adapter = new CurrentOrderAdapter(list,activity,this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getOrders);
        getOrders();
    }

    public void getOrders()
    {
        try {

            Api.getService(Tags.base_url)
                    .getCurrentPreviousOrders(userModel.getData().getId(),"current","desc")
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


    public void call(CurrentOrderModel model) {
        String phone = model.getUser_fk().getPhone_code()+model.getUser_fk().getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        startActivity(intent);

    }

    public void whatsApp(CurrentOrderModel model) {
        String phone = model.getUser_fk().getPhone_code()+model.getUser_fk().getPhone();
        String url = "https://api.whatsapp.com/send?phone=" + phone;
        try {
            PackageManager pm = activity.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void copy(CurrentOrderModel model) {
        String phone = model.getUser_fk().getPhone_code()+model.getUser_fk().getPhone();

        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
        ClipData item = ClipData.newPlainText("label", phone);
        clipboardManager.setPrimaryClip(item);

    }

    public void finishOrder(CurrentOrderModel model,int pos){
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .finishOrder(userModel.getData().getId(),model.getId(),"old")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                list.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                //activity.displayFragmentPreviousOrder();
                                activity.updateBottonNav(R.id.previous);

                                if (list.size()>0){
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }

                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    public void acceptRefuseOrder(int pos , CurrentOrderModel model, String status){
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .acceptRefuseOrder(userModel.getData().getId(),model.getId(),status)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                list.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                activity.displayFragmentMain();
                                activity.updateBottonNav(R.id.home);

                                if (list.size()>0){
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else {
                                    adapter.notifyDataSetChanged();

                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }

                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

}

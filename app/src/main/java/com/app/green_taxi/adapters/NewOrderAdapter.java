package com.app.green_taxi.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.green_taxi.R;
import com.app.green_taxi.activities_fragments.activity_home.HomeActivity;
import com.app.green_taxi.activities_fragments.activity_home.fragments.Fragment_Home;
import com.app.green_taxi.databinding.NewOrderRowBinding;
import com.app.green_taxi.models.OrderDataModel;


import java.util.List;

public class NewOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment_Home fragment_home;
    private HomeActivity activity;

    public NewOrderAdapter(List<OrderDataModel.Data> list, Context context, Fragment_Home fragment_home) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment_home = fragment_home;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewOrderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.new_order_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            activity=new HomeActivity();
            myHolder.binding.llAccept.setOnClickListener(v -> {
                fragment_home.acceptRefuseOrder(myHolder.getAdapterPosition(),list.get(myHolder.getAdapterPosition()), "accepted");

            });

            myHolder.binding.imageDelete.setOnClickListener(v -> {
                fragment_home.acceptRefuseOrder(myHolder.getAdapterPosition(),list.get(myHolder.getAdapterPosition()), "refused");
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public NewOrderRowBinding binding;

        public MyHolder(@NonNull NewOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}

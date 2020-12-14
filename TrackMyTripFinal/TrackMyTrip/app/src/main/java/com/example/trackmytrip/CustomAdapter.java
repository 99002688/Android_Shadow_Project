package com.example.trackmytrip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    Context context;
    ArrayList tripName, tripDate;
    Integer position;
    private final ClickListerner listener;
    CustomAdapter(Context context,
                  ArrayList tripName,
                  ArrayList tripDate,
                  ClickListerner listener) {
        this.context = context;
        this.tripDate = tripDate;
        this.tripName = tripName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.myrow, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tripdate.setText(String.valueOf(tripDate.get(position)));
        holder.tripname.setText(String.valueOf(tripName.get(position)));
    }

    @Override
    public int getItemCount() {
        return tripName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements com.example.trackmytrip.MyViewHolder {
        private WeakReference<ClickListerner> listenerRef;
        TextView tripname, tripdate;
        Button submit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            tripdate = itemView.findViewById(R.id.tripDate);
            tripname = itemView.findViewById(R.id.tripName);
            submit = itemView.findViewById(R.id.submit);

            submit.setOnClickListener(this::onClick);

        }

        // onClick Listener for view
        @Override

        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }
}

package com.ake.medidorbluetooth.recycleview_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ake.medidorbluetooth.R;

import java.util.ArrayList;

public class RecycleViewBluetoothAdapter extends RecyclerView.Adapter<RecycleViewBluetoothAdapter.ViewHolder>{

    private ArrayList<BluetoothDevice> list;
    private ArrayList<BluetoothDevice> list2;
    private OnClickListenerBluetooth mListener;

    public RecycleViewBluetoothAdapter(ArrayList<BluetoothDevice> list, ArrayList<BluetoothDevice> list2, OnClickListenerBluetooth listener) {
        this.list = list;
        this.list2 = list2;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice device = list.get(position);
        holder.setClickListener(mListener, device);
        holder.getTvNombre().setText(device.getName());
        holder.getTvAddress().setText(device.getAddress());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(BluetoothDevice device){
        if(!list.contains(device) && !list2.contains(device)){
            list.add(device);
            notifyItemInserted(list.size()-1);
        }
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNombre;
        private TextView tvAddress;

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }

        void setClickListener(OnClickListenerBluetooth listener, BluetoothDevice device){
            view.setOnClickListener(view1 -> listener.onClick(device));
        }

        public TextView getTvNombre() {
            return tvNombre;
        }

        public TextView getTvAddress() {
            return tvAddress;
        }
    }

}

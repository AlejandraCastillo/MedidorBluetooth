package com.ake.medidorbluetooth.recycleview_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ake.medidorbluetooth.R;

import java.util.ArrayList;

public class RecycleViewBluetoothAdapter extends RecyclerView.Adapter<RecycleViewBluetoothAdapter.ViewHolder>{
    private static final String TAG = "RecycleViewBluetoothAdapter";

    public ArrayList<BluetoothDevice> list;
    private OnClickListenerBluetooth mListener;

    public RecycleViewBluetoothAdapter(OnClickListenerBluetooth listener) {
        list = new ArrayList<>();
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
        if(!list.contains(device)){
            list.add(device);
            notifyItemInserted(list.size()-1);
            Log.i(TAG, "onReceive: Dispositivo " + device.getName() + device.getAddress() + " agregado");
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

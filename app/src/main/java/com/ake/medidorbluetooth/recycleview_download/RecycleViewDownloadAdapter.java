package com.ake.medidorbluetooth.recycleview_download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ake.medidorbluetooth.R;
import com.ake.medidorbluetooth.database.TablaRegistro;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecycleViewDownloadAdapter extends RecyclerView.Adapter<RecycleViewDownloadAdapter.ViewHolder>{
    private static final String TAG = "RecycleViewDownloadAdapter";

    private ArrayList<TablaRegistro> list;
    private OnClickListenerDownload mListener;

    public RecycleViewDownloadAdapter(ArrayList<TablaRegistro> list, OnClickListenerDownload listener) {
        this.list = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TablaRegistro registro = list.get(position);
        holder.setOnClickListener(mListener, registro);
        holder.getTvRegistro().setText("Registro" + registro.getRegistroID());

        DateFormat formato = new SimpleDateFormat("yy-MM-dd");
        Date fecha = null;
        try {
            fecha = formato.parse(registro.getFecha());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formato1 = new SimpleDateFormat("dd/MM/yy");
        holder.getTvFecha().setText(formato1.format(fecha));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvRegistro;
        private TextView tvFecha;

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvRegistro = itemView.findViewById(R.id.tv_registro);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
        }

        void setOnClickListener(OnClickListenerDownload listener, TablaRegistro registro){
            view.setOnClickListener(view1 -> listener.onClick(registro));
        }

        public TextView getTvRegistro() {
            return tvRegistro;
        }

        public TextView getTvFecha() {
            return tvFecha;
        }
    }
}

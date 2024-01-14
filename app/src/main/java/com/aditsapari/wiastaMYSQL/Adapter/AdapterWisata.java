package com.aditsapari.wiastaMYSQL.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aditsapari.wiastaMYSQL.API.APIRequestData;
import com.aditsapari.wiastaMYSQL.API.RetroServer;
import com.aditsapari.wiastaMYSQL.Activity.wisata.UbahWisataActivity;
import com.aditsapari.wiastaMYSQL.Activity.wisata.WisataActivity;
import com.aditsapari.wiastaMYSQL.Model.DataModel;
import com.aditsapari.wiastaMYSQL.Model.ResponseModel;
import com.aditsapari.wiastaMYSQL.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterWisata extends RecyclerView.Adapter<AdapterWisata.HolderData> {
    private Context ctx;
    private List<DataModel> listModel;

    private int idBudaya;

    public AdapterWisata(Context ctx, List<DataModel> listModel) {
        this.ctx = ctx;
        if (listModel != null) {
            this.listModel = listModel;
        } else {
            this.listModel = new ArrayList<>();
        }
    }


    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wisata, parent, false);

        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModel dm = listModel.get(position);

        holder.tv_id.setText(String.valueOf(dm.getId()));
        holder.tvNama_budaya.setText(dm.getNama_wisata());
        holder.tvdeskripsi_budaya.setText(dm.getDeskripsi_wisata());
        holder.tvAlamat_budaya.setText(dm.getAlamat_wisata());
        holder.harga_budaya.setText(dm.getHarga_tiket());

        String xImage = dm.getImage(); // Ambil URL gambar dari DataModel

        String baseUrl = "https://apiwisatasukabumi.000webhostapp.com/wisata/";
        //String baseUrl = "http://192.168.0.25/native/laundry/wisata/";
        String imageUrl = baseUrl + xImage;
        Log.d("ImageUrl", "URL Gambar: " + imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Uri imageUri = Uri.parse(imageUrl);
            Picasso.get().load(imageUri).into(holder.imageView);
        } else {
            Log.d("ImageUrl", "URL Gambar tidak tersedia");
            // Handle jika URL gambar tidak tersedia
        }
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView tv_id, tvNama_budaya, tvdeskripsi_budaya, tvAlamat_budaya, harga_budaya;
        ImageView imageView;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_idwisata);
            tvNama_budaya = itemView.findViewById(R.id.tv_nama_wisata);
            tvdeskripsi_budaya = itemView.findViewById(R.id.tv_deskripsi_Wisata);
            tvAlamat_budaya = itemView.findViewById(R.id.tv_alamat_wisata);
            harga_budaya = itemView.findViewById(R.id.tv_harga_wisata);
            imageView = itemView.findViewById(R.id.imageViewBudaya);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder dialogpesan = new AlertDialog.Builder(ctx);
                    dialogpesan.setMessage("Pilih Salah Satu Operasi");
                    dialogpesan.setTitle("Perhatian");
                    dialogpesan.setIcon(R.mipmap.ic_launcher_round);
                    dialogpesan.setCancelable(true);

                    idBudaya = Integer.parseInt(tv_id.getText().toString());

                    dialogpesan.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteData();
                            dialogInterface.dismiss();
                            Handler handler = new Handler();

                            ((WisataActivity) ctx).retrieveData();
                        }
                    });

                    dialogpesan.setNegativeButton("Ubah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getData();
                            dialogInterface.dismiss();
                        }
                    });

                    dialogpesan.show();
                    return false;
                }
            });
        }

        private void deleteData() {
            APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponseModel> hapusData = ardData.ardDeleteDatawisata(idBudaya);

            hapusData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int kode = response.body().getKode();
                    String pesan = response.body().getPesan();

                    Toast.makeText(ctx, "kode : " + kode + " | Pesan : " + pesan, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Toast.makeText(ctx, "Gagal Menghubungi Server : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void getData() {
            APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponseModel> tampilData = ardData.ardGetDatawisata(idBudaya);

            tampilData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int kode = response.body().getKode();
                    String pesan = response.body().getPesan();

                    if (response.body().getData() != null && response.body().getData().size() > 0) {
                        DataModel dataModel = response.body().getData().get(0);
                        String xImage = dataModel.getImage(); // Ambil URL gambar dari ResponseModel

                        Intent kirim = new Intent(ctx, UbahWisataActivity.class);
                        kirim.putExtra("xId", dataModel.getId());
                        kirim.putExtra("xNama", dataModel.getNama_wisata());
                        kirim.putExtra("xDeskripsi", dataModel.getDeskripsi_wisata());
                        kirim.putExtra("xAlamat", dataModel.getAlamat_wisata());
                        kirim.putExtra("xhargaTiket", dataModel.getHarga_tiket());
                        kirim.putExtra("xImage", xImage); // Sertakan URL gambar
                        ctx.startActivity(kirim);
                    } else {
                        Toast.makeText(ctx, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Toast.makeText(ctx, "Gagal Menghubungi Server : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
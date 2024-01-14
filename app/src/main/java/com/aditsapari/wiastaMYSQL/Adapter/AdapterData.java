package com.aditsapari.wiastaMYSQL.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aditsapari.wiastaMYSQL.API.APIRequestData;
import com.aditsapari.wiastaMYSQL.API.RetroServer;
import com.aditsapari.wiastaMYSQL.Activity.travel.UbahActivity;
import com.aditsapari.wiastaMYSQL.Activity.wisata.WisataActivity;
import com.aditsapari.wiastaMYSQL.Model.DataModel;
import com.aditsapari.wiastaMYSQL.Model.ResponseModel;
import com.aditsapari.wiastaMYSQL.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData>{
    private Context ctx;
    private List<DataModel> listModel;
    private List<DataModel> listLaundry;
    private int idLaundry;

    public AdapterData(Context ctx, List<DataModel> listModel) {
        this.ctx = ctx;
        this.listModel = listModel;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent,false);

        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModel dm = listModel.get(position);

        holder.tv_id.setText(String.valueOf(dm.getId()));
        holder.tvNama.setText(dm.getNama());
        holder.tvAlamat.setText(dm.getAlamat());
        holder.tvTelepon.setText(dm.getTelepon());

    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView tv_id, tvNama, tvAlamat, tvTelepon;


        public HolderData(@NonNull View itemView) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_id);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvTelepon = itemView.findViewById(R.id.tv_telepon);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder dialogpesan = new AlertDialog.Builder(ctx);
                    dialogpesan.setMessage("Pilih Salah Satu Operasi");
                    dialogpesan.setTitle("Perhatian");
                    dialogpesan.setIcon(R.mipmap.ic_launcher_round);
                    dialogpesan.setCancelable(true);

                    idLaundry = Integer.parseInt(tv_id.getText().toString());

                    dialogpesan.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteData();
                            dialogInterface.dismiss();
                            Handler handler = new Handler();

                            ((WisataActivity) ctx ).retrieveData();

                          /*  handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            } ,1000);
                            */
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

        private void deleteData(){
            APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponseModel> hapusData = ardData.ardDeleteData(idLaundry);

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

        private void getData(){
            APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponseModel> tampilData = ardData.ardGetData(idLaundry);

            tampilData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    int kode = response.body().getKode();
                    String pesan = response.body().getPesan();
                    listLaundry = response.body().getData();

                    int varIdLaundry = listLaundry.get(0).getId();
                    String varNamaLaundry = listLaundry.get(0).getNama();
                    String varAlamatLaundy = listLaundry.get(0).getAlamat();
                    String varTeleponLaundry = listLaundry.get(0).getTelepon();

                   // Toast.makeText(ctx, "kode : " + kode + " | Pesan : " + pesan + " | IdLaundry : " + varIdLaundry + " | Nama : " + varNamaLaundry + " | Alamat : " + varAlamatLaundy + " | Telepon : " + varTeleponLaundry, Toast.LENGTH_SHORT).show();

                    Intent kirim = new Intent(ctx, UbahActivity.class);
                    kirim.putExtra("xId", varIdLaundry);
                    kirim.putExtra("xNama", varNamaLaundry);
                    kirim.putExtra("xAlamat", varAlamatLaundy);
                    kirim.putExtra("xTelepon", varTeleponLaundry);
                    ctx.startActivity(kirim);
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Toast.makeText(ctx, "Gagal Menghubungi Server : " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}

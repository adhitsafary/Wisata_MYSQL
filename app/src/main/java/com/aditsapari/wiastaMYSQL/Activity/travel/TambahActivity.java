package com.aditsapari.wiastaMYSQL.Activity.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aditsapari.wiastaMYSQL.API.APIRequestData;
import com.aditsapari.wiastaMYSQL.API.RetroServer;
import com.aditsapari.wiastaMYSQL.Model.ResponseModel;
import com.aditsapari.wiastaMYSQL.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahActivity extends AppCompatActivity {
    EditText ed_nama, ed_alamat, ed_telepon;
    Button btn_tambah;
    private String nama, alamat, telepon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);
        ed_nama = findViewById(R.id.et_Nama);
        ed_alamat = findViewById(R.id.et_Alamat);
        ed_telepon = findViewById(R.id.et_Telepon);
        btn_tambah = findViewById(R.id.btn_Simpan);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama = ed_nama.getText().toString();
                alamat = ed_alamat.getText().toString();
                telepon = ed_telepon.getText().toString();

                if (nama.trim().equals("")){
                    ed_nama.setError("Nama Harus diisi");
                }
                else if (alamat.trim().equals("")){
                    ed_alamat.setError("Alamat Harus diisi");
                }
                else if (telepon.trim().equals("")){
                    ed_telepon.setError("Telepon harus diisi");
                }
                else {
                    createData();
                }
            }
        });
    }

    private void createData(){
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponseModel> simpanData = ardData.ardCreateData(nama, alamat, telepon);

        simpanData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int kode = response.body().getKode();
                String pesan = response.body().getPesan();

                Toast.makeText(TambahActivity.this, "Kode : "+kode+" | pesan : "+pesan, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(TambahActivity.this, "Gagal : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
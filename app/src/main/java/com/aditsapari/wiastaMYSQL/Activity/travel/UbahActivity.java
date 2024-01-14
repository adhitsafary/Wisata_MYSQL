package com.aditsapari.wiastaMYSQL.Activity.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class UbahActivity extends AppCompatActivity {
    int xid;
    String xnama, xalamat, xtelepon;
    EditText ed_nama, ed_alamat, ed_telepon;
    Button btn_ubah;
    private String yNama, yAlamat, yTelepon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah);

        Intent terima = getIntent();
        xid = terima.getIntExtra("xId", -1);
        xnama = terima.getStringExtra("xNama");
        xalamat = terima.getStringExtra("xAlamat");
        xtelepon = terima.getStringExtra("xTelepon");

        ed_nama = findViewById(R.id.et_Nama);
        ed_alamat = findViewById(R.id.et_Alamat);
        ed_telepon = findViewById(R.id.et_Telepon);
        btn_ubah = findViewById(R.id.btn_Simpan);

        ed_nama.setText(xnama);
        ed_alamat.setText(xalamat);
        ed_telepon.setText(xtelepon);

        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yNama = ed_nama.getText().toString();
                yAlamat = ed_alamat.getText().toString();
                yTelepon = ed_telepon.getText().toString();

                updateData();

            }
        });

    }

    private void updateData() {
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponseModel> ubahData = ardData.ardUpdateData(xid, yNama, yAlamat, yTelepon);

        ubahData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int kode = response.body().getKode();
                String pesan = response.body().getPesan();

                Toast.makeText(UbahActivity.this, "Kode : "+kode+" | pesan : "+pesan, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(UbahActivity.this, "Gagal : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
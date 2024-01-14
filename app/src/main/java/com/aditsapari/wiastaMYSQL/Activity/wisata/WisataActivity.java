package com.aditsapari.wiastaMYSQL.Activity.wisata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aditsapari.wiastaMYSQL.API.APIRequestData;
import com.aditsapari.wiastaMYSQL.API.RetroServer;
import com.aditsapari.wiastaMYSQL.Adapter.AdapterWisata;
import com.aditsapari.wiastaMYSQL.Model.DataModel;
import com.aditsapari.wiastaMYSQL.Model.ResponseModel;
import com.aditsapari.wiastaMYSQL.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WisataActivity extends AppCompatActivity {

    private RecyclerView rvData;
    private androidx.recyclerview.widget.RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModel> listData = new ArrayList<>();
    private SwipeRefreshLayout swl_data;
    private ProgressBar pb_data;
    FloatingActionButton fab_tambah;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swl_data = findViewById(R.id.swl_data);
        pb_data = findViewById(R.id.pb_data);
        rvData = findViewById(R.id.rv_data);
        fab_tambah = findViewById(R.id.fab_tambah_data);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
       // lmData = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(gridLayoutManager);

        // retrieveData();

        fab_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WisataActivity.this, TambahWisataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveData();
    }

    public void retrieveData() {
        pb_data.setVisibility(View.VISIBLE);

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponseModel> call = apiRequestData.ardRetrieveDatawisata();

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pb_data.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel responseModel = response.body();

                    if (responseModel.getKode() == 1) {
                        listData = responseModel.getData();
                        adData = new AdapterWisata(WisataActivity.this, listData);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                    } else {
                        Toast.makeText(WisataActivity.this, "Gagal mendapatkan data: " + responseModel.getPesan(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleErrorResponse(response);
                }

                swl_data.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pb_data.setVisibility(View.GONE);
                handleFailureResponse(t);

                swl_data.setRefreshing(false);
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        Toast.makeText(WisataActivity.this, "Response not successful. Code: " + response.code(), Toast.LENGTH_SHORT).show();
    }

    private void handleFailureResponse(Throwable t) {
        Toast.makeText(WisataActivity.this, "Gagal menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        t.printStackTrace();
    }

}
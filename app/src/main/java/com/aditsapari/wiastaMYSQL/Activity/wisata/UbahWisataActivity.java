package com.aditsapari.wiastaMYSQL.Activity.wisata;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.aditsapari.wiastaMYSQL.API.APIRequestData;
import com.aditsapari.wiastaMYSQL.API.RetroServer;
import com.aditsapari.wiastaMYSQL.Model.ResponseModel;
import com.aditsapari.wiastaMYSQL.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahWisataActivity extends AppCompatActivity {
    int xid;
    String xnama, xdeskripsi, xalamat, xharga, ximage;
    EditText ed_nama_wisata, ed_deskripsi_wisata, ed_alamat_wisata, harga_tiket;
    Button btn_ubah, btn_pilih_image;
    private String yNama, yDeskripsi, yAlamat, yHarga;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_wisata);

        Intent terima = getIntent();
        xid = terima.getIntExtra("xId", -1);
        xnama = terima.getStringExtra("xNama");
        xdeskripsi = terima.getStringExtra("xDeskripsi");
        xalamat = terima.getStringExtra("xAlamat");
        xharga = terima.getStringExtra("xhargaTiket");
        ximage = terima.getStringExtra("xImage");

        ed_nama_wisata = findViewById(R.id.et_Nama);
        ed_deskripsi_wisata = findViewById(R.id.et_deskripsiWisata);
        ed_alamat_wisata = findViewById(R.id.et_Alamat);
        harga_tiket = findViewById(R.id.harga_tiket);
        btn_ubah = findViewById(R.id.btn_Simpan);
        btn_pilih_image = findViewById(R.id.btn_PilihGambar);

        ed_nama_wisata.setText(xnama);
        ed_deskripsi_wisata.setText(xdeskripsi);
        ed_alamat_wisata.setText(xalamat);
        harga_tiket.setText(xharga);

        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yNama = ed_nama_wisata.getText().toString();
                yDeskripsi = ed_deskripsi_wisata.getText().toString();
                yAlamat = ed_alamat_wisata.getText().toString();
                yHarga = harga_tiket.getText().toString();

                updateData();
            }
        });

        btn_pilih_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;

        try {
            // Coba metode pertama
            filePath = getPathFromInputStreamUri(uri);

            // Jika metode pertama gagal, coba metode kedua
            if (filePath == null || filePath.isEmpty()) {
                filePath = getPathFromCursorLoader(uri);
            }
        } catch (Exception e) {
            Log.e("ImagePath", "Exception getting path from URI: " + e.getMessage());
        }

        return filePath;
    }

    private String getPathFromCursorLoader(Uri uri) {
        String filePath = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e("ImagePath", "Exception getting path from CursorLoader: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return filePath;
    }

    private String getPathFromInputStreamUri(Uri uri) {
        String filePath = "";
        InputStream inputStream = null;

        try {
            inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                File tempFile = createTemporalFileFrom(inputStream);
                filePath = tempFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e("ImagePath", "Exception getting path from InputStream: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e("ImagePath", "Exception closing InputStream: " + e.getMessage());
            }
        }

        return filePath;
    }

    private File createTemporalFileFrom(InputStream inputStream) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = createTemporalFile();
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e("ImagePath", "Exception closing OutputStream: " + e.getMessage());
            }
        }
        return targetFile;
    }

    private File createTemporalFile() {
        String fileName = "temp_file";
        File file = new File(getExternalCacheDir(), fileName);
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Tampilkan nama file gambar yang dipilih (opsional)
            File file = new File(imageUri.getPath());
            Log.d("ImagePath", file.getName());
        }
    }

    private void updateData() {
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);

        // Buat bagian dari request untuk mengunggah gambar
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String filePath = getRealPathFromURI(imageUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        Call<ResponseModel> ubahData = ardData.ardUpdateDataWisata(xid, yNama, yDeskripsi, yAlamat, yHarga, imagePart);

        ubahData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();

                    if (responseModel != null) {
                        int kode = responseModel.getKode();
                        String pesan = responseModel.getPesan();

                        Toast.makeText(UbahWisataActivity.this, "Kode : " + kode + " | pesan : " + pesan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e("API_RESPONSE", "Response Model is null");
                    }
                } else {
                    Log.e("API_RESPONSE", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("API_RESPONSE", "Failed: " + t.getMessage());
                Toast.makeText(UbahWisataActivity.this, "Gagal : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });



    }
}

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

public class TambahWisataActivity extends AppCompatActivity {
    EditText ed_nama, ed_alamat, ed_deskripsi, ed_harga;
    Button btn_tambah, btn_pilih_image;
    private String nama, alamat, deskripsi, harga;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_wisata);

        ed_nama = findViewById(R.id.et_Nama);
        ed_alamat = findViewById(R.id.et_Alamat);
        ed_deskripsi = findViewById(R.id.et_deskripsiWisata);
        ed_harga = findViewById(R.id.harga_tiket);
        btn_tambah = findViewById(R.id.btn_Simpan);
        btn_pilih_image = findViewById(R.id.btn_PilihGambar);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama = ed_nama.getText().toString();
                alamat = ed_alamat.getText().toString();
                deskripsi = ed_deskripsi.getText().toString();
                harga = ed_harga.getText().toString();

                if (nama.trim().equals("")) {
                    ed_nama.setError("Nama Harus diisi");
                } else if (alamat.trim().equals("")) {
                    ed_alamat.setError("Alamat Harus diisi");
                } else if (deskripsi.trim().equals("")) {
                    ed_deskripsi.setError("Deskripsi harus diisi");
                } else if (harga.trim().equals("")) {
                    ed_harga.setError("Harga harus diisi");
                } else {
                    createData();
                }
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Tampilkan nama file gambar yang dipilih (opsional)
            File file = new File(imageUri.getPath());
            Log.d("ImagePath", file.getName());
        }
    }

    private void createData() {
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);

        // Buat bagian dari request untuk mengunggah gambar
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String filePath = getRealPathFromURI(imageUri);
            if (filePath != null && !filePath.isEmpty()) {  // Tambahkan pengecekan apakah filePath tidak kosong
                File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            } else {
                // Tindakan penanganan jika filePath kosong atau null
                Log.e("ImagePath", "File path is empty or null");
                return;
            }
        } else {
            // Tindakan penanganan jika imageUri bernilai null
            Log.e("ImagePath", "imageUri is null");
            return;
        }

        Call<ResponseModel> simpanData = ardData.ardCreateDataWisata(nama, deskripsi, alamat, harga, imagePart);

        simpanData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int kode = response.body().getKode();
                String pesan = response.body().getPesan();

                Toast.makeText(TambahWisataActivity.this, "Kode : " + kode + " | pesan : " + pesan, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(TambahWisataActivity.this, "Gagal : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
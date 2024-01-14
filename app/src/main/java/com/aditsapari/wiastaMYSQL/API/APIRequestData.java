package com.aditsapari.wiastaMYSQL.API;

import com.aditsapari.wiastaMYSQL.Model.ResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIRequestData {

    //wisata

    @GET("crud/lihatwisata.php")
    Call<ResponseModel> ardLihatWisata();

    @FormUrlEncoded
    @POST("crud/get.php")
    Call<ResponseModel> ardGetWisata(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("crud/uploadwisata.php")
    Call<ResponseModel> ardUploadteWisata(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("harga") String harga
    );

    @FormUrlEncoded
    @POST("crud/hapuswisata.php")
    Call<ResponseModel> ardHapusWisata(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("crud/updatewisata.php")
    Call<ResponseModel> ardUpdateWisata(
            @Field("id") int id,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("harga") String harga
    );

    //Travell


    @GET("travel/retrieve.php")
    Call<ResponseModel> ardRetrieveData();

    @FormUrlEncoded
    @POST("travel/create.php")
    Call<ResponseModel> ardCreateData(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telepon") String telepon
    );

    @FormUrlEncoded
    @POST("travel/delete.php")
    Call<ResponseModel> ardDeleteData(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("travel/get.php")
    Call<ResponseModel> ardGetData(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("travel/update.php")
    Call<ResponseModel> ardUpdateData(
            @Field("id") int id,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telepon") String telepon
    );

    //Budaya

    @GET("wisata/retrieve.php")
    Call<ResponseModel> ardRetrieveDatawisata();

    @Multipart
    @POST("wisata/create.php")
    Call<ResponseModel> ardCreateDataWisata(
            @Part("nama_wisata") String nama_wisata,
            @Part("deskripsi_wisata") String deskripsi_wisata,
            @Part("alamat_wisata") String alamat_wisata,
            @Part("harga_tiket") String harga_tiket,
            @Part MultipartBody.Part image
    );


    @FormUrlEncoded
    @POST("wisata/delete.php")
    Call<ResponseModel> ardDeleteDatawisata(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("wisata/get.php")
    Call<ResponseModel> ardGetDatawisata(
            @Field("id") int id
    );

    @Multipart
    @POST("wisata/update.php")
    Call<ResponseModel> ardUpdateDataWisata(
            @Part("id_wisata") int idWisata,
            @Part("nama_wisata") String namaWisata,
            @Part("deskripsi_wisata") String deskripsiWisata,
            @Part("alamat_wisata") String alamatWisata,
            @Part("harga_tiket") String hargaTiket,
            @Part MultipartBody.Part image
    );


}

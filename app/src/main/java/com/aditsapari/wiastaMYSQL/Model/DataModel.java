package com.aditsapari.wiastaMYSQL.Model;

public class DataModel {
    private int id;
    private String nama, alamat, telepon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }


    // data wisata

    private String nama_user;
    private String alamat_user;
    private String harga_user;


    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getAlamat_user() {
        return alamat_user;
    }

    public void setAlamat_user(String alamat_user) {
        this.alamat_user = alamat_user;
    }

    public String getHarga_user() {
        return harga_user;
    }

    public void setHarga_user(String harga_user) {
        this.harga_user = harga_user;
    }


    //budaya
    public String nama_wisata;
    public String deskripsi_wisata;
    public String alamat_wisata;
    public String harga_tiket;
    private String image;


    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getDeskripsi_wisata() {
        return deskripsi_wisata;
    }

    public void setDeskripsi_wisata(String deskripsi_wisata) {
        this.deskripsi_wisata = deskripsi_wisata;
    }

    public String getAlamat_wisata() {
        return alamat_wisata;
    }

    public void setAlamat_wisata(String alamat_wisata) {
        this.alamat_wisata = alamat_wisata;
    }

    public String getHarga_tiket() {
        return harga_tiket;
    }

    public void setHarga_tiket(String harga_tiket) {
        this.harga_tiket = harga_tiket;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}

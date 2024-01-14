package com.aditsapari.wiastaMYSQL.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroServer {
    //private static final String baseURL = "https://apiwisatasukabumi.000webhostapp.com/"; modiflagi sesuai dengan
    //link maneh
    //http://172.17.100.2/native/laundry/ ini untuk NOX
    //http://10.0.2.2/native/laundry/ ini untuk emu android studio
    //http://192.168.0.25/native/laundry/ ini untuk device asli ( cek ip config dulu soalnya pasti beda)
    private static final String baseURL = "https://apiwisatasukabumi.000webhostapp.com/";
    public static Retrofit retro;


    public static Retrofit konekRetrofit(){
        if(retro == null){
            retro = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retro;
    }
}

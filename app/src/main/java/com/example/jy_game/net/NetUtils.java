package com.example.jy_game.net;

import android.util.Log;

import com.example.jy_game.TranslationBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtils {

    private static final String TAG = "NetUtils";
    public static void  getNetData(String name, final CallBack callBack){

        Log.d(TAG, "getNetData: "+name);
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiService.baseUrl).build();
        final ApiService apiService = retrofit.create(ApiService.class);
        apiService.getZhBean(name).enqueue(new Callback<TranslationBean>() {
            @Override
            public void onResponse(Call<TranslationBean> call, Response<TranslationBean> response) {
                final TranslationBean.ContentBean content = response.body().getContent();
                final List<String> word_mean = content.getWord_mean();
                for (int i = 0; i < word_mean.size(); i++) {
                    String name = word_mean.get(i);
                    Log.d(TAG, "onResponse: 翻译内容="+name);
                    if (name.startsWith("n. ")){

//                        "n. 天空;空气;空中;神态;曲调;",
                        String[] n = name.split(";");
                        if (n.length>0){
                            String first = n[0].substring(3);
                            if (first.contains("<美>")){
                                first= first.replace("<美>","");
                            }
                            if (first.contains(",")){
                                first = first.split("，|,")[0];
                            }
                            callBack.updateSuccess(first);
//                            mName.setText(first);
//                            talkSound(first);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<TranslationBean> call, Throwable t) {

                callBack.updateFailed(t.getMessage());
            }
        });

    }

}
package com.example.cookai;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAIService {

    @POST("v1/chat/completions")
    Call<Map<String, Object>> generateRecipe(
            @Header("Authorization") String authHeader,
            @Body Map<String, Object> body
    );
}
package com.example.expensetracker;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("expenses")
    Call<List<Expense>> getExpenses(
            @Query("page") int page,
            @Query("limit") int limit
    );
    @GET("expenses/{id}")
    Call<Expense> getExpenseById(@Path("id") String id);

    @POST("expenses")
    Call<Expense> createExpense(@Body Expense expense);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") String id);
}
package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailExpenseActivity extends AppCompatActivity {

    private TextView tvAmount, tvCurrency, tvDate, tvCategory, tvRemark;
    private ProgressBar progressBar;

    private static final String TAG = "DetailExpenseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Expense Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvAmount = findViewById(R.id.tv_detail_amount);
        tvCurrency = findViewById(R.id.tv_detail_currency);
        tvDate = findViewById(R.id.tv_detail_date);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvRemark = findViewById(R.id.tv_detail_remark);
        progressBar = findViewById(R.id.progressBar);

        String expenseId = getIntent().getStringExtra("EXPENSE_ID");

        if (expenseId != null) {
            loadExpenseDetails(expenseId);
        } else {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadExpenseDetails(String expenseId) {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService();
        Call<Expense> call = apiService.getExpenseById(expenseId);

        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayExpenseDetails(response.body());
                } else {
                    Toast.makeText(DetailExpenseActivity.this,
                            "Failed to load expense details", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailExpenseActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void displayExpenseDetails(Expense expense) {
        tvAmount.setText(String.format("$%.2f", expense.getAmount()));
        tvCurrency.setText(expense.getCurrency());
        tvCategory.setText(expense.getCategory());
        tvRemark.setText(expense.getRemark());

        if (expense.getCreatedDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(expense.getCreatedDate()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
package com.example.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount, etRemark;
    private Spinner spinnerCurrency, spinnerCategory;
    private Button btnAddExpense;
    private ProgressBar progressBar;

    private static final String TAG = "AddExpenseFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etAmount = view.findViewById(R.id.etAmount);
        etRemark = view.findViewById(R.id.etRemark);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        progressBar = view.findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.currencies,
                android.R.layout.simple_spinner_item
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        btnAddExpense.setOnClickListener(v -> addExpense());

        return view;
    }

    private void addExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String currency = spinnerCurrency.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String remark = etRemark.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            etAmount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(remark)) {
            etRemark.setError("Remark is required");
            etRemark.requestFocus();
            return;
        }

        String createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String expenseId = UUID.randomUUID().toString();
        Date createdDate = new Date();

        Expense expense = new Expense(
                expenseId,
                amount,
                currency,
                createdDate,
                category,
                remark,
                createdBy
        );

        progressBar.setVisibility(View.VISIBLE);
        btnAddExpense.setEnabled(false);

        ApiService apiService = RetrofitClient.getApiService();
        Call<Expense> call = apiService.createExpense(expense);

        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                progressBar.setVisibility(View.GONE);
                btnAddExpense.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Expense added successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Expense created: " + response.body().getId());

                    clearForm();

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchToExpenseList();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to add expense: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnAddExpense.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void clearForm() {
        etAmount.setText("");
        etRemark.setText("");
        spinnerCurrency.setSelection(0);
        spinnerCategory.setSelection(0);
    }
}
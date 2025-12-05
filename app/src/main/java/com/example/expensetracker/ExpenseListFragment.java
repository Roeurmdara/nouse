package com.example.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private ExpenseAdapter adapter;
    private LinearLayoutManager layoutManager;

    private List<Expense> expenseList = new ArrayList<>();

    private boolean isLastPage = false;
    private boolean isLoading = false;

    private int currentPage = 1;
    private static final int PAGE_START = 1;
    private static final int PAGE_LIMIT = 10;

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_expenses);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        apiService = RetrofitClient.getApiService();

        adapter = new ExpenseAdapter(getContext(), expenseList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Pagination scroll listener
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItems() {
                if (!isLoading && !isLastPage) {
                    isLoading = true;
                    currentPage++;
                    loadNextPage();
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // Swipe-to-delete setup
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expense = adapter.getItem(position);
                showDeleteConfirmation(expense, position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Load first page
        loadFirstPage();

        return view;
    }

    // -----------------------------
    // 🔥 Load first page
    // -----------------------------
    private void loadFirstPage() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        isLoading = true;
        currentPage = PAGE_START;
        isLastPage = false;

        apiService.getExpenses(currentPage, PAGE_LIMIT).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenses = response.body();
                    expenseList.clear();
                    expenseList.addAll(expenses);
                    adapter.notifyDataSetChanged();

                    if (expenses.size() < PAGE_LIMIT) {
                        isLastPage = true;
                    }

                    if (expenseList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -----------------------------
    // 🔥 Load next page
    // -----------------------------
    private void loadNextPage() {
        adapter.addLoadingFooter();

        apiService.getExpenses(currentPage, PAGE_LIMIT).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenses = response.body();
                    adapter.addAll(expenses);

                    if (expenses.size() < PAGE_LIMIT) {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                adapter.removeLoadingFooter();
                isLoading = false;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -----------------------------
    // 🔄 Refresh list
    // -----------------------------
    public void refreshList() {
        currentPage = PAGE_START;
        isLastPage = false;
        expenseList.clear();
        adapter.notifyDataSetChanged();
        loadFirstPage();
    }

    // -----------------------------
    // ❌ Delete expense
    // -----------------------------
    private void showDeleteConfirmation(Expense expense, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> deleteExpense(expense, position))
                .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                .setOnCancelListener(dialog -> adapter.notifyItemChanged(position))
                .show();
    }

    private void deleteExpense(Expense expense, int position) {
        apiService.deleteExpense(expense.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeItem(position);
                    Toast.makeText(getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                    if (adapter.getItemCount() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            }
        });
    }
}

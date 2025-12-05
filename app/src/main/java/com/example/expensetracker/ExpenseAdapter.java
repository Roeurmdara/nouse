package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Expense> expenseList;
    private Context context;
    private boolean isLoadingAdded = false;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpenseViewHolder) {
            Expense expense = expenseList.get(position);
            ExpenseViewHolder expenseHolder = (ExpenseViewHolder) holder;

            expenseHolder.tvAmount.setText(String.format("$%.2f", expense.getAmount()));
            expenseHolder.tvCurrency.setText(expense.getCurrency());
            expenseHolder.tvCategory.setText(expense.getCategory());
            expenseHolder.tvRemark.setText(expense.getRemark());

            if (expense.getCreatedDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                expenseHolder.tvDate.setText(sdf.format(expense.getCreatedDate()));
            }

            expenseHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailExpenseActivity.class);
                intent.putExtra("EXPENSE_ID", expense.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return expenseList == null ? 0 : expenseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == expenseList.size() - 1 && isLoadingAdded) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void addLoadingFooter() {
        isLoadingAdded = true;
        expenseList.add(new Expense(null, 0, "", null, "", "", ""));
        notifyItemInserted(expenseList.size() - 1);
    }
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = expenseList.size() - 1;
        Expense item = expenseList.get(position);

        if (item != null) {
            expenseList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void addAll(List<Expense> expenses) {
        int startPosition = expenseList.size();
        expenseList.addAll(expenses);
        notifyItemRangeInserted(startPosition, expenses.size());
    }
    public void removeItem(int position) {
        expenseList.remove(position);
        notifyItemRemoved(position);
    }
    public Expense getItem(int position) {
        return expenseList.get(position);
    }
    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCurrency, tvCategory, tvDate, tvRemark;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvCurrency = itemView.findViewById(R.id.tv_currency);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRemark = itemView.findViewById(R.id.tv_remark);
        }
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
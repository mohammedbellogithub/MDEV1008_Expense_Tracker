package com.example.mdev1008_expense_tracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mdev1008_expense_tracker.databinding.FragmentTransactionDetailsBinding;
import com.google.android.gms.tasks.Task;

public class TransactionDetailsFragment extends Fragment {

    private FragmentTransactionDetailsBinding binding;
    private String category, note, type, date, id;
    private double amount;
    private DatePicker datePicker;
    private EditText amountEditText, noteEditText, categoryEditText;
    //private RadioGroup radioGroup;
    private RadioButton expenseRadioButton, incomeRadioButton;
    ProgressBar progressBar;

    public TransactionDetailsFragment() {
    }
    public static TransactionDetailsFragment newInstance(TransactionModel transaction) {
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        Bundle args = new Bundle();
        args.putString("id",transaction.getId());
        args.putDouble("amount", transaction.getAmount());
        args.putString("category", transaction.getCategory());
        args.putString("note", transaction.getNote());
        args.putString("type", transaction.getType());
        args.putString("date", transaction.getDate());
        args.putString("type", transaction.getType());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = getArguments().getDouble("amount");
            category = getArguments().getString("category");
            note = getArguments().getString("note");
            type = getArguments().getString("type");
            date = getArguments().getString("date");
            id = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            bindTransactionToView(id, amount, category, note, type, date);
        }
        amountEditText = view.findViewById(R.id.amount);
        noteEditText = view.findViewById(R.id.note);
        categoryEditText = view.findViewById(R.id.category);
        datePicker = view.findViewById(R.id.date_picker);
        //radioGroup = view.findViewById(R.id.add_transaction_radioGroup);
        incomeRadioButton = view.findViewById(R.id.income_radio_button);
        expenseRadioButton = view.findViewById(R.id.expense_radio_button);
        Button saveButton = view.findViewById(R.id.update);
        progressBar = view.findViewById(R.id.progressBar);
        saveButton.setOnClickListener(v -> updateTransaction());

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> navigateBack());
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void bindTransactionToView(String id, double amount, String category, String note, String type, String date) {
        binding.amount.setText(String.valueOf(amount));
        binding.category.setText(category);
        binding.note.setText(note);
        if (Objects.equals(type, "expense")){
            binding.expenseRadioButton.setChecked(true);
        }
        else{
            binding.incomeRadioButton.setChecked(true);
        }

        // Initialize Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Parse the date string and set year, month, and day
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
            return; // Stop execution if parsing fails
        }

        // Set the parsed date on the DatePicker
        binding.datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null
        );
    }

    @Nullable
    private TransactionModel validateForm() {
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(getActivity(), "Amount is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        double amount = Double.parseDouble(amountText);
        String note = noteEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth()));

        String type;
        if (expenseRadioButton.isChecked()) {
            type = "expense";
        } else if (incomeRadioButton.isChecked()) {
            type = "income";
        } else {
            Toast.makeText(getActivity(), "Please select Income or Expense", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new TransactionModel(id, amount, note, category, date, type);
    }
    private void updateTransaction() {
        progressBar.setVisibility(View.VISIBLE);
        TransactionModel model = validateForm();
        if (model != null) {
            Task<Boolean> saveTask = model.updateAsync(getActivity());
            if (saveTask != null) {
                saveTask.addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult()) {
                        navigateBack();
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }


    }

    private void navigateBack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Pop the back stack
    }
}
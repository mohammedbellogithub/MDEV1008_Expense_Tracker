package com.example.mdev1008_expense_tracker;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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


import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddFragment extends Fragment {

    private DatePicker datePicker;
    private EditText amountEditText, noteEditText, categoryEditText;
    private RadioGroup radioGroup;
    private RadioButton expenseRadioButton, incomeRadioButton;
    ProgressBar progressBar;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);


        amountEditText = view.findViewById(R.id.amount);
        noteEditText = view.findViewById(R.id.note);
        categoryEditText = view.findViewById(R.id.category);
        datePicker = view.findViewById(R.id.date_picker);
        radioGroup = view.findViewById(R.id.add_transaction_radioGroup);
        incomeRadioButton = view.findViewById(R.id.income_radio_button);
        expenseRadioButton = view.findViewById(R.id.expense_radio_button);
        Button saveButton = view.findViewById(R.id.save);
        progressBar = view.findViewById(R.id.progressBar);
        saveButton.setOnClickListener(v -> AddTransaction());

        Button clearButton = view.findViewById(R.id.clear);
        clearButton.setOnClickListener(v -> clearForm());
        return view;
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

        return new TransactionModel(amount, note, category, date, type);
    }

    private void clearForm() {
        amountEditText.setText("");
        noteEditText.setText("");
        categoryEditText.setText("");
        radioGroup.clearCheck();
        datePicker.updateDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }
    private void AddTransaction() {
        progressBar.setVisibility(View.VISIBLE);
        TransactionModel model = validateForm();
        if (model != null) {
            Task<Boolean> saveTask = model.addAsync(getActivity());
            if (saveTask != null) {
                saveTask.addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult()) {
                        clearForm();
                    }
                });
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
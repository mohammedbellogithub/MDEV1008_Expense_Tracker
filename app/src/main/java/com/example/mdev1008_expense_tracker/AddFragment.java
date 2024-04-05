package com.example.mdev1008_expense_tracker;

// Import statements
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// AddFragment class
public class AddFragment extends Fragment {

    // Member variables
    private DatePicker datePicker;
    private EditText amountEditText, noteEditText, categoryEditText;
    private RadioGroup radioGroup;
    private RadioButton expenseRadioButton, incomeRadioButton;
    ProgressBar progressBar;

    // Default constructor
    public AddFragment() {
        // Required empty public constructor
    }

    // onCreate method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize views
        amountEditText = view.findViewById(R.id.amount);
        noteEditText = view.findViewById(R.id.note);
        categoryEditText = view.findViewById(R.id.category);
        datePicker = view.findViewById(R.id.date_picker);
        radioGroup = view.findViewById(R.id.add_transaction_radioGroup);
        incomeRadioButton = view.findViewById(R.id.income_radio_button);
        expenseRadioButton = view.findViewById(R.id.expense_radio_button);
        Button saveButton = view.findViewById(R.id.save);
        Button clearButton = view.findViewById(R.id.clear);
        progressBar = view.findViewById(R.id.progressBar);

        // Set click listener for save button
        saveButton.setOnClickListener(v -> AddTransaction());

        // Set click listener for clear button
        clearButton.setOnClickListener(v -> clearForm());
        return view;
    }

    // Method to validate form input and create TransactionModel object
    @Nullable
    private TransactionModel validateForm() {
        // Retrieve form inputs
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            // Show error if amount is empty
            Toast.makeText(getActivity(), "Amount is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Parse amount
        double amount = Double.parseDouble(amountText);
        String note = noteEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth()));

        // Determine transaction type
        String type;
        if (expenseRadioButton.isChecked()) {
            type = "expense";
        } else if (incomeRadioButton.isChecked()) {
            type = "income";
        } else {
            // Show error if type is not selected
            Toast.makeText(getActivity(), "Please select Income or Expense", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Create and return TransactionModel object
        return new TransactionModel(amount, note, category, date, type);
    }

    // Method to clear form inputs
    private void clearForm() {
        amountEditText.setText("");
        noteEditText.setText("");
        categoryEditText.setText("");
        radioGroup.clearCheck();
        datePicker.updateDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    // Method to add transaction
    private void AddTransaction() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Validate form and create TransactionModel object
        TransactionModel model = validateForm();
        if (model != null) {
            // Add transaction asynchronously
            Task<Boolean> saveTask = model.addAsync(getActivity());
            if (saveTask != null) {
                saveTask.addOnCompleteListener(task -> {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult()) {
                        // Clear form if transaction added successfully
                        clearForm();
                    }
                });
            }
        } else {
            // Hide progress bar if form validation failed
            progressBar.setVisibility(View.GONE);
        }
    }
}
package com.example.mdev1008_expense_tracker;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FireBaseHelper {
    private static FireBaseHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FireBaseHelper() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static FireBaseHelper getInstance() {
        if (instance == null) {
            instance = new FireBaseHelper();
        }
        return instance;
    }

    public String getLoggedInUser() {
        return auth.getUid();
    }
    public String getLoggedInUserEmail() {
        return Objects.requireNonNull(auth.getCurrentUser()).getEmail();
    }
    public String getUserEmailWithoutDomain() {
        String userEmail = getLoggedInUserEmail();
        if (userEmail != null && userEmail.contains("@")) {
            userEmail = userEmail.substring(0, userEmail.indexOf("@"));
        } else {
            userEmail = "JOHN DOE";
        }
        return userEmail;
    }

    public Task<Boolean> addAsync(String collectionPath, TransactionModel model, Context context) {
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        db.collection(collectionPath)
                .document(model.getId())
                .set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,  collectionPath + " added successfully!", Toast.LENGTH_SHORT).show();
                        tcs.setResult(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,  "Failed to add " + collectionPath +". Please try again.", Toast.LENGTH_SHORT).show();
                        tcs.setResult(false);
                    }
                });

        return tcs.getTask();
    }


    public Task<List<TransactionModel>> getAllAsync(String collectionPath) {
        TaskCompletionSource<List<TransactionModel>> tcs = new TaskCompletionSource<>();

        db.collection(collectionPath)
                .whereEqualTo("userId", this.getLoggedInUser())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TransactionModel> transactions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TransactionModel transaction = document.toObject(TransactionModel.class);
                        transactions.add(transaction);
                    }
                    tcs.setResult(transactions);
                })
                .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching transactions: " + e.getMessage());
                tcs.setException(e);
        });
        return tcs.getTask();
    }

    public Task<List<TransactionModel>> getByParamAsync(String collectionPath, String param, Object value) {
        TaskCompletionSource<List<TransactionModel>> tcs = new TaskCompletionSource<>();

        db.collection(collectionPath)
                .whereEqualTo("userId", this.getLoggedInUser())
                .whereEqualTo(param, value)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TransactionModel> transactions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TransactionModel transaction = document.toObject(TransactionModel.class);
                        transactions.add(transaction);
                    }
                    tcs.setResult(transactions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching transactions: " + e.getMessage());
                    tcs.setException(e);
                });
        return tcs.getTask();
    }

    public Task<Boolean> updateAsync(String collectionPath, String entityId, Map<String, Object> updates, Context context) {
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();
        db.collection(collectionPath)
                .document(entityId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,  collectionPath + " updated successfully!", Toast.LENGTH_SHORT).show();
                    tcs.setResult(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update " + collectionPath + ". Please try again.", Toast.LENGTH_SHORT).show();
                    tcs.setResult(false);
                });
        return tcs.getTask();
    }

}


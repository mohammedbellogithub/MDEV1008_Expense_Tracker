package com.example.mdev1008_expense_tracker;

// Import statements
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// FireBaseHelper class
public class FireBaseHelper {
    // Member variables
    private static FireBaseHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    // Private constructor
    private FireBaseHelper() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    //Get instance of FireBaseHelper
    public static FireBaseHelper getInstance() {
        if (instance == null) {
            instance = new FireBaseHelper();
        }
        return instance;
    }

    // Get ID of logged-in user
    public String getLoggedInUser() {
        return auth.getUid();
    }

    // Get email of logged-in user
    public String getLoggedInUserEmail() {
        return Objects.requireNonNull(auth.getCurrentUser()).getEmail();
    }

    // Get user email without domain
    public String getUserEmailWithoutDomain() {
        String userEmail = getLoggedInUserEmail();
        if (userEmail != null && userEmail.contains("@")) {
            userEmail = userEmail.substring(0, userEmail.indexOf("@"));
        } else {
            userEmail = "JOHN DOE";
        }
        return userEmail;
    }

    // Add transaction asynchronously
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


    // Get all transactions asynchronously
    public Task<List<TransactionModel>> getAllAsync(String collectionPath) {
        TaskCompletionSource<List<TransactionModel>> tcs = new TaskCompletionSource<>();

        db.collection(collectionPath)
                .whereEqualTo("userId", this.getLoggedInUser())
                .orderBy("date", Query.Direction.DESCENDING)
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

    // Get transactions by parameter asynchronously
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

    // Update transaction asynchronously
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

    // Delete document asynchronously
    public Task<Boolean> deleteAsync(String collectionPath, String entityId, Context context) {
        // Create a TaskCompletionSource to handle the asynchronous task
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        // Access FireStore collection and document, then delete the document
        db.collection(collectionPath)
                .document(entityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Document deletion successful
                    Toast.makeText(context, collectionPath + " deleted successfully!", Toast.LENGTH_SHORT).show();
                    tcs.setResult(true);
                })
                .addOnFailureListener(e -> {
                    // Document deletion failed
                    Toast.makeText(context, "Failed to delete " + collectionPath + ". Please try again.", Toast.LENGTH_SHORT).show();
                    tcs.setResult(false);
                });

        // Return the task
        return tcs.getTask();
    }
}


package com.example.mdev1008_expense_tracker;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionModel {
    private String id;
    private double amount;
    private String note;
    private String category;
    private String date;
    private String userId;
    private String type;

    public TransactionModel() {
    }

    public TransactionModel(double amount, String note, String category, String date, String type) {
        this.type = type;
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
        this.userId = getUserId();
    }
    public TransactionModel(String id, double amount, String note, String category, String date, String type) {
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
        this.userId = getUserId();
    }


    public Task<Boolean> addAsync(Context context){
        FireBaseHelper fireStoreHelper = FireBaseHelper.getInstance();
        return fireStoreHelper.addAsync("transactions", this, context);
    }

    public Task<Boolean> updateAsync(Context context){
        FireBaseHelper fireStoreHelper = FireBaseHelper.getInstance();
        Map<String, Object> updates = new HashMap<>();
        updates.put("id", this.id);
        updates.put("amount", this.amount);
        updates.put("category", this.category);
        updates.put("note", this.note);
        updates.put("type", this.type);
        updates.put("date", this.date);

        // Log the updates map values
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            Log.d("UPDATE_ASYNC", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        return fireStoreHelper.updateAsync("transactions", this.id, updates, context);
    }

    //Getters and setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return FireBaseHelper.getInstance().getLoggedInUser();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}



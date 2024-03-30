package com.example.mdev1008_expense_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mdev1008_expense_tracker.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    CalendarView calendarView;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchFragment(new HomeFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
                if(item.getItemId() == R.id.homeNavId)
                {
                    switchFragment(new HomeFragment());
                }
                else if (item.getItemId() == R.id.accountNavId)
                {
                    switchFragment(new AccountFragment());
                }
                else if (item.getItemId() == R.id.addNavId)
                {
                    switchFragment(new AddFragment());
                } else if (item.getItemId() == R.id.reportsNavId) {
                    switchFragment(new ReportsFragment());
                }
                else if (item.getItemId() == R.id.profileNavId)
                {
                    switchFragment(new ProfileFragment());
                }
                return true;
        });


    }

    public void switchFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navFragmentView, fragment);
        fragmentTransaction.commit();
    }

    private void setDate(int day, int month, int year){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long time = calendar.getTimeInMillis();
        calendarView.setDate(time);
    }

    private String getDate()
    {
        long dateTime = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(dateTime);
        return  simpleDateFormat.format(calendar.getTime());
    }
}
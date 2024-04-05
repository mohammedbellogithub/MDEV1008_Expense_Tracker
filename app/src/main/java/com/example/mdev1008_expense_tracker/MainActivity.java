package com.example.mdev1008_expense_tracker;

// Import statements
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.example.mdev1008_expense_tracker.databinding.ActivityMainBinding;

// MainActivity class
public class MainActivity extends AppCompatActivity {
    // Member variables
    ActivityMainBinding binding;

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple));

        // Set content view using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Switch to HomeFragment by default
        switchFragment(new HomeFragment());

        // Bottom navigation item selection listener
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

    // Method to switch between fragments
    public void switchFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navFragmentView, fragment);
        fragmentTransaction.commit();
    }
}
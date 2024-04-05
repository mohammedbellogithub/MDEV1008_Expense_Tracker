package com.example.mdev1008_expense_tracker;

//import statements
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//profile fragment class
public class ProfileFragment extends Fragment {
    //members
    FirebaseAuth auth;
    FirebaseUser user;

    // Static method to create a new instance of ProfileFragment
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // Default constructor
    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Find TextViews in the layout
        TextView emailText = view.findViewById(R.id.profile_email);
        TextView AccountText = view.findViewById(R.id.account_profile_text);
        TextView logoutText = view.findViewById(R.id.logout);


        // Check if user is logged in
        if (user == null)
        {
            // Redirect to LoginActivity if not logged in
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            // Set the email address of the logged-in user
            emailText.setText(user.getEmail());
        }

        // Logout button click listener
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                auth.signOut();

                // Redirect to LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // "AccountText" click listener to navigate to the AccountFragment
        AccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).switchFragment(new AccountFragment());
                BottomNavigationView b = requireActivity().findViewById(R.id.bottomNav);
                b.setSelectedItemId(R.id.accountNavId);
            }
        });
        return view;
    }
}
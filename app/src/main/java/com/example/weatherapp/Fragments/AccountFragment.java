package com.example.weatherapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment implements View.OnClickListener {
    private TextView userEmailTextView;
    private TextView emailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        userEmailTextView = rootView.findViewById(R.id.userEmail);
        emailTextView = rootView.findViewById(R.id.textView27);

        // Retrieve user data from Firebase and update TextViews
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            userEmailTextView.setText(userEmail);
            emailTextView.setText(userEmail);
        }

        // Find password layout and components
        ImageView passwordArrow = rootView.findViewById(R.id.imageChangePasswordArrow);
        LinearLayout passwordLayout = rootView.findViewById(R.id.passwordChange);

        passwordArrow.setOnClickListener(this);
        passwordLayout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // Display the change password dialog
        ChangePasswordDialog dialogFragment = new ChangePasswordDialog();
        dialogFragment.show(getParentFragmentManager(), "change_password_dialog");
    }
}

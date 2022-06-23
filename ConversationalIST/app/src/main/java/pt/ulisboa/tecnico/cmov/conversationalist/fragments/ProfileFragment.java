package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import java.io.*;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;

public class ProfileFragment extends Fragment {

    private UserAccount user;
    private View view;

    public ProfileFragment(){
        // require a empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        configureUserDisplay();
        configureLougoutButton();

        return view;
    }


    private void configureUserDisplay() {
        TextView usernameTV = view.findViewById(R.id.usernameTextView);
        usernameTV.setText(user.getUsername());
    }

    private void configureLougoutButton() {
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToLoginScreen();
            }
        });
    }
    private void returnToLoginScreen() {
        getActivity().finish();
    }
}
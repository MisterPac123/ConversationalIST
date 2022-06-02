package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.MainFragment;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    UserAccount user;
    Bundle bundle;

    MainFragment firstFragment = new MainFragment();
    ProfileFragment secondFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.person);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (UserAccount) getIntent().getSerializableExtra("user");
        }
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.mainScreenFragm, firstFragment);
        //fragmentTransaction.commit();
        bundle = new Bundle();
        bundle.putSerializable("user", user);
        firstFragment.setArguments(bundle);

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.person:
                firstFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenFragm, firstFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenFragm, secondFragment).commit();
                return true;
        }

        return false;
    }
}
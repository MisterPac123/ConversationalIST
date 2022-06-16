package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.MainFragment;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.ProfileFragment;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.SearchChatRoomsFragment;

public class MainActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    UserAccount user;
    Bundle bundle;

    MainFragment mainFragmentNav = new MainFragment();
    ProfileFragment profileFragmentNav = new ProfileFragment();
    SearchChatRoomsFragment searchFragmentNav = new SearchChatRoomsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

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
        mainFragmentNav.setArguments(bundle);
        searchFragmentNav.setArguments(bundle);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                mainFragmentNav.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenFragm, mainFragmentNav).commit();
                return true;

            case R.id.seachChat:
                searchFragmentNav.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenFragm, searchFragmentNav).commit();
                return true;

            case R.id.person:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainScreenFragm, profileFragmentNav).commit();
                return true;
        }

        return false;
    }
}
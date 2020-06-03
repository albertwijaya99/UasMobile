package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    ArrayList<User> suggestList;
    Adapter_Recyclerview searchAdapter;
    private Button btnSearch;
    private CardView cardSearch;
    private RecyclerView recyclerView;
    private SearchView searchBar;
    Session sharedpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mDatabase = FirebaseDatabase.getInstance().getReference("employees");
        suggestList = new ArrayList<User>();
        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        cardSearch = findViewById(R.id.cardSearch);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    User item = dataSnap.getValue(User.class);
                    suggestList.add(item);
                }
                searchAdapter = new Adapter_Recyclerview(SearchActivity.this, suggestList);
                recyclerView.setAdapter(searchAdapter);
                recyclerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(searchBar!= null){
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }
    }
    private void search(String str){
        ArrayList<User> myList = new ArrayList<>();
        for(User searchMenu : suggestList){
            if(searchMenu.getName().toLowerCase().contains(str.toLowerCase())){
                myList.add(searchMenu);
            }
        }
        searchAdapter = new Adapter_Recyclerview(SearchActivity.this, myList);
        recyclerView.setAdapter(searchAdapter);
        if(str != null){
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }
}
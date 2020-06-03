package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.Adapter_Recyclerview;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.AddActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.CustomGridAdapter;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.CustomListAdapter;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.R;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.SearchActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.Session;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.User;

public class HomeFragment extends Fragment {
    private FloatingActionButton fab;
    User user;
    Session sharedpref;
    ArrayList<User> list;
    Adapter_Recyclerview adapter;
    private TextView searchBar, tvSortAsc, tvSortDesc;

    private ListView listView;
    private GridView gridView;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        sharedpref = new Session(getContext());
        if(sharedpref.loadNightModeState()) {
            getContext().setTheme(R.style.DarkTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            getContext().setTheme(R.style.AppTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }

        listView = root.findViewById(R.id.listView);
        gridView = root.findViewById(R.id.gridView);
        recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        if (sharedpref.loadView() == 0){
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }else if(sharedpref.loadView() == 1){
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else if(sharedpref.loadView() == 2){
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("employees");
        recyclerView.setAdapter(adapter);

        sortAsc();

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

        searchBar = root.findViewById(R.id.searchBar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchScreen  = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchScreen);
            }
        });

        tvSortAsc = root.findViewById(R.id.tvSortAsc);
        tvSortAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortAsc.setVisibility(View.GONE);
                tvSortDesc.setVisibility(View.VISIBLE);
                sortAsc();
            }
        });

        tvSortDesc = root.findViewById(R.id.tvSortDesc);
        tvSortDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortAsc.setVisibility(View.VISIBLE);
                tvSortDesc.setVisibility(View.GONE);
                sortDesc();
            }
        });

        return root;
    }
    private void sortDesc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("employees").orderByChild("name");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<User>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String menuKey = ds.getKey();
                        User user = ds.getValue(User.class);
                        list.add(user);
                        Collections.reverse(list);
                    }
                    CustomListAdapter customListAdapter = new CustomListAdapter(getContext(), list);
                    listView.setAdapter(customListAdapter);

                    CustomGridAdapter customGridAdapter = new CustomGridAdapter(getContext(), list);
                    gridView.setAdapter(customGridAdapter);

                    Adapter_Recyclerview sortDesc = new Adapter_Recyclerview(getActivity(),list);
                    recyclerView.setAdapter(sortDesc);
                    sortDesc.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sortAsc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("employees").orderByChild("name");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<User>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String menuKey = ds.getKey();
                        User user = ds.getValue(User.class);
                        list.add(user);
                    }
                    CustomListAdapter customListAdapter = new CustomListAdapter(getContext(), list);
                    listView.setAdapter(customListAdapter);

                    CustomGridAdapter customGridAdapter = new CustomGridAdapter(getContext(), list);
                    gridView.setAdapter(customGridAdapter);

                    Adapter_Recyclerview sortAsc = new Adapter_Recyclerview(getActivity(),list);
                    recyclerView.setAdapter(sortAsc);
                    sortAsc.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
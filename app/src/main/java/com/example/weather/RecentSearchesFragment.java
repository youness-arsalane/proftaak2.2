package com.example.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weather.DBHandler.RecentSearchesDBHelper;

import java.util.Objects;

public class RecentSearchesFragment extends Fragment {

    private static RecentSearchesFragment instance;

    public static RecentSearchesFragment getInstance() {
        if (instance == null) {
            instance = new RecentSearchesFragment();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_recent_searches, container, false);

        RecentSearchesDBHelper recentSearchesDBHelper = new RecentSearchesDBHelper(Objects.requireNonNull(getActivity()).getApplicationContext());

        ListView listView = itemView.findViewById(R.id.listView);
        String[] listItem = recentSearchesDBHelper.getAllKeywords();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listItem
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String value = adapter.getItem(position);
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }
        });

        return itemView;
    }
}

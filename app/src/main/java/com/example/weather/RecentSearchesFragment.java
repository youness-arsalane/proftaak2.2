package com.example.weather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecentSearchesFragment extends Fragment {

    private ListView listView;
    private TextView textView;
    private String[] listItem;

    @SuppressLint("StaticFieldLeak")
    private static RecentSearchesFragment instance;

    static RecentSearchesFragment getInstance() {
        if (instance == null) {
            instance = new RecentSearchesFragment();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_recent_searches, container, false);


        if (getView() != null) {
            listView = getView().findViewById(R.id.listView);


            textView = getActivity().findViewById(R.id.textView);
            listItem = getResources().getStringArray(R.array.array_technology);


            final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listItem
            );


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String value = adapter.getItem(position);
                    Toast.makeText(getActivity().getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                }
            });

        }
        return itemView;
    }
}

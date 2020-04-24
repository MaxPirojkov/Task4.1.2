package ru.netology.lists;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.provider.ContactsContract.Intents.Insert.NAME;

public class ListViewActivity extends AppCompatActivity {

    private static final String FIRST_ITEM = "0";
    private static final String SECOND_ITEM = "1";

    List<Map<String, String>> content = new ArrayList<>();
    SimpleAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    ListView listView;

    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.list);

        saveText();

        prepareContent();

        createAdapter();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                content.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        readText();
                    }

                }, 1000);
            }
        });

    }

    private void saveText() {
        sPref = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (!sPref.contains(NAME)) {
            sPref.edit()
                    .putString(SAVED_TEXT, getString(R.string.large_text))
                    .apply();
        }
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, getString(R.string.large_text));
        ed.apply();
    }

    private void readText() {
        content.clear();
        prepareContent();
        adapter.notifyDataSetChanged();
    }

    private void prepareContent() {
        String savedText = sPref.getString(SAVED_TEXT, "");
        String[] arrayContent = Objects.requireNonNull(savedText).split("\n\n");
        for (String s : arrayContent) {
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put(FIRST_ITEM, s);
            rowMap.put(SECOND_ITEM, String.valueOf(s.length()));
            content.add(rowMap);
        }
    }

    private void createAdapter() {
        adapter = new SimpleAdapter(this, content, android.R.layout.simple_list_item_2,
                new String[]{FIRST_ITEM, SECOND_ITEM},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }


}


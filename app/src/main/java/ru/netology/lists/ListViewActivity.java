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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.provider.ContactsContract.Intents.Insert.NAME;

public class ListViewActivity extends AppCompatActivity {

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

        createAdapter(content);


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
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            readText();
                        }
                    }
                }, 1000);
            }
        });

    }

    private void saveText() {
        sPref = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (!sPref.getString(NAME, "Default value").equals("Default value")) {
            startActivity(new Intent(ListViewActivity.this, ListViewActivity.class));
            finish();
        }
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, getString(R.string.large_text));
        ed.apply();
    }

    private void readText() {
        content.clear();
        String savedText = sPref.getString(SAVED_TEXT, "");
        prepareContent();
        adapter.notifyDataSetChanged();
    }

    private void prepareContent() {
        String[] arrayContent = getString(R.string.large_text).split("\n\n");
        for (int i = 0; i < arrayContent.length; i++) {
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("0", arrayContent[i]);
            rowMap.put("1", String.valueOf(arrayContent[i].length()));
            content.add(rowMap);
        }
    }

    @NonNull
    private void createAdapter(List<Map<String, String>> values) {
        adapter = new SimpleAdapter(this, content, android.R.layout.simple_list_item_2,
                new String[]{"0", "1"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }


}


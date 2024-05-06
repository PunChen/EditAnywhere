package com.example.editanywhere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.entity.view.AdapterEvent;
import com.example.editanywhere.itf.AdapterEventListener;
import com.example.editanywhere.enumrate.AdapterEventType;
import com.example.editanywhere.adapter.SelectBookListAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.ActivitySelectNotebookBinding;
import com.example.editanywhere.entity.model.Notebook;

public class SelectNotebookActivity extends AppCompatActivity {


    private static final String TAG = "MoveEntryActivity";
    private ActivitySelectNotebookBinding binding;
    private SelectBookListAdapter selectBookListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_notebook);

        binding = ActivitySelectNotebookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();

        initNotebook();

    }

    private void initToolbar() {
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.getMenu().findItem(R.id.menu_entry_edit).setVisible(false);
    }

    private void initNotebook() {
        RecyclerView rcBookList = binding.rvNotebook;
        RecyclerViewNoBugLinearLayoutManager layoutManager =
                new RecyclerViewNoBugLinearLayoutManager(SelectNotebookActivity.this);
        rcBookList.setLayoutManager(layoutManager);
        selectBookListAdapter = new SelectBookListAdapter(SelectNotebookActivity.this);
        rcBookList.setAdapter(selectBookListAdapter);
        selectBookListAdapter.refreshAll();
        selectBookListAdapter.setAdapterEventListener(new AdapterEventListener() {
            @Override
            public void onEvent(AdapterEvent<?> event) {
                if (event.getType() == AdapterEventType.EVENT_NOTEBOOK_SELECTED) {
                    Long bookId = (Long) event.getObj();
                    Intent intent = new Intent();
                    intent.putExtra(Notebook.class.getSimpleName(), bookId);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
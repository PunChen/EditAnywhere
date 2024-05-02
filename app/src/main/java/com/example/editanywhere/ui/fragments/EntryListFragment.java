package com.example.editanywhere.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.adapter.AdapterEventType;
import com.example.editanywhere.adapter.BookViewAdapter;
import com.example.editanywhere.adapter.EntryListAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.FragmentEntryListBinding;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class EntryListFragment extends CustomFragment {

    private static final String TAG = "EntryListFragment";
    private final Activity activity;
    private FragmentEntryListBinding binding;
    private EntryListAdapter entryListAdapter;
    private BookViewAdapter bookViewAdapter;

    public EntryListFragment(Activity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEntryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // 词条
        RecyclerView rcEntryList = binding.rcEntryList;
        RecyclerViewNoBugLinearLayoutManager layoutManager = new RecyclerViewNoBugLinearLayoutManager(activity);
        rcEntryList.setLayoutManager(layoutManager);
        entryListAdapter = new EntryListAdapter(activity);
        rcEntryList.setAdapter(entryListAdapter);
        // 笔记本
        RecyclerView rcBookList = binding.rcBookList;
        RecyclerViewNoBugLinearLayoutManager bookLayoutManager =
                new RecyclerViewNoBugLinearLayoutManager(
                        activity, RecyclerViewNoBugLinearLayoutManager.HORIZONTAL, false);
        rcBookList.setLayoutManager(bookLayoutManager);
        bookViewAdapter = new BookViewAdapter(activity);
        rcBookList.setAdapter(bookViewAdapter);

        bookViewAdapter.setOtherAdapterListener(event -> {
            if (event.getType() == AdapterEventType.EVENT_ITEM_CLICK) {
                EntryService.getInstance(activity).queryAllByNotebookId(event.getArg1(), new EntryServiceCallback<>() {
                    @Override
                    public void onSuccess(List<Entry> result) {
                        entryListAdapter.initList(result);
                    }
                    @Override
                    public void onFailure(String errMsg) {
                        ToastUtil.toast(activity, "queryAllByNotebookId fail, err: " + errMsg);
                    }
                });
            } else if (event.getType() == AdapterEventType.EVENT_QUERY_ALL) {
                entryListAdapter.refreshAll();
            }
        });


//        bookViewAdapter.a
        //toolbar
        initToolbar();

        return root;
    }

    private void initToolbar() {
        final Toolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> {
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).openDrawer();
            } else {
                Log.e(TAG, "initToolbar: activity not instance of MainActivity");
            }
        });
        toolbar.getMenu().findItem(R.id.action_add_entry).setOnMenuItemClickListener(item -> {
            showAddEntryAlertDialog();
            return false;
        });

        binding.svSearchEntry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || "".equals(newText)) {
                    EntryService.getInstance(activity).queryAll(new EntryServiceCallback<List<Entry>>() {
                        @Override
                        public void onSuccess(List<Entry> result) {
                            refreshEntryList(result);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            ToastUtil.toast(activity, errMsg);
                        }


                    });
                } else {
                    EntryService.getInstance(activity).queryByEntryName(newText, new EntryServiceCallback<Entry>() {
                        @Override
                        public void onSuccess(Entry result) {
                            refreshEntryList(List.of(result));
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            ToastUtil.toast(activity, errMsg);
                        }
                    });
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: refresh all entry list");
        entryListAdapter.refreshAll();
        bookViewAdapter.refreshAll();
    }

    private void showAddEntryAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        EditText editText = new EditText(activity);
        editText.setHint("词条名称");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("添加词条");//设置对话框的标题
        dialog.setView(editText);
        dialog.setCancelable(true);//设置对话框是否可以取消
        //确定按钮的点击事件
        dialog.setPositiveButton("确认", (dialog12, which) -> {
            String text = editText.getText().toString();
            if (!"".equals(text)) {
                entryListAdapter.tryAddEntry(text);
            } else {
                Toast.makeText(activity, "input can not be empty!", Toast.LENGTH_SHORT).show();
            }
            dialog12.dismiss();
        });
        //取消按钮的点击事件
        dialog.setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss());
        dialog.show();//显示对话框
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void refreshEntryList(List<Entry> entryList) {
        if (entryList == null) {
            entryList = new ArrayList<>();
        }
        // fragment 加载数据
        if (entryListAdapter != null) {
            entryListAdapter.initList(entryList);
        }
    }


}
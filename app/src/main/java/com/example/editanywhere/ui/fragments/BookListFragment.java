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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.adapter.BookListAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.FragmentBookListBinding;
import com.example.editanywhere.entity.model.Notebook;

import java.util.ArrayList;
import java.util.List;


public class BookListFragment extends CustomFragment {

    private static final String TAG = "BookListFragment";
    private final Activity activity;
    private FragmentBookListBinding binding;
    private BookListAdapter bookListAdapter;

    public BookListFragment(Activity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBookListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView rcBookList = binding.rcBookList;
        RecyclerViewNoBugLinearLayoutManager layoutManager = new RecyclerViewNoBugLinearLayoutManager(activity);
        rcBookList.setLayoutManager(layoutManager);
        bookListAdapter = new BookListAdapter(activity);
        rcBookList.setAdapter(bookListAdapter);
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
            showAddBookAlertDialog();
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: refresh all entry list");
        bookListAdapter.refreshAll();
    }

    private void showAddBookAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        EditText editText = new EditText(activity);
        editText.setHint("笔记本名称");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("添加笔记本");//设置对话框的标题
        dialog.setView(editText);
        dialog.setCancelable(true);//设置对话框是否可以取消
        //确定按钮的点击事件
        dialog.setPositiveButton("确认", (dialog12, which) -> {
            String text = editText.getText().toString();
            if (!"".equals(text)) {
                bookListAdapter.tryAddEntry(text);
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

    public void refreshEntryList(List<Notebook> notebookList) {
        if (notebookList == null) {
            notebookList = new ArrayList<>();
        }
        // fragment 加载数据
        if (bookListAdapter != null) {
            bookListAdapter.initList(notebookList);
        }
    }


}
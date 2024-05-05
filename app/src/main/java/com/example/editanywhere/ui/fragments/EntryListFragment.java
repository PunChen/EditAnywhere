package com.example.editanywhere.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.EntryInfoActivity;
import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.SelectNotebookActivity;
import com.example.editanywhere.adapter.AdapterEventType;
import com.example.editanywhere.adapter.BookViewAdapter;
import com.example.editanywhere.adapter.EntryListAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.FragmentEntryListBinding;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.model.Notebook;
import com.example.editanywhere.entity.view.EntryView;
import com.example.editanywhere.entity.view.NotebookView;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.service.NoteBookService;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class EntryListFragment extends CustomFragment {

    private static final String TAG = "EntryListFragment";
    private final Activity activity;
    private FragmentEntryListBinding binding;
    private EntryListAdapter entryListAdapter;
    private BookViewAdapter bookViewAdapter;

    private Boolean isSearching = false;

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

        bookViewAdapter.setAdapterEventListener(event -> {
            if (event.getType() == AdapterEventType.EVENT_REFRESH_ENTRY_LIST) {
                if (isSearching) {
                    String text = binding.svSearchEntry.getQuery().toString();
                    entryListAdapter.searchContentByBook(text, bookViewAdapter.getSelectedNotebook());
                } else {
                    entryListAdapter.refreshAll(bookViewAdapter.getSelectedNotebook());
                }
            }
        });
        entryListAdapter.setAdapterEventListener(event -> {
            if (event.getType() == AdapterEventType.EVENT_SHOW_ENTRY_OP_MENU) {
                showBottomOpMenu();
            } else if (event.getType() == AdapterEventType.EVENT_HIDE_ENTRY_OP_MENU) {
                hideBottomOpMenu();
            }
        });
        // 加载笔记本列表
        bookViewAdapter.refreshAll();
        //toolbar
        initToolbar();

        // 底部词条操作
        initBottomOpMenu();
        return root;
    }

    private void showBottomOpMenu() {
        binding.clEntryOperateGroup.setVisibility(View.VISIBLE);
        entryListAdapter.setBatchOperating(true);
    }

    private void hideBottomOpMenu() {
        binding.clEntryOperateGroup.setVisibility(View.GONE);
        entryListAdapter.setBatchOperating(false);
    }

    private void moveEntryListToNotebook(NotebookView fromNotebook, Long tgtBookId, Set<Long> entryIdSet) {
        if (fromNotebook.isAll()) {
            if(!NoteBookService.getInstance(activity).addEntryToNotebookByIdSet(tgtBookId, entryIdSet)) {
                ToastUtil.toast(activity, "addEntryToNotebookByIdSet fail");
            }
        } else {
            if(!NoteBookService.getInstance(activity).moveEntryToNotebookByIdSet(fromNotebook.getId(), tgtBookId, entryIdSet)) {
                ToastUtil.toast(activity, "moveEntryToNotebookByIdSet fail");
            }
        }
        // 重新加载当前笔记本，注意有过滤场景
        String text = binding.svSearchEntry.getQuery().toString();
        entryListAdapter.searchContentByBook(text, bookViewAdapter.getSelectedNotebook());
    }

    private final ActivityResultLauncher<Intent> getSelectedNotebookLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (entryListAdapter != null && bookViewAdapter != null && result.getData() != null) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        List<EntryView> selectedEntry = entryListAdapter.getAllSelectedEntry();
                        Set<Long> idSet = selectedEntry.stream().map(EntryView::getId).collect(Collectors.toSet());
                        NotebookView fromNotebook = bookViewAdapter.getSelectedNotebook();
                        long bookId = result.getData().getLongExtra(Notebook.class.getSimpleName(), -1L);
                        if (bookId != -1L) {
                            moveEntryListToNotebook(fromNotebook, bookId, idSet);
                        }
                    }
                }
            });
    private void initBottomOpMenu() {
        binding.clEntryOperateGroup.setVisibility(View.GONE);
        binding.rbActionCancel.setOnClickListener(v -> {
            binding.clEntryOperateGroup.setVisibility(View.GONE);
            entryListAdapter.setCheckStatusForAllEntry(false, false);
            hideBottomOpMenu();
        });
        binding.rbActionSelectAll.setOnClickListener(v ->
                entryListAdapter.setCheckStatusForAllEntry(true, !entryListAdapter.isAllChecked()));
        binding.rbActionMoveTo.setOnClickListener(v -> {
            List<EntryView> selectedEntry = entryListAdapter.getAllSelectedEntry();
            if (selectedEntry.isEmpty()) {
                ToastUtil.toast(activity, "no items selected");
                return;
            }
            // 选择目标笔记本
            Intent intent = new Intent(activity, SelectNotebookActivity.class);
            getSelectedNotebookLauncher.launch(intent);
        });
        binding.rbActionDelete.setOnClickListener(v -> {
            showDeleteEntryAlertDialog();
        });

    }

    private void showDeleteEntryAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("");//设置对话框的标题
        dialog.setMessage("确认删除？");//设置对话框的内容
        dialog.setCancelable(true);//设置对话框是否可以取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            //确定按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<EntryView> toDelList = entryListAdapter.getAllSelectedEntry();
                Set<Long> idSet = toDelList.stream().map(EntryView::getId).collect(Collectors.toSet());
                EntryService.getInstance(activity).deleteByEntryIdSet(idSet, new EntryServiceCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            // 删除词条之后，删除笔记本中的关联关系
                            if (!NoteBookService.getInstance(activity).deleteEntryBookKeyByEntryId(idSet)) {
                                ToastUtil.toast(activity, "deleteEntryBookKeyByEntryId fail");
                            } else {
                                entryListAdapter.deleteSelectedEntryOnView();
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ToastUtil.toast(activity, errMsg);
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            //取消按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();//显示对话框
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

        binding.svSearchEntry.setOnSearchClickListener(v -> {
            toolbar.getMenu().findItem(R.id.action_add_entry).setEnabled(false);
            isSearching = true;
        });

        binding.svSearchEntry.setOnCloseListener(() -> {
            toolbar.getMenu().findItem(R.id.action_add_entry).setEnabled(true);
            isSearching = false;
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
                    entryListAdapter.refreshAll(bookViewAdapter.getSelectedNotebook());
                } else {
                    entryListAdapter.searchContentByBook(newText, bookViewAdapter.getSelectedNotebook());
                }
                return false;
            }
        });

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
        NotebookView notebookView = bookViewAdapter.getSelectedNotebook();
        dialog.setPositiveButton("确认", (dialog12, which) -> {
            String text = editText.getText().toString();
            if (!"".equals(text)) {
                entryListAdapter.tryAddEntry(notebookView, text);
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

    @Override
    public void onSwitch() {
        if (bookViewAdapter != null) {
            bookViewAdapter.refreshAll();
            if (entryListAdapter != null) {
                entryListAdapter.refreshAll(bookViewAdapter.getSelectedNotebook());
            }
        }
    }
}
package com.example.editanywhere.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.adapter.EntryListAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.FragmentEntryListBinding;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.ApiUti;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.OkHttpCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class EntryListFragment extends CustomFragment {

    private FragmentEntryListBinding binding;
    private EntryListAdapter entryListAdapter;
    private final MainActivity fromActivity;
    public EntryListFragment(MainActivity fromActivity){
        this.fromActivity = fromActivity;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEntryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView rcEntryList = binding.rcEntryList;
        entryListAdapter = new EntryListAdapter(fromActivity);
        RecyclerViewNoBugLinearLayoutManager layoutManager = new RecyclerViewNoBugLinearLayoutManager(fromActivity);
        rcEntryList.setLayoutManager(layoutManager);
        rcEntryList.setAdapter(entryListAdapter);

        //toolbar
        initToolbar();

        onUpdate(null);

        return root;
    }

    private void initToolbar(){
        final Toolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(view -> fromActivity.openDrawer());
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
                if(newText == null || "".equals(newText)){
                    OKHttpUtil.post(ApiUti.API_ENTRY_QUERY_ALL,
                            new ApiUti.Builder().build(),
                            new OkHttpCallBack() {
                                @Override
                                public void onSuccess(String res) {
                                    List<Entry> entries = JSON.parseArray(res,Entry.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("List<Entry>", (Serializable) entries);
                                    onUpdate(bundle);
                                }
                                @Override
                                public void onError(String msg) {
                                    fromActivity.makeToast("onError: "+msg);
                                }
                            });
                }else {
                    OKHttpUtil.post(ApiUti.API_ENTRY_QUERY,
                            new ApiUti.Builder().add("entryName", newText).build(),
                            new OkHttpCallBack() {
                                @Override
                                public void onSuccess(String res) {
                                    Entry entry = JSON.parseObject(res,Entry.class);
                                    List<Entry> entries = new ArrayList<>();
                                    entries.add(entry);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("List<Entry>", (Serializable) entries);
                                    onUpdate(bundle);
                                }
                                @Override
                                public void onError(String msg) {
                                    fromActivity.makeToast("onError: "+msg);
                                }
                            });
                }
                return false;
            }
        });

    }

    private void showAddEntryAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder (fromActivity);
        EditText editText = new EditText(fromActivity);
        editText.setHint("词条名称");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("添加词条");//设置对话框的标题
        dialog.setView(editText);
        dialog.setCancelable(true);//设置对话框是否可以取消
        //确定按钮的点击事件
        dialog.setPositiveButton("确认", (dialog12, which) -> {
            String text = editText.getText().toString();
            if(!"".equals(text)){
                postAddEntry(text);
            }else {
                fromActivity.makeToast("input can not be empty!");
            }
            dialog12.dismiss();
        });
        //取消按钮的点击事件
        dialog.setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss());
        dialog.show();//显示对话框
    }

    private void postAddEntry(String entryName){
        OKHttpUtil.post(ApiUti.API_ENTRY_ADD,
                new ApiUti.Builder().add("entryName", entryName)
                        .add("entryContent", JSON.toJSON(new String[]{entryName})).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry entry = JSON.parseObject(res,Entry.class);
                        syncAdapterData(entry);
                    }
                    @Override
                    public void onError(String msg) {
                        fromActivity.makeToast("onError: "+msg);
                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void syncAdapterData(Entry addEntry){
        entryListAdapter.onDataSetInsertOneSync(0,addEntry);
    }

    @Override
    public void onUpdate(Bundle bundle) {
        if (bundle != null && bundle.get("List<Entry>") != null){
            List<Entry> entries = (List<Entry>) bundle.get("List<Entry>");
            // fragment 加载数据
            if(entryListAdapter != null){
                entryListAdapter.initList(entries);
            }
        }else {
            if(entryListAdapter != null){
                entryListAdapter.initList();
            }

        }
    }

}
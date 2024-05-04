package com.example.editanywhere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.editanywhere.adapter.EntryContentAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.databinding.ActivityEntryInfoBinding;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.utils.DateUtil;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class EntryInfoActivity extends AppCompatActivity {

    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_UPDATE = 3;
    private static final String TAG = "EntryInfoActivity";
    private ActivityEntryInfoBinding binding;
    private Entry entry;
    private EntryContentAdapter entryContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntryInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.tbToolbar);

        initToolbar();

        initEntry();
    }

    private void initToolbar() {
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.getMenu().findItem(R.id.menu_entry_edit).setOnMenuItemClickListener(menuItem -> {
            showAddEntryContentAlertDialog();
            return false;
        });
    }

    private void initViewByEntry() {
        binding.tvEntryName.setText(entry.getEntryName());
        binding.tvCreateTime.setText(DateUtil.dateFormat(entry.getCreateTime(), DateUtil.DEFAULT_DATE_FORMAT));
        binding.tvUpdateTime.setText(DateUtil.dateFormat(entry.getUpdateTime(), DateUtil.DEFAULT_DATE_FORMAT));
    }

    private void initAdapter() {
        entryContentAdapter = new EntryContentAdapter(this);
        RecyclerViewNoBugLinearLayoutManager layoutManager = new RecyclerViewNoBugLinearLayoutManager(this);
        binding.rvEntryContent.setLayoutManager(layoutManager);
        binding.rvEntryContent.setAdapter(entryContentAdapter);
        entryContentAdapter.initList(entry);
    }

    private void initEntry() {
        Intent intent = getIntent();

        long id = intent.getLongExtra(Entry.class.getSimpleName(), -1L);
        if (id == -1L) {
            ToastUtil.toast(EntryInfoActivity.this, "initEntry fail");
            return;
        }
        EntryService.getInstance(EntryInfoActivity.this).queryByEntryId(id, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                entry = result;
                initViewByEntry();
                initAdapter();
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(EntryInfoActivity.this, errMsg);
            }
        });

    }

    private void showAddEntryContentAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EntryInfoActivity.this);
        EditText editText = new EditText(EntryInfoActivity.this);
        editText.setHint("添加内容");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("添加词条内容");//设置对话框的标题
        dialog.setView(editText);
        dialog.setCancelable(true);//设置对话框是否可以取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            //确定按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (!"".equals(text)) {
                    postAddEntryContent(text);
                } else {
                    ToastUtil.toast(EntryInfoActivity.this, "input can not be empty!");
                }
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

    private void updateViewWithEntry(Entry result, String addContent) {
        this.entry = result;
        initViewByEntry();
        if (entryContentAdapter != null) {
            entryContentAdapter.onDataSetInsertOneSync(0, addContent);
        }
    }

    private void postAddEntryContent(String addText) {
        List<String> newEntryContentList = new ArrayList<>(entryContentAdapter.getEntryContentList());
        newEntryContentList.add(0, addText);
        EntryService.getInstance(EntryInfoActivity.this).editEntryContentByEntryId(entry.getId(),
                newEntryContentList, new EntryServiceCallback<Entry>() {
                    @Override
                    public void onSuccess(Entry result) {
                        updateViewWithEntry(result, addText);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ToastUtil.toast(EntryInfoActivity.this, errMsg);
                    }
                });
    }


}
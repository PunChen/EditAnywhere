package com.example.editanywhere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.adapter.EntryContentAdapter;
import com.example.editanywhere.bugfix.RecyclerViewNoBugLinearLayoutManager;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.ApiUti;
import com.example.editanywhere.utils.DateUtil;
import com.example.editanywhere.databinding.ActivityEntryInfoBinding;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.OkHttpCallBack;

import java.util.ArrayList;
import java.util.List;

public class EntryInfoActivity extends AppCompatActivity {

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
        initView();
    }

    private void initToolbar(){
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.getMenu().findItem(R.id.menu_entry_edit).setOnMenuItemClickListener(menuItem -> {
            showAddEntryContentAlertDialog();
            return false;
        });
    }

    private void initView(){
        Intent intent = getIntent();
        entry = (Entry) intent.getSerializableExtra(Entry.class.getSimpleName());
        binding.tvEntryName.setText(entry.getEntryName());
        binding.tvEntryVersion.setText("v" + entry.getVersion());
        binding.tvCreateTime.setText(DateUtil.dateFormat(entry.getCreateTime(),DateUtil.DEFAULT_DATE_FORMAT));
        binding.tvUpdateTime.setText(DateUtil.dateFormat(entry.getUpdateTime(),DateUtil.DEFAULT_DATE_FORMAT));
        entryContentAdapter = new EntryContentAdapter(this);
        RecyclerViewNoBugLinearLayoutManager layoutManager = new RecyclerViewNoBugLinearLayoutManager(this);
        binding.rvEntryContent.setLayoutManager(layoutManager);
        binding.rvEntryContent.setAdapter(entryContentAdapter);
        entryContentAdapter.initList(entry);
    }

    private void showAddEntryContentAlertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder (EntryInfoActivity.this);
        EditText editText = new EditText(EntryInfoActivity.this);
        editText.setHint("添加内容");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("添加词条内容");//设置对话框的标题
        dialog.setView(editText);
        dialog.setCancelable(true);//设置对话框是否可以取消
        dialog.setPositiveButton("确认", new DialogInterface. OnClickListener() {
            //确定按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if(!"".equals(text)){
                    postAddEntryContent(text);
                }else {
                    makeToast("input can not be empty!");
                }
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface. OnClickListener() {
            //取消按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();//显示对话框
    }

    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_ID_TOAST:
                    String toastMsg = msg.getData().getString(MSG_KEY_TOAST_MSG);
                    if(toastMsg != null){
                        Toast.makeText(EntryInfoActivity.this, toastMsg,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };



    private void makeToast(String msg){
        Message message = new Message();
        message.what = MSG_ID_TOAST;
        message.getData().putString(MSG_KEY_TOAST_MSG,msg);
        handler.sendMessage(message);
    }
    private void postAddEntryContent(String addText){
        String entryName = entry.getEntryName();
        List<String> newEntryContentList = new ArrayList<>(entryContentAdapter.getEntryContentList());
        newEntryContentList.add(0,addText);
        OKHttpUtil.post(ApiUti.API_ENTRY_EDIT,
                new ApiUti.Builder().add("entryName", entryName).add("entryContent", JSON.toJSON(newEntryContentList)).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry entry = JSON.parseObject(res,Entry.class);
                        syncAdapterData(addText);
                    }
                    @Override
                    public void onError(String msg) {
                        makeToast("onError: "+msg);
                    }
                });
    }
    private void syncAdapterData(String addText){
        entryContentAdapter.onDataSetInsertOneSync(0,addText);
    }

}
package com.example.editanywhere.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.EntryInfoActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.dao.EntryDao;
import com.example.editanywhere.dao.EntryDatabase;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.service.LocalEntryService;
import com.example.editanywhere.service.RemoteEntryService;
import com.example.editanywhere.utils.ApiUti;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.OkHttpCallBack;
import com.example.editanywhere.utils.SPUtil;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.ViewHolder> {


    private final List<Entry> entryList = new ArrayList<>();
    private final Context context;
    private final Activity activity;

    private static final String TAG = "EntryListAdapter";
    public List<Entry> getEntryList() {
        return entryList;
    }

    private String getTag(){return this.getClass().getSimpleName();}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_entry_name;
        private final TextView tv_entry_version;
        private final ImageButton iv_entry_delete;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_entry_name = view.findViewById(R.id.tv_entry_name);
            tv_entry_version = view.findViewById(R.id.tv_entry_version);
            iv_entry_delete = view.findViewById(R.id.iv_entry_delete);
        }

    }

    public void tryAddEntry(String entryName){
        EntryService.getInstance(activity).addByEntryName(entryName, new EntryServiceCallback() {
            @Override
            public void onAddByEntryName(Entry result) {
                onDataSetInsertOneSync(0, result);
            }

            @Override
            public void onFinish(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }
    public EntryListAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity;
        EntryService.getInstance(activity).queryAll( new EntryServiceCallback() {
            @Override
            public void onQueryAll(List<Entry> result) {
                initList(result);
            }
            @Override
            public void onFinish(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_item, viewGroup, false);
        return new ViewHolder(view);
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Entry entry = entryList.get(position);
        viewHolder.tv_entry_name.setText(entry.getEntryName());
        viewHolder.tv_entry_version.setText(String.format("v%s",entry.getVersion()));
        viewHolder.iv_entry_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder (context);
                //通过AlertDialog.Builder创建出一个AlertDialog的实例
                dialog.setTitle("");//设置对话框的标题
                dialog.setMessage("确认删除？");//设置对话框的内容
                dialog.setCancelable(true);//设置对话框是否可以取消
                dialog.setPositiveButton("确认", new DialogInterface. OnClickListener() {
                    //确定按钮的点击事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postDeleteEntry(entry,viewHolder.getBindingAdapterPosition());
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
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,EntryInfoActivity.class);
                intent.putExtra(Entry.class.getSimpleName(),entry.getEntryName());
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public void onDataSetChanged(List<Entry> entryList){
        if(entryList != null){
            int preSize = this.entryList.size();
            this.entryList.clear();
            notifyItemRangeRemoved(0,preSize);
            this.entryList.addAll(entryList);
            notifyItemRangeChanged(0,entryList.size());
        }
    }

    public void onDataSetDeleteOne(int position) {
        if(position >= 0 && position < entryList.size()){
            entryList.remove(position);
            notifyItemRemoved(position);
            //刷新下标，不然下标就重复
            notifyItemRangeChanged(position, getItemCount()-position);
        }
    }

    public void onDataSetInsertOne( int position, Entry entry){
        if(position >= 0 && position <= entryList.size()){
            entryList.add(position, entry);
            notifyItemInserted(position);
            //刷新下标，不然下标就不连续
            notifyItemRangeChanged(position, getItemCount()-position);
        }
    }

    public void onDataSetInsertOneSync(int position ,Entry addEntry){
        Message message = new Message();
        message.what = MSG_ID_ITEM_INSERT;
        message.arg1=position;
        message.obj = addEntry;
        handler.sendMessage(message);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_ID_UPDATE_LIST:
                    List<Entry> entryList = (List<Entry>) msg.obj;
                    onDataSetChanged(entryList);
                    break;
                case MSG_ID_ITEM_INSERT:
                    Entry entry = (Entry) msg.obj;
                    int pos = msg.arg1;
                    onDataSetInsertOne(pos,entry);
                    break;
                case MSG_ID_ITEM_DELETE:
                    pos = msg.arg1;
                    onDataSetDeleteOne(pos);
                    break;
            }
        }
    };

    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_ITEM_DELETE= 3;
    private static final int MSG_ID_ITEM_INSERT= 4;
    private static final int MSG_ID_ITEM_EDIT= 5;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";


    public void initList(List<Entry> entries){
        Message message = new Message();
        message.obj = entries;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
    }


    private void postDeleteEntry(Entry entry, int pos){
        Log.e("TAG", "postDeleteEntry: "+entry.toString());
        EntryService.getInstance(activity).deleteByEntryName(entry.getEntryName(), new EntryServiceCallback() {
            @Override
            public void onDeleteByEntryName(Entry result) {
                Message message = new Message();
                message.what = MSG_ID_ITEM_DELETE;
                message.arg1 = pos;
                handler.sendMessage(message);
            }
            @Override
            public void onFinish(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

}

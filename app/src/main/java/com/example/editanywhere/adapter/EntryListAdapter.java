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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.EntryInfoActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.view.NotebookView;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.service.NoteBookService;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.ToastUtil;

import java.util.List;


public class EntryListAdapter extends BaseAdapter<Entry, EntryListAdapter.ViewHolder> {


    private static final String TAG = "EntryListAdapter";
    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_ITEM_DELETE = 3;
    private static final int MSG_ID_ITEM_INSERT = 4;
    private static final int MSG_ID_ITEM_EDIT = 5;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";
    private final Context context;
    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ID_UPDATE_LIST:
                    List<Entry> entryList = (List<Entry>) msg.obj;
                    onDataSetChanged(entryList);
                    break;
                case MSG_ID_ITEM_INSERT:
                    Entry entry = (Entry) msg.obj;
                    int pos = msg.arg1;
                    onDataSetInsertOne(pos, entry);
                    break;
                case MSG_ID_ITEM_DELETE:
                    pos = msg.arg1;
                    onDataSetDeleteOne(pos);
                    break;
            }
        }
    };

    public EntryListAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public void refreshAll(NotebookView notebookView) {
        if (notebookView == null || notebookView.isAll()) {
            EntryService.getInstance(activity).queryAll(new EntryServiceCallback<>() {
                @Override
                public void onSuccess(List<Entry> result) {
                    initList(result);
                }
                @Override
                public void onFailure(String errMsg) {
                    ToastUtil.toast(activity, "queryAll fail, err: " + errMsg);
                }
            });
        } else {
            EntryService.getInstance(activity).queryAllByNotebookId(notebookView.getId(), new EntryServiceCallback<>() {
                @Override
                public void onSuccess(List<Entry> result) {
                    initList(result);
                }
                @Override
                public void onFailure(String errMsg) {
                    ToastUtil.toast(activity, "queryAllByNotebookId fail, err: " + errMsg);
                }
            });
        }
    }

    public List<Entry> getEntryList() {
        return list;
    }

    public void tryAddEntry(NotebookView notebookView, String entryName) {
        EntryService.getInstance(activity).addByEntryName(entryName, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                onDataSetInsertOneSync(0, result);
                addEntryToNotebook(result, notebookView);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

    private void addEntryToNotebook(Entry entry, NotebookView notebookView) {
        if (notebookView.isAll()) { // 全部会默认加载所有，无需添加到笔记本中
            return;
        }
        if(NoteBookService.getInstance(activity).addEntryToNotebook(notebookView.getId(), entry.getId())) {
            refreshAll(notebookView);
        } else {
            ToastUtil.toast(context, "addEntryToNotebook fail");
        }
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
        Entry entry = list.get(position);
        viewHolder.tv_entry_name.setText(entry.getEntryName());
        viewHolder.iv_entry_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                //通过AlertDialog.Builder创建出一个AlertDialog的实例
                dialog.setTitle("");//设置对话框的标题
                dialog.setMessage("确认删除？");//设置对话框的内容
                dialog.setCancelable(true);//设置对话框是否可以取消
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    //确定按钮的点击事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postDeleteEntry(entry, viewHolder.getBindingAdapterPosition());
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
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EntryInfoActivity.class);
                intent.putExtra(Entry.class.getSimpleName(), entry.getId());
                context.startActivity(intent);
            }
        });
    }

    public void onDataSetInsertOneSync(int position, Entry addEntry) {
        Message message = new Message();
        message.what = MSG_ID_ITEM_INSERT;
        message.arg1 = position;
        message.obj = addEntry;
        handler.sendMessage(message);
    }

    public void initList(List<Entry> entries) {
        Message message = new Message();
        message.obj = entries;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
    }

    private void postDeleteEntry(Entry entry, int pos) {
        Log.e("TAG", "postDeleteEntry: " + entry.toString());
        EntryService.getInstance(activity).deleteByEntryId(entry.getId(), new EntryServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Message message = new Message();
                message.what = MSG_ID_ITEM_DELETE;
                message.arg1 = pos;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_entry_name;
        private final ImageButton iv_entry_delete;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_entry_name = view.findViewById(R.id.tv_entry_name);
            iv_entry_delete = view.findViewById(R.id.iv_entry_delete);
        }

    }

}

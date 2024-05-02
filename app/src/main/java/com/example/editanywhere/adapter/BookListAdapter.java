package com.example.editanywhere.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.editanywhere.R;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.model.Notebook;
import com.example.editanywhere.service.NoteBookService;
import com.example.editanywhere.utils.ToastUtil;

import java.util.List;


public class BookListAdapter extends BaseAdapter<Notebook, BookListAdapter.ViewHolder> {


    private static final String TAG = "BookListAdapter";
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
                    List<Notebook> notebooks = (List<Notebook>) msg.obj;
                    onDataSetChanged(notebooks);
                    break;
                case MSG_ID_ITEM_INSERT:
                    Notebook notebook = (Notebook) msg.obj;
                    int pos = msg.arg1;
                    onDataSetInsertOne(pos, notebook);
                    break;
                case MSG_ID_ITEM_DELETE:
                    pos = msg.arg1;
                    onDataSetDeleteOne(pos);
                    break;
            }
        }
    };


    public BookListAdapter(Activity activity) {
        super();
        this.activity = activity;
        this.context = activity;
        refreshAll();
    }

    public void refreshAll() {
        List<Notebook> notebookList = NoteBookService.getInstance(activity).getAllNotebooks();
        initList(notebookList);
    }

    public void onDataSetInsertOneSync(int position, Notebook notebook) {
        Message message = new Message();
        message.what = MSG_ID_ITEM_INSERT;
        message.arg1 = position;
        message.obj = notebook;
        handler.sendMessage(message);
    }

    public void tryAddEntry(String bookName) {
        Notebook notebook = new Notebook();
        if (NoteBookService.getInstance(activity).addOneNotebook(bookName, bookName, notebook)) {
            onDataSetInsertOneSync(0, notebook);
        } else {
            ToastUtil.toast(context, "tryAddEntry addOneNotebook fail");
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Notebook notebook = list.get(position);
        viewHolder.tv_book_name.setText(notebook.getNotebookName());
        viewHolder.tv_book_desc.setText(notebook.getDescription());
        viewHolder.iv_book_delete.setOnClickListener(new View.OnClickListener() {
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
                        postDeleteEntry(notebook, viewHolder.getBindingAdapterPosition());
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
                ToastUtil.toast(context, "clicked " + notebook.getNotebookName());
            }
        });
    }

    public void initList(List<Notebook> entries) {
        Message message = new Message();
        message.obj = entries;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
    }

    private void postDeleteEntry(Notebook notebook, int pos) {
        Log.e("TAG", "postDeleteEntry: " + notebook.toString());
        if (NoteBookService.getInstance(activity).deleteOneNotebook(notebook.getId())) {
            Message message = new Message();
            message.what = MSG_ID_ITEM_DELETE;
            message.arg1 = pos;
            handler.sendMessage(message);
        } else {
            ToastUtil.toast(activity, "delete notebook fail");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_book_name;
        private final TextView tv_book_desc;
        private final ImageButton iv_book_delete;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_book_name = view.findViewById(R.id.tv_book_name);
            tv_book_desc = view.findViewById(R.id.tv_book_desc);
            iv_book_delete = view.findViewById(R.id.iv_book_delete);
        }

    }

}

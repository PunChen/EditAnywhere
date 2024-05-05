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
import com.example.editanywhere.entity.model.Notebook;
import com.example.editanywhere.entity.view.NotebookView;
import com.example.editanywhere.service.NoteBookService;
import com.example.editanywhere.utils.ToastUtil;

import java.util.List;


public class SelectBookListAdapter extends BaseAdapter<Notebook, SelectBookListAdapter.ViewHolder> {


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

    private AdapterEventListener adapterEventListener;

    public void setAdapterEventListener(AdapterEventListener adapterEventListener) {
        this.adapterEventListener = adapterEventListener;
    }

    public SelectBookListAdapter(Activity activity) {
        super();
        this.activity = activity;
        this.context = activity;
    }

    public void refreshAll() {
        List<Notebook> notebookList = NoteBookService.getInstance(activity).getAllNotebooks();
        initList(notebookList);
    }
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
        viewHolder.iv_book_delete.setVisibility(View.GONE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterEventListener != null) {
                    AdapterEvent<Long> event = new AdapterEvent<>();
                    event.setObj(notebook.getId());
                    event.setType(AdapterEventType.EVENT_NOTEBOOK_SELECTED);
                    adapterEventListener.onEvent(event);
                }
            }
        });
    }


    public void initList(List<Notebook> entries) {
        Message message = new Message();
        message.obj = entries;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
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

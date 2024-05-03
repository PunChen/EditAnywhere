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
import android.widget.Button;
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
import java.util.stream.Collectors;


public class BookViewAdapter extends BaseAdapter<NotebookView, BookViewAdapter.ViewHolder> {
    private static final String TAG = "BookViewAdapter";
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
            if (msg.what == MSG_ID_UPDATE_LIST) {
                List<NotebookView> notebooks = (List<NotebookView>) msg.obj;
                onDataSetChanged(notebooks);
                // 全量加载笔记本完成后触发词条刷新机制
                if (otherAdapterListener != null) {
                    AdapterEvent event = new AdapterEvent();
                    event.setType(AdapterEventType.EVENT_REFRESH_ENTRY_LIST);
                    otherAdapterListener.onEvent(event);
                }
            }
        }
    };
    private AdapterEventListener otherAdapterListener;

    public void setOtherAdapterListener(AdapterEventListener otherAdapterListener) {
        this.otherAdapterListener = otherAdapterListener;
    }
    public NotebookView getSelectedNotebook() {
        for (NotebookView view : list) {
            if (view.getSelected()) {
                return view;
            }
        }
        return null;
    }

    public BookViewAdapter(Activity activity) {
        super();
        this.activity = activity;
        this.context = activity;
    }

    public void refreshAll() {
        List<Notebook> notebookList = NoteBookService.getInstance(activity).getAllNotebooks();
        // 全部
        NotebookView notebook = new NotebookView();
        notebook.setSelected(true);
        notebook.setNotebookName(context.getString(R.string.book_all));
        notebook.setAll(true);
        List<NotebookView> notebookViews = notebookList.stream().map(obj -> {
            NotebookView view = new NotebookView();
            view.setSelected(false);
            view.setId(obj.getId());
            view.setAll(false);
            view.setNotebookName(obj.getNotebookName());
            return view;
        }).collect(Collectors.toList());
        notebookViews.add(0, notebook);
        initList(notebookViews);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        NotebookView notebook = list.get(position);
        int color = notebook.getSelected() ? context.getColor(R.color.little_gray) : context.getColor(R.color.white);
        viewHolder.tv_inner.setBackgroundColor(color);
        viewHolder.tv_inner.setText(notebook.getNotebookName());
        int pos = position;
        viewHolder.tv_inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSelected(pos);
                if (otherAdapterListener != null) {
                    AdapterEvent event = new AdapterEvent();
                    if (!notebook.isAll()) {
                        event.setArg1(notebook.getId());
                    }
                    event.setType(AdapterEventType.EVENT_REFRESH_ENTRY_LIST);
                    otherAdapterListener.onEvent(event);
                }
            }
        });
    }

    private void changeSelected(final int pos) {
        for (NotebookView view : list) {
            view.setSelected(false);
        }
        list.get(pos).setSelected(true);
        notifyItemRangeChanged(0, list.size());
    }

    public void initList(List<NotebookView> entries) {
        Message message = new Message();
        message.obj = entries;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_inner;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_inner = view.findViewById(R.id.tv_inner);
        }

    }

}

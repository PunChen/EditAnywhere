package com.example.editanywhere.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.EntryInfoActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.view.AdapterEvent;
import com.example.editanywhere.entity.view.EntryView;
import com.example.editanywhere.entity.view.NotebookView;
import com.example.editanywhere.enumrate.AdapterEventType;
import com.example.editanywhere.itf.AdapterEventListener;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.service.NoteBookService;
import com.example.editanywhere.utils.DateUtil;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.EntryUtil;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class EntryListAdapter extends BaseAdapter<EntryView, EntryListAdapter.ViewHolder> {
    private static final String TAG = "EntryListAdapter";
    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_ITEM_DELETE = 3;
    private static final int MSG_ID_ITEM_INSERT = 4;
    private static final int MSG_ID_ITEM_EDIT = 5;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";

    public Boolean getBatchOperating() {
        return isBatchOperating;
    }

    public void setBatchOperating(Boolean batchOperating) {
        isBatchOperating = batchOperating;
        if (!isBatchOperating) {
            setCheckStatusForAllEntry(false, false);
        }
    }

    private Boolean isBatchOperating = false;
    private final Context context;
    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ID_UPDATE_LIST:
                    List<EntryView> entryList = (List<EntryView>) msg.obj;
                    if (isBatchOperating) {
                        entryList.forEach(obj -> {
                            obj.setShowCheckbox(true);
                            obj.setChecked(false);
                        });
                    }
                    onDataSetChanged(entryList);
                    break;
            }
        }
    };

    public EntryListAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public void searchContentByBook(String text, NotebookView notebookView) {
        if (notebookView == null || notebookView.isAll()) {
            EntryService.getInstance(activity).queryByEntryNameOrContent(text, new EntryServiceCallback<>() {
                @Override
                public void onSuccess(List<Entry> result) {
                    initList(result);
                }

                @Override
                public void onFailure(String errMsg) {
                    ToastUtil.toast(activity, "queryByEntryNameOrContent fail, err: " + errMsg);
                }
            });
        } else {
            EntryService.getInstance(activity).queryByEntryNameOrContentInNotebook(notebookView.getId(), text, new EntryServiceCallback<>() {
                @Override
                public void onSuccess(List<Entry> result) {
                    initList(result);
                }

                @Override
                public void onFailure(String errMsg) {
                    ToastUtil.toast(activity, "queryByEntryNameOrContentInNotebook fail, err: " + errMsg);
                }
            });
        }
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


    public void tryAddEntry(NotebookView notebookView, String entryName) {
        EntryService.getInstance(activity).addByEntryName(entryName, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                onDataSetInsertOneSync(0, EntryUtil.toEntryView(result));
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
        if (NoteBookService.getInstance(activity).addEntryToNotebook(notebookView.getId(), entry.getId())) {
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
        EntryView entry = list.get(position);
        viewHolder.tv_entry_name.setText(entry.getEntryName());
        viewHolder.tv_entry_update_time.setText(DateUtil.dateFormat(entry.getUpdateTime(), DateUtil.DATE_FORMAT_DATE_TIME));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBatchOperating) {// 批处理状态屏蔽点击事件
                    return;
                }
                Intent intent = new Intent(activity, EntryInfoActivity.class);
                intent.putExtra(Entry.class.getSimpleName(), entry.getId());
                context.startActivity(intent);
            }
        });

        viewHolder.cb_entry_select.setVisibility(entry.getShowCheckbox() ? View.VISIBLE : View.GONE);
        viewHolder.cb_entry_select.setChecked(entry.getChecked());
        viewHolder.cb_entry_select.setOnCheckedChangeListener((buttonView, isChecked) ->
                // 使用绝对位置进行绑定
                list.get(viewHolder.getAbsoluteAdapterPosition()).setChecked(isChecked));
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 长按后显示隐藏所有的所有的checkbox冰选中当前
                onSpecificEntryLongClick(entry);
                if (adapterEventListener != null) {
                    // 弹出词条操作列表
                    adapterEventListener.onEvent(new AdapterEvent<>(AdapterEventType.EVENT_SHOW_ENTRY_OP_MENU));
                }
                return false;
            }
        });
    }


    private AdapterEventListener adapterEventListener;

    public void setAdapterEventListener(AdapterEventListener adapterEventListener) {
        this.adapterEventListener = adapterEventListener;
    }

    public boolean isAllChecked() {
        // todo 数组优化
        for (EntryView view : list) {
            if (!view.getChecked()) {
                return false;
            }
        }
        return true;
    }

    public List<EntryView> getAllSelectedEntry() {
        return list.stream().filter(EntryView::getChecked).collect(Collectors.toList());
    }

    public void setCheckStatusForAllEntry(boolean showCheckbox, boolean checked) {
        for (EntryView view : list) {
            view.setShowCheckbox(showCheckbox);
            view.setChecked(checked);
        }
        notifyItemRangeChanged(0, list.size());
    }

    private void onSpecificEntryLongClick(final EntryView entryView) {
        // 所有词条隐藏删除按钮，显示选中按钮
        for (EntryView view : list) {
            view.setShowCheckbox(true);
            view.setChecked(false);
        }
        // 当前词条选中
        entryView.setChecked(true);
        // 刷新界面
        notifyItemRangeChanged(0, list.size());
        isBatchOperating = true;// 进入操作状态
    }

    public void onDataSetInsertOneSync(int position, EntryView addEntry) {
        onDataSetInsertOne(position, addEntry);
    }

    public void deleteSelectedEntryOnView() {
        List<int[]> delInfo = new ArrayList<>();
        boolean first = true;
        int cnt = 0;
        int pos = 0;
        for (int i = 0; i < list.size(); ++i) {
            EntryView view = list.get(i);
            if (view.getChecked()) {
                if (first) {
                    pos = i;
                    first = false;
                }
                cnt++;
            } else {
                if (!first) {
                    delInfo.add(new int[]{pos, cnt});
                    first = true;
                    cnt = 0;
                    pos = 0;
                }
            }
        }
        if (cnt > 0) {
            delInfo.add(new int[]{pos, cnt});
        }
        list.removeIf(EntryView::getChecked);
        Log.e(TAG, "deleteSelectedEntryOnView: " + delInfo);
        for (int i = delInfo.size() - 1; i >= 0; --i) {
            int[] del = delInfo.get(i);
            notifyItemRangeRemoved(del[0], del[1]);
        }
        setCheckStatusForAllEntry(false, false);
        if (adapterEventListener != null) {
            adapterEventListener.onEvent(new AdapterEvent<>(AdapterEventType.EVENT_HIDE_ENTRY_OP_MENU));
        }
    }

    public void initList(List<Entry> entries) {
        List<EntryView> entryViews = entries.stream().map(EntryUtil::toEntryView).collect(Collectors.toList());
        Message message = new Message();
        message.obj = entryViews;
        message.what = MSG_ID_UPDATE_LIST;
        handler.sendMessage(message);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_entry_name;
        private final TextView tv_entry_update_time;
        private final CheckBox cb_entry_select;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_entry_name = view.findViewById(R.id.tv_entry_name);
            tv_entry_update_time = view.findViewById(R.id.tv_entry_update_time);
            cb_entry_select = view.findViewById(R.id.cb_entry_select);
        }

    }

}

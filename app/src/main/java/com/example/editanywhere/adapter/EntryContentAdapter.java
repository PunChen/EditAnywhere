package com.example.editanywhere.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.editanywhere.R;
import com.example.editanywhere.entity.model.Content;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class EntryContentAdapter extends BaseAdapter<Content, EntryContentAdapter.ViewHolder> {

    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_ITEM_DELETE = 3;
    private static final int MSG_ID_ITEM_INSERT = 4;
    private static final int MSG_ID_ITEM_EDIT = 5;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";
    private static final String TAG = "EntryContentAdapter";
    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String toastMsg;
            switch (msg.what) {
                case MSG_ID_UPDATE_LIST:
                    Entry entry = (Entry) msg.obj;
                    List<Content> entryList = entry.getEntryContent();
                    toastMsg = msg.getData().getString(MSG_KEY_TOAST_MSG);
                    onDataSetChanged(entryList);
                    if (toastMsg != null) {
                        Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_ID_TOAST:
                    toastMsg = msg.getData().getString(MSG_KEY_TOAST_MSG);
                    if (toastMsg != null) {
                        Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_ID_ITEM_INSERT:
                    Content insertText = (Content) msg.obj;
                    int pos = msg.arg1;
                    onDataSetInsertOne(pos, insertText);
                    break;
                case MSG_ID_ITEM_DELETE:
                    pos = msg.arg1;
                    onDataSetDeleteOne(pos);
                    break;
                case MSG_ID_ITEM_EDIT:
                    pos = msg.arg1;
                    Content editText = (Content) msg.obj;
                    onDataSetEditOne(pos, editText);
                    break;
            }
        }
    };
    private Entry entry;

    public EntryContentAdapter(Activity activity) {
        this.activity = activity;
    }

    public static void copyToClipboard(Context context, Content content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content.getContent());
        clipboard.setPrimaryClip(clipData);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entry_content_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Content entryContentItem = list.get(position);
        viewHolder.tv_entry_content_item.setText(entryContentItem.getContent());
        viewHolder.itemView.setOnLongClickListener(v -> {
            showPopUpMenu(v, position);
            return false;
        });

        viewHolder.cb_content_item.setVisibility(entry.getShowContentCheckBox() ? View.VISIBLE : View.GONE);
        viewHolder.cb_content_item.setChecked(entryContentItem.getChecked());
        viewHolder.cb_content_item.setOnCheckedChangeListener((compoundButton, checked) -> {
            setContentItemChecked(entryContentItem, checked);
        });
    }

    private void setContentItemChecked(Content entryContentItem, boolean checked) {
        entryContentItem.setChecked(checked);
        EntryService.getInstance(activity).editEntryContentByEntryId(entry.getId(), list, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                Log.i(TAG, "onSuccess: " + result);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

    private void showPopUpMenu(View attachView, int itemPos) {
        PopupMenu popupMenu = new PopupMenu(activity, attachView);
        popupMenu.inflate(R.menu.menu_edit_entry_content);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_entry_content_edit) {
                //dialog
                showEditEntryContentAlertDialog(itemPos, list.get(itemPos));
            } else if (id == R.id.menu_entry_content_delete) {
                postDeleteEntryContent(itemPos);
            } else if (id == R.id.menu_entry_content_copy) {
                copyToClipboard(activity, list.get(itemPos));
                ToastUtil.toast(activity, activity.getResources().getString(R.string.tip_copied));
            }
            popupMenu.dismiss();
            return false;
        });
        popupMenu.show();
    }

    private void showEditEntryContentAlertDialog(int editPos, Content orgText) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        EditText editText = new EditText(activity);
        editText.setHint("新的内容");
        //通过AlertDialog.Builder创建出一个AlertDialog的实例
        dialog.setTitle("修改词条内容");//设置对话框的标题
        dialog.setView(editText);
        editText.setText(orgText.getContent());
        dialog.setCancelable(true);//设置对话框是否可以取消
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            //确定按钮的点击事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    Content editedContent = new Content(text, orgText.getChecked());
                    postEditEntryContent(editPos, editedContent);
                } else {
                    makeToast("input can not be empty!");
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

    private void postEditEntryContent(int editPos, Content destText) {
        List<Content> newEntryContentList = new ArrayList<>(getEntryContentList());
        newEntryContentList.set(editPos, destText);
        EntryService.getInstance(activity).editEntryContentByEntryId(entry.getId(), newEntryContentList, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                Message message = new Message();
                message.what = MSG_ID_ITEM_EDIT;
                message.obj = destText;
                message.arg1 = editPos;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }

    private void postDeleteEntryContent(int deletePos) {
        List<Content> newEntryContentList = new ArrayList<>(getEntryContentList());
        if (deletePos < 0 || deletePos >= newEntryContentList.size()) return;
        newEntryContentList.remove(deletePos);
        EntryService.getInstance(activity).editEntryContentByEntryId(entry.getId(), newEntryContentList, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                Message message = new Message();
                message.what = MSG_ID_ITEM_DELETE;
                message.arg1 = deletePos;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, errMsg);
            }
        });
    }


    public void initList(Entry entry) {
        if (entry == null) return;
        this.entry = entry;
        Log.e("TAG", "initList: " + entry);
        Message message = new Message();
        message.what = MSG_ID_UPDATE_LIST;
        message.obj = entry;
//        message.getData().putString(MSG_KEY_TOAST_MSG,"initList");
        handler.sendMessage(message);
    }

    public void makeToast(String msg) {
        Message message = new Message();
        message.what = MSG_ID_TOAST;
        message.getData().putString(MSG_KEY_TOAST_MSG, msg);
        handler.sendMessage(message);
    }

    public void onDataSetInsertOneSync(int position, Content addText) {
        Message message = new Message();
        message.what = MSG_ID_ITEM_INSERT;
        message.arg1 = position;
        message.obj = addText;
        handler.sendMessage(message);
    }

    public void setEntryContentShowCheckBox(boolean checked) {
        // 数据库更新
        entry.setShowContentCheckBox(!entry.getShowContentCheckBox());
        EntryService.getInstance(activity).saveEntryShowContentCheckBox(entry.getId(), checked, new EntryServiceCallback<Entry>() {
            @Override
            public void onSuccess(Entry result) {
                Log.i(TAG, "edit entry checked success");
                // 界面更新
                initList(result);
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(activity, "edit entry checked fail");
                Log.e(TAG, "setEntryContentShowCheckBox: " + errMsg);
            }
        });
    }

    public List<Content> getEntryContentList() {
        return list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_entry_content_item;
        private final CheckBox cb_content_item;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tv_entry_content_item = view.findViewById(R.id.tv_entry_content_item);
            cb_content_item = view.findViewById(R.id.cb_content_item);
        }
    }


}

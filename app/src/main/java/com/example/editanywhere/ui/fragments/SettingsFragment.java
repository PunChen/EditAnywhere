package com.example.editanywhere.ui.fragments;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.databinding.FragmentSettingsBinding;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.enumrate.FileType;
import com.example.editanywhere.service.EntryService;
import com.example.editanywhere.utils.DateUtil;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.EntryUtil;
import com.example.editanywhere.utils.FileUtils;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.SPUtil;
import com.example.editanywhere.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends CustomFragment {

    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private MainActivity fromActivity;
    private final ActivityResultLauncher<String[]> filePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                Log.i(TAG, "filePermissionLauncher: result:" + result);
                boolean hasRight = result.values().stream().allMatch(right -> right);
                if (!hasRight) {
                    ToastUtil.toast(fromActivity, "no permission to access file");
                }
            });
    private boolean hasFileAccess = false;
    private FileType fileType;
    // https://blog.csdn.net/hx7013/article/details/120916287
    private final ActivityResultLauncher<String> chooseFileLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), result -> {
                if (result == null) {
                    return;
                }
                String filePath = FileUtils.getPath(fromActivity, result);
                Log.i(TAG, "chooseFileLauncher: result:" + result + " filePath: " + filePath);
                requestPermission();
                doImportFromCsvFile(filePath, fileType);
            });
    private final ActivityResultLauncher<String> createFilerLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(), result -> {
                if (result == null) {
                    return;
                }
                String filePath = FileUtils.getPath(fromActivity, result);
                Log.i(TAG, "createFilerLauncher: result:" + result + " filePath: " + filePath);
                String fileName;
                if (filePath == null) {
                    fileName = buildFileName(fileType);
                } else {
                    fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                }
                requestPermission();
                doExportToFile(filePath, fileName, fileType);
            });


    public SettingsFragment(MainActivity fromActivity) {
        this.fromActivity = fromActivity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();

        return root;
    }

    private void initView() {
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> fromActivity.openDrawer());
        toolbar.getMenu().findItem(R.id.menu_settings_reset).setOnMenuItemClickListener(item -> {
            resetSettings();
            return false;
        });
        String address = OKHttpUtil.getUrl();
        binding.incEtLineServerAddress.etEditLineContent.setText(address);
        //ip修改按钮
        binding.incEtLineServerAddress.ibBtEditLineEdit.setOnClickListener(v -> {
            binding.incEtLineServerAddress.ibBtEditLineEdit.setVisibility(View.GONE);
            binding.incEtLineServerAddress.etEditLineContent.setEnabled(true);
            binding.incEtLineServerAddress.ibBtEditLineEnsure.setVisibility(View.VISIBLE);
        });
        //ip确认按钮
        binding.incEtLineServerAddress.ibBtEditLineEnsure.setOnClickListener(v -> {
            // update ip
            Application application = fromActivity.getApplication();
            String ip = binding.incEtLineServerAddress.etEditLineContent.getText().toString();
            SPUtil.putString(application, SPUtil.TAG_SERVER_ADDRESS, ip);

            binding.incEtLineServerAddress.ibBtEditLineEdit.setVisibility(View.VISIBLE);
            binding.incEtLineServerAddress.etEditLineContent.setEnabled(false);
            binding.incEtLineServerAddress.ibBtEditLineEnsure.setVisibility(View.GONE);

        });

        binding.incCheckLineLocalMode.tvCheckLineContent.setText(R.string.check_line_content_local_mode);
        binding.incCheckLineLocalMode.cbLineCheck.setOnClickListener(v -> {
            setWorkMode();
        });

        requestPermission();

        binding.incButtonLineImportCsv.btnInner.setText(R.string.settings_import_from_csv_file);
        binding.incButtonLineImportCsv.btnInner.setOnClickListener(v -> {
            // 选择要导入的文件
            fileType = FileType.CSV;
            chooseFileLauncher.getContract().createIntent(fromActivity, "*/*");
            chooseFileLauncher.launch("*/*");
        });

        binding.incButtonLineExportCsv.btnInner.setText(R.string.settings_export_to_csv_file);
        binding.incButtonLineExportCsv.btnInner.setOnClickListener(v -> {
            // 选择要导出的文件
            fileType = FileType.CSV;
            String fileName = buildFileName(fileType);
            createFilerLauncher.launch(fileName);
        });

        binding.incButtonLineImportJson.btnInner.setText(R.string.settings_import_from_json_file);
        binding.incButtonLineImportJson.btnInner.setOnClickListener(v -> {
            // 选择要导入的文件
            fileType = FileType.JSON;
            chooseFileLauncher.getContract().createIntent(fromActivity, "*/*");
            chooseFileLauncher.launch("*/*");
        });

        binding.incButtonLineExportJson.btnInner.setText(R.string.settings_export_to_json_file);
        binding.incButtonLineExportJson.btnInner.setOnClickListener(v -> {
            // 选择要导出的文件
            fileType = FileType.JSON;
            String fileName = buildFileName(fileType);
            createFilerLauncher.launch(fileName);
        });

    }


    private void requestPermission() {
        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> needPerms = new ArrayList<>();
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(fromActivity, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                needPerms.add(perm);
            }
        }
        if (!needPerms.isEmpty()) {
            ToastUtil.toast(fromActivity, "lack necessary permissions");
            Log.e(TAG, "requestPermission: " + needPerms);
            filePermissionLauncher.launch(needPerms.toArray(new String[0]));
        } else {
            hasFileAccess = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // 方案一：跳转到系统文件访问页面，手动赋予
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + fromActivity.getPackageName()));
                startActivity(intent);
            } else {
                hasFileAccess = true;
            }
        }
    }

    private String buildFileName(FileType fileType) {
        String name = DateUtil.dateFormat(System.currentTimeMillis(), DateUtil.DATE_FORMAT_NUMBER);
        name += fileType.type;
        return name;
    }

    private void doExportToFile(String fullFilePath, String fileName, FileType fileType) {

        List<Entry> resultList = new ArrayList<>();
        EntryService.getInstance(fromActivity).queryAllByBatch(EntryUtil.EXPORT_BATCH_SIZE, new EntryServiceBatchQueryCallback() {
            @Override
            public void onStart(int totalCount) {
                Log.i(TAG, "onStart: totalCount:" + totalCount);
            }

            @Override
            public void onProgress(int curCount, int totalCount, List<Entry> entryList) {
                resultList.addAll(entryList);
                String msg = String.format("onProgress: curCount:%s totalCount:%s", curCount, totalCount);
                Log.i(TAG, msg);
            }

            @Override
            public void onFinish(boolean success, String errMsg) {
                Log.i(TAG, "onFinish: success:" + success + " errMsg:" + errMsg);
                if (success) {
                    ToastUtil.toast(fromActivity, "start to write " + fileType.type + " file:" + fileName);
                    if (fileType == FileType.CSV) {
                        List<String[]> list = EntryUtil.toStringArrayList(resultList, true);
                        FileUtils.writeCSVFile(list, fullFilePath, ',');
                    } else if (fileType == FileType.JSON) {
                        FileUtils.writeJsonFile(resultList, fullFilePath);
                    }
                    ToastUtil.toast(fromActivity, "finish to write " + fileType.type + " file:" + fileName);
                } else {
                    ToastUtil.toast(fromActivity, errMsg);
                }
            }
        });
    }

    private void doImportFromCsvFile(String filePath, FileType fileType) {
        ToastUtil.toast(fromActivity, "import start");
        List<Entry> entryList = new ArrayList<>();
        if (fileType == FileType.CSV) {
            List<String[]> list = FileUtils.readCSVFile(filePath, ',');
            entryList = EntryUtil.fromStringArrayList(list, true);
        } else if (fileType == FileType.JSON) {
            entryList = FileUtils.readJsonFile(filePath, Entry.class);
        }
        EntryService.getInstance(fromActivity).addByBatch(entryList, new EntryServiceCallback<List<Long>>() {
            @Override
            public void onSuccess(List<Long> result) {
                Log.i(TAG, "doImportFromCsvFile onSuccess: " + result);
                ToastUtil.toast(fromActivity, "import finish");
            }

            @Override
            public void onFailure(String errMsg) {
                ToastUtil.toast(fromActivity, errMsg);
            }
        });
    }


    private void resetSettings() {
        String address = OKHttpUtil.server_address;
        binding.incEtLineServerAddress.etEditLineContent.setText(address);
        OKHttpUtil.resetUrl();
    }

    private void setWorkMode() {
        boolean checked = binding.incCheckLineLocalMode.cbLineCheck.isChecked();
        SPUtil.putBoolean(fromActivity.getApplication(), SPUtil.TAG_WORKING_MODE_LOCAL, checked);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onSwitch() {

    }
}
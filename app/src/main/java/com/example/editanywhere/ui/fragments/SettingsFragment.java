package com.example.editanywhere.ui.fragments;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.editanywhere.MainActivity;
import com.example.editanywhere.R;
import com.example.editanywhere.databinding.FragmentSettingsBinding;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.SPUtil;

public class SettingsFragment extends CustomFragment {

    private FragmentSettingsBinding binding;
    private MainActivity fromActivity;

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


    }

    private void resetSettings() {
        String address = OKHttpUtil.server_address;
        binding.incEtLineServerAddress.etEditLineContent.setText(address);
        OKHttpUtil.resetUrl();
    }

    private void setWorkMode() {
        boolean checked = binding.incCheckLineLocalMode.cbLineCheck.isChecked();
        SPUtil.putBoolean(fromActivity.getApplication(), SPUtil.TAG_WORKING_MODE, checked);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onUpdate(Bundle bundle) {

    }

}
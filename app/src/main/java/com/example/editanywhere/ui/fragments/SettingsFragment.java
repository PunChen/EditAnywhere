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

    private FragmentSettingsBinding  binding;
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


    private void initView(){
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> fromActivity.openDrawer());
        toolbar.getMenu().findItem(R.id.menu_settings_reset).setOnMenuItemClickListener(item -> {
            resetSettings();
            return false;
        });
        String address = OKHttpUtil.getUrl();
        binding.etSettingServerAddress.setText(address);
        //ip修改按钮
        binding.ibBtEditServerAddress.setOnClickListener(v -> {
            binding.ibBtEditServerAddress.setVisibility(View.GONE);
            binding.etSettingServerAddress.setEnabled(true);
            binding.ibBtEnsureServerAddress.setVisibility(View.VISIBLE);
        });
        //ip确认按钮
        binding.ibBtEnsureServerAddress.setOnClickListener(v -> {
            // update ip
            Application application = fromActivity.getApplication();
            String ip = binding.etSettingServerAddress.getText().toString();
            SPUtil.putString(application,SPUtil.TAG_SERVER_ADDRESS,ip);
            binding.ibBtEditServerAddress.setVisibility(View.VISIBLE);
            binding.etSettingServerAddress.setEnabled(false);
            binding.ibBtEnsureServerAddress.setVisibility(View.GONE);

        });
    }

    private void resetSettings(){
        String address = OKHttpUtil.server_address;
        binding.etSettingServerAddress.setText(address);
        OKHttpUtil.resetUrl();
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
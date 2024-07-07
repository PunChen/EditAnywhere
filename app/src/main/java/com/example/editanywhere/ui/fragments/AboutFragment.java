package com.example.editanywhere.ui.fragments;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.editanywhere.MainActivity;
import com.example.editanywhere.databinding.FragmentAboutBinding;
import com.example.editanywhere.utils.ProjectInfoUtil;

public class AboutFragment extends CustomFragment {

    private final MainActivity fromActivity;
    private FragmentAboutBinding binding;

    public AboutFragment(MainActivity fromActivity) {
        this.fromActivity = fromActivity;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        return root;
    }

    private void initView() {
        final Toolbar toolbar = binding.tbToolbar;
        toolbar.setNavigationOnClickListener(view -> fromActivity.openDrawer());
        final LinearLayout layout = binding.llUrlContainer;
        layout.removeAllViews();
        for (String key : ProjectInfoUtil.relatedProjectUrl.keySet()) {
            String val = ProjectInfoUtil.relatedProjectUrl.get(key);
            TextView title = new TextView(fromActivity);
            title.setText(key);
            layout.addView(title);
            TextView content = new TextView(fromActivity);
            content.setText(val);
            content.setTextIsSelectable(true);
            content.setAutoLinkMask(Linkify.WEB_URLS);
            layout.addView(content);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onSwitch() {
        initView();
    }
}
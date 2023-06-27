package com.example.editanywhere;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.editanywhere.databinding.ActivityMainBinding;
import com.example.editanywhere.ui.fragments.CustomFragment;
import com.example.editanywhere.ui.fragments.AboutFragment;
import com.example.editanywhere.ui.fragments.EntryListFragment;
import com.example.editanywhere.ui.fragments.SettingsFragment;
import com.example.editanywhere.utils.OKHttpUtil;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private CustomFragment entryListFragment = null;
    private CustomFragment settingsFragment = null;
    private  CustomFragment aboutFragment = null;
    private  CustomFragment[] fragmentArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //okhttp
        OKHttpUtil.init(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 三个全局fragment
        initFragments();
        // 抽屉
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_entry_list){
                switchFragmentWithBundle(null,entryListFragment);
            }else if(id == R.id.nav_settings){
                switchFragmentWithBundle(null,settingsFragment);
            }else if(id == R.id.nav_about){
                switchFragmentWithBundle(null,aboutFragment);
            }
            // 关闭抽屉
            drawer.close();
            return false;
        });

    }

    private void initFragments(){
        entryListFragment = new EntryListFragment(this);
        settingsFragment = new SettingsFragment(this);
        aboutFragment = new AboutFragment(this);
        fragmentArray = new CustomFragment[]{entryListFragment, settingsFragment, aboutFragment};
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.fragment_main,entryListFragment)
                .add(R.id.fragment_main,settingsFragment)
                .add(R.id.fragment_main,aboutFragment)
        .commit();
        switchFragmentWithBundle(null,entryListFragment);
    }

    // 统一的异步线程ui处理
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_ID_TOAST:
                    String toastMsg = msg.getData().getString(MSG_KEY_TOAST_MSG);
                    if(toastMsg != null){
                        Toast.makeText(MainActivity.this, toastMsg,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private static final int MSG_ID_UPDATE_LIST = 1;
    private static final int MSG_ID_TOAST = 2;
    private static final int MSG_ID_SWITCH_FRAGMENT_WITH_BUNDLE = 3;
    private static final String MSG_KEY_TOAST_MSG = "TOAST_MSG";
    public void makeToast(String msg){
        Message message = new Message();
        message.what = MSG_ID_TOAST;
        message.getData().putString(MSG_KEY_TOAST_MSG,msg);
        handler.sendMessage(message);
    }
    //统一的fragment切换
    public void switchFragmentWithBundle(Bundle bundle,final CustomFragment targetFragment){
        FragmentManager manager = getSupportFragmentManager();
        for(Fragment fragment:fragmentArray){
            manager.beginTransaction().hide(fragment).commitNow();
        }
        targetFragment.onUpdate(bundle);
        manager.beginTransaction().show(targetFragment).commit();
    }

    public void openDrawer(){
        if(drawer != null){
            drawer.open();
        }
    }

    public void closeDrawer(){
        if(drawer != null){
            drawer.close();
        }
    }


    /*
    NavController.navigate 底层是删除fragment然后创建再添加的
    todo https://juejin.cn/post/6844903896104747022
    修改为主动切换
     */

}
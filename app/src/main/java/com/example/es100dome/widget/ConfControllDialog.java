package com.example.es100dome.widget;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.es100.MyState;
import com.es100.baserx.RxManager;
import com.example.es100dome.BaseFragmentAdapter;
import com.example.es100dome.R;
import com.example.es100dome.fragment.ConfControllFragment;
import com.example.es100dome.fragment.ConfMemberFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator
 */
public class ConfControllDialog extends DialogFragment {
    private  List<Fragment> fragments = new ArrayList<>();
    private  List<String> names = new ArrayList<>();
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private TabLayout.Tab tab;
    private TabLayout.Tab tab1;
    private RxManager rxManager;
    //    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
//            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_conf_controll, null);
//        bottomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            bottomDialog.setContentView(contentView);
//        Window window = bottomDialog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.RIGHT;
//        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setWindowAnimations(R.style.BottomDialog_Animation1);
//        window.setAttributes(wlp);
//
//        return bottomDialog;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_conf_controll, null);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.bg_dialog)));
        initDialog(contentView);
        return contentView;
    }

    private void initDialog(View view) {
        rxManager = new RxManager();
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_conf_controll);
        names.add("与会成员"+ MyState.getInstance().terminalNum);
        names.add("会议控制");
        tab = tabLayout.newTab();
        tab1 = tabLayout.newTab();
        tab.setText("与会成员"+ MyState.getInstance().terminalNum);
        tab1.setText("会议控制");
        tabLayout.addTab(tab);
        tabLayout.addTab(tab1);
        ConfControllFragment confControllFragment = new ConfControllFragment();
        ConfMemberFragment confMemberFragment = new ConfMemberFragment();
        fragments.add(confMemberFragment);
        fragments.add(confControllFragment);
        mFragmentPagerAdapter = new BaseFragmentAdapter(getChildFragmentManager(),fragments,names);
        viewPager.setAdapter(mFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        rxManager.on(AppConstant.RxAction.TERMINAL_NUM, new Action1<String>() {
//            @Override
//            public void call(String s) {
//                tabLayout.removeTab(tab);
//                tab.setText("与会成员"+s);
//                tabLayout.addTab(tab);
//            }
//        });
    }
    //    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_conf_controll,container);
//        return view;
//    }
//
    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.RIGHT);
        wlp.dimAmount = 0.0f;
//        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.BottomDialog_Animation1);
        window.setAttributes((WindowManager.LayoutParams) wlp);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( getDialog().getWindow().getAttributes().width, dm.heightPixels );
    }
    //        /** 加载数据对话框 */
//    private static Dialog mLoadingDialog;
//    private static List<Fragment> fragments = new ArrayList<>();
//    private static List<String> names = new ArrayList<>();
//    private static FragmentPagerAdapter mFragmentPagerAdapter;
//    /**
//     * 显示加载对话框
//     * @param context 上下文
//     * @param cancelable 对话框是否可以取消
//     */
//    public static Dialog showDialogForLoading(final Activity context, boolean cancelable) {
//        mLoadingDialog = new Dialog(context, R.style.BottomDialog);
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_conf_controll,null);
//        mLoadingDialog.setContentView(view);
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = DisplayUtil.getScreenHeight(context);
//        view.setLayoutParams(layoutParams);
//        mLoadingDialog.getWindow().setGravity(Gravity.RIGHT);
//        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
//        mLoadingDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation1);
//        initDialog(context,view);
//        mLoadingDialog.show();
//        return  mLoadingDialog;
//    }
//
//    private static void initDialog(Activity activity, View view) {
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
//        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_conf_controll);
//        names.add("会议控制");
//        names.add("与会成员");
//        tabLayout.addTab(tabLayout.newTab().setText("会议控制"));
//        tabLayout.addTab(tabLayout.newTab().setText("与会成员"));
//        ConfControllFragment confControllFragment = new ConfControllFragment();
//        ConfMemberFragment confMemberFragment = new ConfMemberFragment();
//        fragments.add(confControllFragment);
//        fragments.add(confMemberFragment);
//        mFragmentPagerAdapter = new BaseFragmentAdapter(MyState.getInstance().supportFragmentManager,fragments,names);
//        viewPager.setAdapter(mFragmentPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    /**
//     * 关闭加载对话框
//     */
//    public static void cancelDialogForLoading() {
//        if(mLoadingDialog != null) {
//            mLoadingDialog.cancel();
//        }
//    }
}

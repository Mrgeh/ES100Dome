package com.example.es100dome.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.es100dome.R;

/**
 * Created by yuhengyi on 2017/1/10.
 */

public class LPGiftView extends RelativeLayout {
    /**
     * 礼物飞进的动画
     */
    private static TranslateAnimation mGiftLayoutInAnim;
    /**
     * icon缩放的动画
     */
    private static ScaleAnimation mIconScaleAnim;
    /**
     * 用户名飞进的动画
     */
    private static TranslateAnimation mUserNameIn;
    /**
     * 礼物名飞进的动画
     */
    private static TranslateAnimation mGiftNameIn;

    private AnimMessage mAnimMessage;
    private View view;

    public LPGiftView(Context context, AnimMessage message) {
        super(context);
        mAnimMessage = message;
        init();
    }

    public LPGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LPGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private  void init(){

        mGiftLayoutInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), com.es100.R.anim.lp_gift_in);
        mIconScaleAnim = (ScaleAnimation) AnimationUtils.loadAnimation(getContext(), com.es100.R.anim.lp_icon_scale);
        mUserNameIn = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), com.es100.R.anim.lp_username_in);
        mGiftNameIn = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), com.es100.R.anim.lp_giftname_in);

        // 外层是线性布局
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_speak_person, this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        TextView tv_speak_name = (TextView) view.findViewById(R.id.tv_speak_name);
        tv_speak_name.setText(mAnimMessage.getUserName()+"举手发言");
        mAnimMessage.setUpdateTime(System.currentTimeMillis());/*设置时间标记*/
        setTag(mAnimMessage);/*设置view标识*/
        mIconScaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }



}

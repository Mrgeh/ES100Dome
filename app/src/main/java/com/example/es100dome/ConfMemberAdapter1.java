package com.example.es100dome;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.es100.MyState;
import com.es100.app.ApiConstant;
import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.es100.entity.ConfTerminalList;
import com.es100.entity.EpId;
import com.es100.entity.MeetingControlParam;
import com.es100.util.StringUtil;
import com.es100.util.ToastUitl;
import com.example.es100dome.widget.ExpandableLayout;
import com.ifreecomm.debug.MLog;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class ConfMemberAdapter1 extends RecyclerView.Adapter<ConfMemberAdapter1.MyViewHolder> {
    private List<ConfTerminalList.ConfTerminal> confMemberList;
    private Context context;
    public static final String TAG = "ConfMemberAdapter1";
    public String s = "";
    public ConfMemberAdapter1(List<ConfTerminalList.ConfTerminal> confMemberList, Context context) {
        this.confMemberList = confMemberList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_conf_member, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ConfTerminalList.ConfTerminal confTerminal = confMemberList.get(position);
        if(!StringUtil.isEmpty(confTerminal.e164)){
            if(confTerminal.e164.equals(MyState.getInstance().E164)){
                holder.tv_name.setText(confTerminal.name+"(我)");
            }else{
                holder.tv_name.setText(confTerminal.name);
            }
        }else{
            holder.tv_name.setText(confTerminal.name);
        }
        GradientDrawable gradientDrawable = (GradientDrawable) holder.rl_bg.getBackground();
        if(confTerminal.type.equals("ConfSite")){
            holder.tv_key_words.setText(context.getResources().getString(R.string.icon_meeting));
        }else{
            holder.tv_key_words.setText(confTerminal.name.substring(confTerminal.name.length() - 1, confTerminal.name.length()));
        }
        if(MyState.getInstance().isHost){
            holder.rl_set_chair.setVisibility(View.VISIBLE);
            holder.ll_set_chair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.tv_set_chair_text.getText().equals("设为主讲")){
                       RxBus.getInstance().post(AppConstant.RxAction.SET_CHAIR,new MeetingControlParam(confTerminal.epId,holder));
                    }else{
                        RxBus.getInstance().post(AppConstant.RxAction.CANCEL_CHAIR,new MeetingControlParam(confTerminal.epId,holder));
                    }
                }
            });
        }else{
            holder.rl_set_chair.setVisibility(View.GONE);
        }
        if((confTerminal.status & 0x1) == 0x1 ){
            //在线
            MLog.e(TAG,"已入会"+confTerminal.name+"----"+confTerminal.status);
            switch (position%5){
                case 0:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.color_loop1));
                    break;
                case 1:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.color_loop2));
                    break;
                case 2:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.color_loop3));
                    break;
                case 3:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.color_loop4));
                    break;
                case 4:
                    gradientDrawable.setColor(context.getResources().getColor(R.color.color_loop5));
                    break;
                default:
                    break;
            }
            holder.tv_up_down.setVisibility(View.VISIBLE);
            if(confTerminal.applying){
                holder.tv_apply_speak.setVisibility(View.VISIBLE);
            }else{
                holder.tv_apply_speak.setVisibility(View.GONE);
            }
        }else{
            MLog.e(TAG,"未入会"+confTerminal.name);
            holder.tv_apply_speak.setVisibility(View.GONE);
            gradientDrawable.setColor(context.getResources().getColor(R.color.offline));
            holder.tv_up_down.setVisibility(View.GONE);
        }
        if(confTerminal.isExpand){
            holder.expandableLayout.setExpanded(true,false);
            holder.tv_up_down.setText(R.string.icon_up);
        }else{
            holder.expandableLayout.setExpanded(false,false);
            holder.tv_up_down.setText(R.string.icon_down);
        }
        if(((confTerminal.status & 0x20000) == 0x20000)){
            confTerminal.isSpeaker = true;
        }else{
            confTerminal.isSpeaker = false;
        }
        holder.tv_apply_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //允许发言
                RxBus.getInstance().post(AppConstant.RxAction.ALLOW_SPEAK,new MeetingControlParam(MyState.getInstance().confId,new EpId(confTerminal.epId)));
            }
        });
        holder.tv_cancel_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消发言
                RxBus.getInstance().post(AppConstant.RxAction.CANCEL_SPEAK,new MeetingControlParam(MyState.getInstance().confId,new EpId(confTerminal.epId)));
            }
        });
        holder.ll_root.setOnClickListener(v -> {
            if((confTerminal.status & 0x1) == 0x1){
                if(holder.expandableLayout.isExpanded()){
                    holder.expandableLayout.collapse();
                    confTerminal.isExpand = false;
                    holder.tv_up_down.setText(R.string.icon_down);
                }else{
                    confTerminal.isExpand = true;
                    holder.expandableLayout.expand();
                    holder.tv_up_down.setText(R.string.icon_up);
                }
            }

        });
        if((confTerminal.status & 0x1) == 0x1) {
            holder.ll_status.setVisibility(View.VISIBLE);
            if (confTerminal.hostman) {
                s = "主持人";
            } else {
                s = "";
            }
            if((confTerminal.status & 0x2) == 0x2){
                if(!StringUtil.isEmpty(s)){
                    s = s+"/主讲人";
                }else{
                    s = "主讲人";
                }
            }
            if((confTerminal.status & 0x200000) == 0x200000){
                if(!StringUtil.isEmpty(s)){
                    s = s+"/辅流";
                }else{
                    s = "辅流";
                }
            }
            if(((confTerminal.status & 0x4) == 0x4)){
                if(confTerminal.isSpeaker){
                    holder.tv_cancel_speak.setVisibility(View.VISIBLE);
                }
                if(!StringUtil.isEmpty(s)){
                    s = s+"/发言";
                }else{
                    s = "发言";
                }
            }else{
                holder.tv_cancel_speak.setVisibility(View.INVISIBLE);
            }
            if(((confTerminal.status & 0x8) == 0x8)){
                if(!StringUtil.isEmpty(s)){
                    s = s+"/广播";
                }else{
                    s = "广播";
                }
            }
            if(((confTerminal.status & 0x80) == 0x80)){
                if(!StringUtil.isEmpty(s)){
                    s = s+"/闭音";
                }else{
                    s = "闭音";
                }
            }
            holder.tv_text.setText(s);
        }else{
            holder.tv_cancel_speak.setVisibility(View.GONE);
            holder.ll_status.setVisibility(View.GONE);
        }
        if((confTerminal.status & 0x80) == 0x80){
            //闭音状态
            holder.tv_mute.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_mute.setText(R.string.icon_close_mic);
            holder.tv_mute_text.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_mute_text.setText("取消闭音");
        }else{
            holder.tv_mute.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_mute.setText(R.string.icon_mic);
            holder.tv_mute_text.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_mute_text.setText("闭音");
        }
        if((confTerminal.status & 0x2) == 0x2){
            holder.tv_set_chair.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_set_chair.setText(R.string.icon_speaker_ban);
            holder.tv_set_chair_text.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_set_chair_text.setText("剥夺主讲");
        }else{
            holder.tv_set_chair.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_set_chair.setText(R.string.icon_speaker);
            holder.tv_set_chair_text.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_set_chair_text.setText("设为主讲");
        }
//        if((confTerminal.status & 0x8) == 0x8){
//            //广播状态
//            holder.tv_broadcast.setTextColor(context.getResources().getColor(R.color.main_blue));
//            holder.tv_broadcast_text.setTextColor(context.getResources().getColor(R.color.main_blue));
//            holder.tv_broadcast_text.setText("取消广播");
//        }else{
//            holder.tv_broadcast.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tv_broadcast_text.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tv_broadcast_text.setText("广播");
//        }
        if(confTerminal.speaker&&confTerminal.broadcast){
            holder.tv_speak.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_speak_text.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.tv_speak_text.setText("取消点名");
        }else {
            holder.tv_speak.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_speak_text.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_speak_text.setText("点名发言");
        }
        EpId epId = new EpId(confTerminal.epId);
        holder.ll_mute.setOnClickListener(v -> {
            MLog.e(TAG,"文字为"+holder.tv_mute_text.getText().toString());
            if(holder.tv_mute_text.getText().toString().equals("闭音")){
                //闭音操作
                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId, ApiConstant.Mute,epId,holder));
            }else{
                //取消闭音操作
                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId, ApiConstant.Demute,epId,holder));
            }

        });
//        holder.ll_broadcast.setOnClickListener(v -> {
//            MLog.e(TAG,"文字为"+holder.tv_broadcast_text.getText().toString());
//            if(holder.tv_broadcast_text.getText().toString().equals("广播")){
//                MLog.e(TAG,"广播会场");
//                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId,ApiConstant.BroadcastConventioner,epId,holder));
//            }else{
//                MLog.e(TAG,"广播多画面");
//                MyState.getInstance().cancelType = 0;
//                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId,ApiConstant.BroadcastSplitScreen,epId,holder));
//            }
//        });
        holder.ll_speak.setOnClickListener(v -> {
            if((confTerminal.status & 0x2) == 0x2){
                ToastUitl.showShort("不支持当前操作");
                return;
            }
            if(holder.tv_speak_text.getText().toString().equals("点名发言")){
//                confTerminal.isSpeaker = true;
                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId,ApiConstant.SetSpeakSite,epId,holder,confTerminal));
            }else{
//                confTerminal.isSpeaker = false;
                MyState.getInstance().cancelType = 1;
                RxBus.getInstance().post(AppConstant.RxAction.CONF_CONTROL,new MeetingControlParam(MyState.getInstance().confId,ApiConstant.CancelSpeakSite,epId,holder,confTerminal));
            }
        });

    }

    @Override
    public int getItemCount() {
        return confMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       public TextView tv_name;
        public TextView tv_up_down;
        public  LinearLayout ll_root;
        public LinearLayout ll_mute;
//        public LinearLayout ll_broadcast;
        public LinearLayout ll_speak;
        public ExpandableLayout expandableLayout;
        public TextView tv_mute;
        public TextView tv_mute_text;
//        public TextView tv_broadcast;
//        public TextView tv_broadcast_text;
        public TextView tv_speak;
        public TextView tv_speak_text;
        public RelativeLayout rl_bg;
        public TextView tv_key_words;
        public TextView tv_apply_speak;
//        public TextView tv_speaker;
//        public TextView tv_host_man;
//        public TextView tv_chairman_status;
//        public TextView tv_mute_status;
//        public TextView tv_aux_status;
//        public TextView tv_speak_status;
//        public TextView tv_broadcast_status;
        public TextView tv_cancel_speak;
        public LinearLayout ll_status;
        public RelativeLayout rl_set_chair;
        public LinearLayout ll_set_chair;
        public TextView tv_set_chair;
        public TextView tv_set_chair_text;

        public TextView tv_text;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_up_down = (TextView) itemView.findViewById(R.id.tv_up_down);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expand_control);
            ll_mute = (LinearLayout) itemView.findViewById(R.id.ll_mute);
            tv_mute = (TextView) itemView.findViewById(R.id.tv_mute);
            tv_mute_text = (TextView) itemView.findViewById(R.id.tv_mute_text);
//            ll_broadcast = (LinearLayout) itemView.findViewById(R.id.ll_broadcast);
//            tv_broadcast = (TextView) itemView.findViewById(R.id.tv_broadcast);
//            tv_broadcast_text = (TextView) itemView.findViewById(R.id.tv_broadcast_text);
            ll_speak = (LinearLayout) itemView.findViewById(R.id.ll_speak);
            tv_speak = (TextView) itemView.findViewById(R.id.tv_speak);
            tv_speak_text = (TextView) itemView.findViewById(R.id.tv_speak_text);
            rl_bg = (RelativeLayout) itemView.findViewById(R.id.rl_bg);
            tv_key_words = (TextView) itemView.findViewById(R.id.tv_key_words);
//            tv_speaker = (TextView) itemView.findViewById(R.id.tv_speaker);
            tv_apply_speak = (TextView) itemView.findViewById(R.id.tv_apply_speak);
//            tv_chairman_status = (TextView) itemView.findViewById(R.id.tv_chairman_status);
//            tv_mute_status = (TextView) itemView.findViewById(R.id.tv_mute_status);
//            tv_aux_status = (TextView) itemView.findViewById(R.id.tv_aux_status);
//            tv_speak_status = (TextView) itemView.findViewById(R.id.tv_speak_status);
//            tv_broadcast_status = (TextView) itemView.findViewById(R.id.tv_broadcast_status);
//            tv_host_man = (TextView) itemView.findViewById(R.id.tv_host_man);
            tv_cancel_speak = (TextView) itemView.findViewById(R.id.tv_cancel_speak);
            ll_status = (LinearLayout) itemView.findViewById(R.id.ll_status);
            rl_set_chair = (RelativeLayout) itemView.findViewById(R.id.rl_set_chair);
            ll_set_chair = (LinearLayout) itemView.findViewById(R.id.ll_set_chair);
            tv_set_chair = (TextView) itemView.findViewById(R.id.tv_set_chair);
            tv_set_chair_text = (TextView) itemView.findViewById(R.id.tv_set_chair_text);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}

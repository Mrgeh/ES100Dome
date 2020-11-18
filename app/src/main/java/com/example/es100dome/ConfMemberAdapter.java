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
import com.es100.entity.ConfTerminalList;
import com.es100.util.StringUtil;
import com.ifreecomm.debug.MLog;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class ConfMemberAdapter extends RecyclerView.Adapter<ConfMemberAdapter.MyViewHolder> {
    private List<ConfTerminalList.ConfTerminal> confMemberList;
    private Context context;
    private String s = "";
    public static final String TAG = "ConfMemberAdapter";
    public ConfMemberAdapter(List<ConfTerminalList.ConfTerminal> confMemberList, Context context) {
        this.confMemberList = confMemberList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.normal_conf_member, null));
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
//        stringBuffer.append()
        GradientDrawable gradientDrawable = (GradientDrawable) holder.rl_bg.getBackground();
        if(confTerminal.type.equals("ConfSite")){
            holder.tv_key_words.setText(context.getResources().getString(R.string.icon_meeting));
        }else{
            holder.tv_key_words.setText(confTerminal.name.substring(confTerminal.name.length() - 1, confTerminal.name.length()));
        }
        if((confTerminal.status & 0x1) == 0x1 ){
            //在线
            MLog.e(TAG,"已入会"+confTerminal.name);
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

        }else{
            gradientDrawable.setColor(context.getResources().getColor(R.color.offline));
        }
        if((confTerminal.status & 0x1) == 0x1){
            holder.ll_status.setVisibility(View.VISIBLE);
            if(confTerminal.hostman){
                s = "主持人";
            }else{
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
                if(!StringUtil.isEmpty(s)){
                    s = s+"/发言";
                }else{
                    s = "发言";
                }
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
//            if(confTerminal.hostman){
//                MLog.e(TAG,"主持的e64"+confTerminal.e164);
//                holder.tv_host.setVisibility(View.VISIBLE);
//            }else {
//                holder.tv_host.setVisibility(View.GONE);
//            }
//            if((confTerminal.status & 0x2) == 0x2){
//                holder.tv_chairman.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_chairman.setVisibility(View.GONE);
//            }
//            if((confTerminal.status & 0x200000) == 0x200000){
//                holder.tv_aux.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_aux.setVisibility(View.GONE);
//            }
//            if(((confTerminal.status & 0x20000) == 0x20000)){
//                holder.tv_speak.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_speak.setVisibility(View.GONE);
//            }
//            if((confTerminal.status & 0x8) == 0x8){
//                holder.tv_broadcast.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_broadcast.setVisibility(View.GONE);
//            }
//            if((confTerminal.status & 0x80) == 0x80){
//                holder.tv_mute.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_mute.setVisibility(View.GONE);
//            }
        }else{
            holder.ll_status.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return confMemberList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        RelativeLayout rl_bg;
        TextView tv_key_words;
//        TextView tv_chairman;
//        TextView tv_aux;
//        TextView tv_speak;
//        TextView tv_broadcast;
//        TextView tv_mute;
//        TextView tv_host;
        TextView tv_text;
        LinearLayout ll_status;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            rl_bg = (RelativeLayout) itemView.findViewById(R.id.rl_bg);
            tv_key_words = (TextView) itemView.findViewById(R.id.tv_key_words);
//            tv_chairman = (TextView) itemView.findViewById(R.id.tv_chairman);
//            tv_aux = (TextView) itemView.findViewById(R.id.tv_aux);
//            tv_speak = (TextView) itemView.findViewById(R.id.tv_speak);
//            tv_broadcast = (TextView) itemView.findViewById(R.id.tv_broadcast);
//            tv_mute = (TextView) itemView.findViewById(R.id.tv_mute);
//            tv_host = (TextView) itemView.findViewById(R.id.tv_host);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
            ll_status = (LinearLayout) itemView.findViewById(R.id.ll_status);
        }
    }
}

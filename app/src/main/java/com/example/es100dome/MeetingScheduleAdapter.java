package com.example.es100dome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es100.baserx.RxBus;
import com.es100.entity.Confrence;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.util.StringUtil;
import com.es100.util.TimeUtil;
import com.es100.util.ToastUitl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class MeetingScheduleAdapter extends RecyclerView.Adapter<MeetingScheduleAdapter.MyViewHolder> {
    private List<MeetingScheduleListEntity.MeetingSchedule> normalList;
    private Context context;

    public MeetingScheduleAdapter(List<MeetingScheduleListEntity.MeetingSchedule> normalList, Context context) {
        this.normalList = new ArrayList<>(normalList);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_meeting_schedule, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MeetingScheduleListEntity.MeetingSchedule meetingSchedule = normalList.get(position);
        holder.tvMeetingName.setText(meetingSchedule.name+"("+meetingSchedule.confCode+")");
        if (meetingSchedule.status.equals("WaitingForConvening")) {
            holder.tvStatus.setText("等待召开");
            holder.tvStatus.setTextColor(context.getResources().getColor(com.es100.R.color.color_wait_meeting));
        } else if (meetingSchedule.status.equals("InMeeting")) {
            holder.tvStatus.setText("正在召开");
            holder.tvStatus.setTextColor(context.getResources().getColor(com.es100.R.color.color_in_meeting));
        }
//        holder.tv_meeting_detail.setOnClickListener(v -> {
//            Intent intent = new Intent(context, MeetingDetailActivity.class);
//            intent.putExtra("MeetingDetail",meetingSchedule);
//            intent.putExtra("chairmanTerminalId",meetingSchedule.chairmanTerminalId);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });
        if(!StringUtil.isEmpty(meetingSchedule.startAt)){
            String s = meetingSchedule.startAt.split("\\.")[0];
            String t = s.replace("T", " ");
            long l = TimeUtil.getDatelongMills(TimeUtil.dateFormatYMDHMS, t) + (8 * 60 * 60 * 1000);
            String s1 = TimeUtil.formatData(TimeUtil.dateFormatMDHM, l);
            String s2 = TimeUtil.formatData(TimeUtil.dateFormatMDHM, l+ Long.parseLong(meetingSchedule.duration)*1000);
            if(TimeUtil.isToday(l)){
                holder.tv_meeting_time.setText("今天 "+s1.split(" ")[1]+" "+ TimeUtil.formatDuring(Long.parseLong(meetingSchedule.duration)*1000));
            }else{
                s1 = TimeUtil.formatData(TimeUtil.dateFormatYMDHMS, l);
                holder.tv_meeting_time.setText(s1+" "+ TimeUtil.formatDuring(Long.parseLong(meetingSchedule.duration)*1000));
            }
        }
        //加入会议
        holder.ll_root.setOnClickListener((View v) -> {
            if (meetingSchedule.status.equals("WaitingForConvening")) {
                ToastUitl.showShort("会议尚未正式召开");
            } else if (meetingSchedule.status.equals("InMeeting")) {
                RxBus.getInstance().post("JoinMeeting", new Confrence(meetingSchedule.confCode, meetingSchedule.name, meetingSchedule.id));
            }

        });
    }

    @Override
    public int getItemCount() {
        return normalList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetingName;
        TextView tvStatus;
        TextView tv_meeting_detail;
        TextView tv_meeting_time;
        LinearLayout ll_root;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvMeetingName = (TextView) itemView.findViewById(R.id.tv_meeting_name);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_meeting_status);
            tv_meeting_detail = (TextView) itemView.findViewById(R.id.tv_meeting_detail);
            tv_meeting_time = (TextView) itemView.findViewById(R.id.tv_meeting_time);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }
    }
    public void upDateList(List<MeetingScheduleListEntity.MeetingSchedule> normalList){
        this.normalList.clear();
        this.normalList.addAll(normalList);
        notifyDataSetChanged();
    }
}

package com.example.sstep.commute;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sstep.R;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;


public class Dispute_RecyclerViewAdpater extends RecyclerView.Adapter<Dispute_RecyclerViewAdpater.ViewHolder> {
    private Context context;
    private static OnItemClickListener onItemClickListener;

    public static void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView disputeDateTv, staffNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            disputeDateTv = (TextView) itemView.findViewById(R.id.disputelist_recycle_disputeDateTv);
            staffNameTv = (TextView) itemView.findViewById(R.id.disputelist_recycle_staffNameTv);
        }
    }

    private List<Dispute_recyclerViewItem> mList = null;

    public Dispute_RecyclerViewAdpater(List<Dispute_recyclerViewItem> mList) {
        this.mList = mList;
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.disputelist_recycle_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // 요일을 문자열로 변환하는 메서드
    private String convertDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                return dayOfWeek.toString(); // 다른 경우에는 열거형 이름을 반환
        }
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dispute_recyclerViewItem item = mList.get(position);

        String dayOfWeekStr = convertDayOfWeek(item.getDayOfWeek());
        holder.disputeDateTv.setText(item.getCommuteDate() + " (" + dayOfWeekStr + ")");
        holder.staffNameTv.setText(item.getStaffName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
package com.uni.treest.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uni.treest.OnTerminusClick;
import com.uni.treest.R;
import com.uni.treest.models.Line;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Line> lineList = new ArrayList<>();
    private OnTerminusClick onTerminusClick;

    public MainAdapter(OnTerminusClick onTerminusClick) {
        this.onTerminusClick = onTerminusClick;
    }

    public void setLines(List<Line> allLines){
        lineList = new ArrayList<>(allLines);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_main_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String direction1 = lineList.get(position).getLineName1();
        String direction2 = lineList.get(position).getLineName2();
        holder.terminus1.setText(direction1);
        holder.terminus2.setText(direction2);
        holder.mainLine.setText(lineList.get(position).getLines());

        holder.selectButton1.setOnClickListener(v -> onTerminusClick.onTerminusClick(lineList.get(holder.getAdapterPosition()).getTerminus1().getDid()
                , lineList.get(holder.getAdapterPosition()).getTerminus2().getDid(),
                direction1, direction2));
        holder.selectButton2.setOnClickListener(v -> onTerminusClick.onTerminusClick(lineList.get(holder.getAdapterPosition()).getTerminus2().getDid()
                , lineList.get(holder.getAdapterPosition()).getTerminus1().getDid(),
                direction2, direction1));
    }

    @Override
    public int getItemCount() {
        return lineList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView terminus1, terminus2, mainLine;
        private Button selectButton1, selectButton2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLine = itemView.findViewById(R.id.recyclerMainLine);
            terminus1 = itemView.findViewById(R.id.recyclerMainTerminus1);
            terminus2 = itemView.findViewById(R.id.recyclerMainTerminus2);
            selectButton1 = itemView.findViewById(R.id.recyclerMainSelectT1);
            selectButton2 = itemView.findViewById(R.id.recyclerMainSelectT2);

        }
    }
}

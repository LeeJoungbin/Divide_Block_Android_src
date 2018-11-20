package com.pnu.cse.termspring2018;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Formatter;

public class ScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ScoreModel> list = new ArrayList<>();

    public ScoreAdapter(ArrayList<ScoreModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.score_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView t = holder.itemView.findViewById(R.id.user_name);
        //TextView t2 = holder.itemView.findViewById(R.id.user_score);
        int realPosition = position + 1;
        String line = String.format(" No. %-6s %12s %-8s",
                        Integer.toString(realPosition),
                        list.get(position).name,
                        Integer.toString(list.get(position).score));
        t.setText(line);
        //t.setText(realPosition + " | " + list.get(position).name + " | " + String.valueOf(list.get(position).score));
        //t2.setText("스코어 = " +String.valueOf(list.get(position).score));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ScoreHolder extends RecyclerView.ViewHolder {
        public ScoreHolder(View itemView) {
            super(itemView);
        }
    }
}

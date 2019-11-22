package io.github.shivams112.squarenews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.shivams112.squarenews.R;
import io.github.shivams112.squarenews.model.Sources;

import java.util.ArrayList;

/**
 * Created by Shivam
 */

public class TopSourcesAdapter extends RecyclerView.Adapter<TopSourcesAdapter.TopSourceViewHolder> {
    private ArrayList<Sources> mSourcesArrayList;
    private Context mContext;
    private static CheckBox mLastChecked = null;
    private static int mLastCheckedPos = 0;

    public TopSourcesAdapter(ArrayList<Sources> mSourcesArrayList, Context mContext) {
        this.mSourcesArrayList = mSourcesArrayList;
        this.mContext = mContext;
    }

    @Override
    public TopSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View sourceItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_source_item,parent,false);
        return new TopSourceViewHolder(sourceItemView);
    }

    @Override
    public void onBindViewHolder(final TopSourceViewHolder holder, int position) {
        final Sources sourcesObj = mSourcesArrayList.get(position);
        String sourceId = sourcesObj.getSourceID();
        if(sourceId.equals("google-news-in")){
            holder.ivSourceImage.setImageResource(R.drawable.ic_googlenews);
            holder.tvSourceName.setText(sourcesObj.getSourceName());
        }else if(sourceId.equals("the-hindu")){
            holder.ivSourceImage.setImageResource(R.drawable.ic_thehindu);
            holder.tvSourceName.setText(sourcesObj.getSourceName());
        }else if(sourceId.equals("the-times-of-india")){
            holder.ivSourceImage.setImageResource(R.drawable.ic_timesofindia);
            holder.tvSourceName.setText(sourcesObj.getSourceName());
        }else{
            holder.ivSourceImage.setImageResource(R.drawable.ic_news_default);
            holder.tvSourceName.setText(sourcesObj.getSourceName());
        }

        holder.rbSelected.setChecked(sourcesObj.isChecked());
        holder.rbSelected.setTag(new Integer(position));
        if(position == 0 && mSourcesArrayList.get(0).isChecked() && holder.rbSelected.isChecked())
        {
            mLastChecked = holder.rbSelected;
            mLastCheckedPos = 0;
        }


        holder.rbSelected.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox)v;
                int clickedPos = ((Integer)cb.getTag()).intValue();

                if(cb.isChecked())
                {
                    if(mLastChecked != null)
                    {
                        mLastChecked.setChecked(false);
                        mSourcesArrayList.get(mLastCheckedPos).setChecked(false);
                    }

                    mLastChecked = cb;
                    mLastCheckedPos = clickedPos;
                }
                else
                    mLastChecked = null;

                mSourcesArrayList.get(clickedPos).setChecked(cb.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSourcesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class TopSourceViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivSourceImage;
        private TextView tvSourceName;
        private CheckBox rbSelected;
        public TopSourceViewHolder(View itemView) {
            super(itemView);
            ivSourceImage = itemView.findViewById(R.id.source_img);
            tvSourceName = itemView.findViewById(R.id.source_name);
            rbSelected = itemView.findViewById(R.id.is_checked_rb);
        }
    }
}

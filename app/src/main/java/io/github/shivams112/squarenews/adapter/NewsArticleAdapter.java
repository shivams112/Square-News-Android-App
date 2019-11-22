package io.github.shivams112.squarenews.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import io.github.shivams112.squarenews.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.shivams112.squarenews.R;
import io.github.shivams112.squarenews.model.NewsArticle;
import io.github.shivams112.squarenews.view.NewsViewActivity;

/**
 * Created by Shivam
 */

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.ArticleViewHolder> {
    private Context mContext;
    private ArrayList<NewsArticle> mNewsArticleArrayList;
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public NewsArticleAdapter(Context mContext, ArrayList<NewsArticle> mNewsArticleArrayList) {
        this.mContext = mContext;
        this.mNewsArticleArrayList = mNewsArticleArrayList;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_article_item,parent,false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        final NewsArticle newsArticle = mNewsArticleArrayList.get(position);
        holder.tvArticleTitle.setText(newsArticle.getArticleTitle());
        holder.tvDescription.setText(newsArticle.getDescription());
        holder.tvPublishedAt.setText(convertDate(newsArticle.getPublishedDate()));
       /* try {
            input.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = input.parse(newsArticle.getPublishedDate()) ;
            holder.tvPublishedAt.setText(output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvPublishedAt.setText(newsArticle.getPublishedDate());
        }*/
        Glide.with(mContext)
                .load(newsArticle.getUrlToImage())
                .thumbnail(0.1f)
                .into(holder.ivArticleImage);

        holder.llytContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsArticle.getNewsURL();
                mContext.startActivity(new Intent(mContext, NewsViewActivity.class).putExtra("news_url",newsArticle.getNewsURL()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNewsArticleArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDescription;
        private TextView tvArticleTitle;
        private TextView tvPublishedAt;
        private ImageView ivArticleImage;
        private LinearLayout llytContainerView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_news_headline);
            tvArticleTitle = itemView.findViewById(R.id.tv_news_header);
            ivArticleImage = itemView.findViewById(R.id.iv_news_image);
            tvPublishedAt = itemView.findViewById(R.id.tv_news_time);
            llytContainerView = itemView.findViewById(R.id.container_lyt);
        }
    }

    private String convertDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {

            Date date = formatter.parse(dateInString.replaceAll("Z$", "+0000"));
            return output.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInString;
    }
}

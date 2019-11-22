package io.github.shivams112.squarenews.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import io.github.shivams112.squarenews.R;
import io.github.shivams112.squarenews.adapter.NewsArticleAdapter;
import io.github.shivams112.squarenews.model.NewsArticle;
import io.github.shivams112.squarenews.utils.AppUtils;
import io.github.shivams112.squarenews.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.android.volley.Request.Method.GET;

public class CategoryWiseNewsActivity extends AppCompatActivity {

    private static final String LOG_TAG = CategoryWiseNewsActivity.class.getSimpleName();
    private Context mContext;
    private ArrayList<NewsArticle> newsArticleArrayList;
    private NewsArticleAdapter mNewsArticleAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_articles);
        mContext = this;
        String category = getIntent().getStringExtra("category");
        newsArticleArrayList = new ArrayList<>();
        initUI(category);
        if(AppUtils.isInternetConnected(mContext)){
            getTopHeadlinesFromACategory(category);
        }else{
            Toast.makeText(mContext,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    private void initUI(String newsCategory){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(capitalize(newsCategory)+" News");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView rc_viewArticles = findViewById(R.id.articles_rcv);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        rc_viewArticles.setLayoutManager(layoutManager);
        mNewsArticleAdapter = new NewsArticleAdapter(mContext,newsArticleArrayList);
        rc_viewArticles.setAdapter(mNewsArticleAdapter);
    }

    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    private void getTopHeadlinesFromACategory(String category){
        StringRequest stringRequest = new StringRequest(GET, Constants.BASE_URL+"top-headlines?country=in&category="+category + Constants.API_KEY_STR + Constants.API_KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG,"onResponse() : "+response);
                mProgressBar.setVisibility(View.GONE);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.getString("status").equals("ok")){
                        JSONArray sourcesArray = responseObj.getJSONArray("articles");
                        if(sourcesArray!=null && sourcesArray.length() > 0){
                            newsArticleArrayList.clear();
                            for(int i=0; i< sourcesArray.length(); i++){
                                JSONObject sourceContent = sourcesArray.getJSONObject(i);
                                NewsArticle newsArticleObj = new NewsArticle();
                                newsArticleObj.setAuthor(sourceContent.getString("author"));
                                newsArticleObj.setArticleTitle(sourceContent.getString("title"));
                                newsArticleObj.setDescription(sourceContent.getString("description"));
                                newsArticleObj.setNewsURL(sourceContent.getString("url"));
                                newsArticleObj.setUrlToImage(sourceContent.getString("urlToImage"));
                                newsArticleObj.setPublishedDate(sourceContent.getString("publishedAt"));
                                newsArticleObj.setContent(sourceContent.getString("content"));
                                newsArticleArrayList.add(newsArticleObj);
                            }
                            mNewsArticleAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(mContext,"No Headlines Available",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG,"onErrorResponse() : "+error);
                mProgressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headerParams = new HashMap<>();
                headerParams.put("x-api-key",Constants.API_KEY);
                return headerParams;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}

package io.github.shivams112.squarenews.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.github.shivams112.squarenews.R;
import io.github.shivams112.squarenews.adapter.TopSourcesAdapter;
import io.github.shivams112.squarenews.model.Sources;
import io.github.shivams112.squarenews.utils.AppUtils;
import io.github.shivams112.squarenews.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class TopSourceScreen extends AppCompatActivity {

    private static final String LOG_TAG = TopSourceScreen.class.getSimpleName();
    private TopSourcesAdapter mTopSourceAdapter;
    private ArrayList<Sources> mSourcesArrayList;
    private ProgressBar mProgressBar;
    private TextView tvFooterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_source_screen);
        mSourcesArrayList = new ArrayList<>();
        initUI();
    }


    private void initUI(){
        tvFooterText = findViewById(R.id.footer_text);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        RecyclerView rcviewTopSources = findViewById(R.id.top_news_sources);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rcviewTopSources.setLayoutManager(gridLayoutManager);
        mTopSourceAdapter = new TopSourcesAdapter(mSourcesArrayList,TopSourceScreen.this);
        rcviewTopSources.setAdapter(mTopSourceAdapter);


        ImageButton imgBtnProceed = findViewById(R.id.done_button);
        imgBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getSlelectedItem().equals("")){
                    startActivity(new Intent(TopSourceScreen.this,HomeScreen.class).putExtra("source",getSlelectedItem()));
                    finish();
                }else{
                    Toast.makeText(TopSourceScreen.this,"Please select a source first",Toast.LENGTH_LONG).show();
                }

            }
        });

        if(AppUtils.isInternetConnected(this)){
            tvFooterText.setText("Select a source and stay tuned");
            getNewsFromSource();
        }else{
            mProgressBar.setVisibility(View.GONE);
            tvFooterText.setText("You are not connected to Internet.");
            Toast.makeText(this,"You are not connected to internet!",Toast.LENGTH_LONG).show();
        }
    }

    private String getSlelectedItem(){
        String selectedSourceID = "";
        if(mSourcesArrayList!=null  && mSourcesArrayList.size()>0){
            for(int i=0; i< mSourcesArrayList.size(); i++){
                if(mSourcesArrayList.get(i).isChecked()){
                    selectedSourceID = mSourcesArrayList.get(i).getSourceID();
                    return selectedSourceID;
                }
            }
        }
        return selectedSourceID;
    }

    private void getNewsFromSource(){
        StringRequest stringRequest = new StringRequest(GET, Constants.BASE_URL + Constants.LANGUAGE + Constants.API_KEY_STR + Constants.API_KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG,"onResponse() : "+response);
                mProgressBar.setVisibility(View.GONE);
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.getString("status").equals("ok")){
                        JSONArray sourcesArray = responseObj.getJSONArray("sources");
                        if(sourcesArray!=null && sourcesArray.length()>0){
                            mSourcesArrayList.clear();
                            for(int i=0; i< sourcesArray.length(); i++){
                                JSONObject sourceObj = sourcesArray.getJSONObject(i);
                                Sources sources = new Sources();
                                sources.setSourceID(sourceObj.getString("id"));
                                sources.setSourceName(sourceObj.getString("name"));
                                sources.setCategory(sourceObj.getString("category"));
                                mSourcesArrayList.add(sources);
                            }
                            mTopSourceAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(TopSourceScreen.this,"No source found",Toast.LENGTH_LONG).show();
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
                tvFooterText.setText("We are facing some issue, please try later!");
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
}

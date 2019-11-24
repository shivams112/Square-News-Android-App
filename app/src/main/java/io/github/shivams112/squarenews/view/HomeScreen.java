package io.github.shivams112.squarenews.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.shivams112.squarenews.adapter.NewsArticleAdapter;
import io.github.shivams112.squarenews.model.NewsArticle;
import io.github.shivams112.squarenews.utils.AppUtils;
import io.github.shivams112.squarenews.utils.Constants;

import static com.android.volley.Request.Method.GET;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LocationListener {
    private static final String LOG_TAG = HomeScreen.class.getSimpleName();
    private Context mContext;
    private ArrayList<NewsArticle> newsArticleArrayList;
    private NewsArticleAdapter mNewsArticleAdapter;
    private ProgressBar mProgressBar;
    private LocationManager mLocationManager;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        String chosenSource = getIntent().getStringExtra("source");
        newsArticleArrayList = new ArrayList<>();
        initUI();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(AppUtils.isInternetConnected(mContext)){
            getTopHeadlinesFromASource(chosenSource);
        }else{
            Toast.makeText(mContext,"No Internet Connection",Toast.LENGTH_LONG).show();
        }

    }

    //location
    @Override
    public void onLocationChanged(Location location){
   longitude = location.getLongitude();
   latitude = location.getLatitude();
        Log.d("Location", "Long: "+longitude + "\n Lat: "+latitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private String locationConfig(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Toast.makeText(mContext, "Fetching the location...", Toast.LENGTH_SHORT).show();
        String res = getMyCurrentLocation(location);
        Log.d("Location", res);
        return res;
    }

    //getting my location
    private String getMyCurrentLocation(Location location){

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        String country="";
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            country = addresses.get(0).getCountryName();
         return country;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return country;
    }

    private void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Top Headlines");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        RecyclerView rc_viewArticles = findViewById(R.id.articles_rcv);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        rc_viewArticles.setLayoutManager(layoutManager);
        mNewsArticleAdapter = new NewsArticleAdapter(mContext,newsArticleArrayList);
        rc_viewArticles.setAdapter(mNewsArticleAdapter);
    }

    private void getTopHeadlinesFromASource(String newsSource){
         String country ="in";
            String count  = locationConfig();
        StringRequest stringRequest = new StringRequest(GET, Constants.BASE_URL+"top-headlines?sources="+"&country="+country.substring(0,2) + Constants.API_KEY_STR + Constants.API_KEY, new Response.Listener<String>() {
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String selectedCategory = "";
        if (id == R.id.nav_business) {
            selectedCategory = "business";
        } else if (id == R.id.nav_entertainment) {
            selectedCategory = "entertainment";
        } else if (id == R.id.nav_health) {
            selectedCategory = "health";
        } else if (id == R.id.nav_science) {
            selectedCategory = "science";
        } else if (id == R.id.nav_sports) {
            selectedCategory = "sports";
        } else if (id == R.id.nav_technology) {
            selectedCategory = "technology";
        }

        startActivity(new Intent(mContext,CategoryWiseNewsActivity.class).putExtra("category",selectedCategory));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.sarnava.myassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    ImageView pro_pic;
    TextView info;
    private String YouTube_API_Key = "AIzaSyBibkYuEgOL2kt76MwtpL1KUi3t06CN_xM";
    private ArrayList<Video> videolist = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String nextpageToken,user_name;
    String URL="https://www.googleapis.com/youtube/v3/search?pageToken=CDIQAQ&part=snippet&maxResults=50&order=date&type=video&videoCategoryId=23&key="+YouTube_API_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pro_pic=(ImageView) findViewById(R.id.toolbar_pic);
        info = (TextView) findViewById(R.id.user_info);
        recyclerView =(RecyclerView) findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Picasso.with(this).load(user.getPhotoUrl()).into(pro_pic);
            user_name = user.getDisplayName();
        }

        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        info.setText(user_name+"\n"+sp.getString("age", "")+" ("+sp.getString("gender", "")+")");

        mAuth=FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                }
            }
        };

        mAdapter = new VideoAdapter(videolist, new VideoAdapter.MyAdapterListener() {
            @Override
            public void ll_OnClick(View v, int position) {
                startActivity(new Intent(getApplicationContext(),Comment.class));
            }

            @Override
            public void ll2_OnClick(View v, int position) {
                Toast.makeText(getApplicationContext(),"Liked",Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //Toast.makeText(getApplicationContext(),totalItemsCount+" "+nextpageToken,Toast.LENGTH_SHORT).show();

                String new_URL="https://www.googleapis.com/youtube/v3/search?pageToken="+nextpageToken+"&part=snippet&maxResults=50&order=date&type=video&videoCategoryId=23&key="+YouTube_API_Key;
                loaddata(new_URL, totalItemsCount);

            }
        });


        //load on swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loaddata(URL, 0);
                swipeRefreshLayout.setRefreshing(false);
            }

        });


        loaddata(URL, 0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    public void loaddata(String url, final int index){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            try{
                                nextpageToken = jsonObject.getString("nextPageToken");
                            }catch (Exception e){}

                            JSONArray array = jsonObject.getJSONArray("items");

                            for (int i = 0 ; i < array.length() ; i++) {

                                JSONObject jo = array.getJSONObject(i);
                                JSONObject jo1 = jo.getJSONObject("id");
                                String s = jo1.getString("videoId");
                                String url= "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+s+"\" frameborder=\"0\" allowfullscreen></iframe>";

                                JSONObject jo2 = jo.getJSONObject("snippet");

                                videolist.add((index+i), new Video(
                                        url,
                                        jo2.getString("title")
                                ));

                            }

                            if(index==0){
                                mAdapter.notifyDataSetChanged();
                            }else {
                                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), videolist.size());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void so(View v){
        mAuth.signOut();
    }


}

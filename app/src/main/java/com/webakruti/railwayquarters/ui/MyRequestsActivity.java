package com.webakruti.railwayquarters.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.webakruti.railwayquarters.R;
import com.webakruti.railwayquarters.adapter.MyRequestColonyStatusAdapter;
import com.webakruti.railwayquarters.model.MyRequestStatusResponse;
import com.webakruti.railwayquarters.retrofit.service.RestClient;
import com.webakruti.railwayquarters.utils.NetworkUtil;
import com.webakruti.railwayquarters.utils.SharedPreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestsActivity extends AppCompatActivity {
    private ImageView imageViewBack;
    private View rootView;
    private com.webakruti.railwayquarters.utils.CustomSwipeToRefresh swipeContainer;
    private RecyclerView recyclerView;
    private TextView textViewNoData;
    private ProgressDialog progressDialogForAPI;
    private MyRequestColonyStatusAdapter myRequestStatusAdapter;
    boolean isCallFromPullDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyRequestsActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        swipeContainer = (com.webakruti.railwayquarters.utils.CustomSwipeToRefresh) findViewById(R.id.swipeContainer);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textViewNoData = (TextView) findViewById(R.id.textViewNoData);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(MyRequestsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager2);
        //recyclerView.setAdapter(new MyRequestStatusAdapter(MyRequestsActivity.this, list));

        initSwipeLayout();

        progressDialogForAPI = new ProgressDialog(MyRequestsActivity.this);
        progressDialogForAPI.setCancelable(false);
        progressDialogForAPI.setIndeterminate(true);
        progressDialogForAPI.setMessage("Please wait...");
        progressDialogForAPI.show();

        if (NetworkUtil.hasConnectivity(MyRequestsActivity.this)) {
            callGetRequestAPI();
        } else {
            Toast.makeText(MyRequestsActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }


        /*recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textViewNoData = (TextView) findViewById(R.id.textViewNoData);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(MyRequestsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager2);
        recyclerView.setAdapter(new MyRequestColonyStatusAdapter(MyRequestsActivity.this, 6));*/
    }

    private void initSwipeLayout() {

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("", "swipe to refresh");
                if (NetworkUtil.hasConnectivity(MyRequestsActivity.this)) {
                    // call API
                    isCallFromPullDown = true;
                    callGetRequestAPI();

                } else {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
// Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue,
                R.color.red,
                R.color.blue,
                R.color.red);

    }

    private void callGetRequestAPI() {

        SharedPreferenceManager.setApplicationContext(MyRequestsActivity.this);
        String token = SharedPreferenceManager.getUserObjectFromSharedPreference().getSuccess().getToken();

        String API = "http://quarter.webakruti.in/api/";
        String headers = "Bearer " + token;
        Call<MyRequestStatusResponse> requestCallback = RestClient.getApiService(API).getMyRequestStatus(headers);
        requestCallback.enqueue(new Callback<MyRequestStatusResponse>() {
            @Override
            public void onResponse(Call<MyRequestStatusResponse> call, Response<MyRequestStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {

                    MyRequestStatusResponse details = response.body();
                    //  Toast.makeText(getActivity(),"Data : " + details ,Toast.LENGTH_LONG).show();
                    if (details.getSuccess().getStatus() && details.getSuccess().getColony() != null && details.getSuccess().getColony().size() > 0) {
                        textViewNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        List<MyRequestStatusResponse.Colony> list = details.getSuccess().getColony();
                        myRequestStatusAdapter = new MyRequestColonyStatusAdapter(MyRequestsActivity.this, list);
                        recyclerView.setAdapter(myRequestStatusAdapter);
                    } else {
                        textViewNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }


                } else {
                    // Response code is 401
                }

                if (isCallFromPullDown) {
                    swipeContainer.setRefreshing(false);
                    isCallFromPullDown = false;
                } else {
                    if (progressDialogForAPI != null) {
                        progressDialogForAPI.cancel();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyRequestStatusResponse> call, Throwable t) {

                if (t != null) {

                    if (isCallFromPullDown) {
                        swipeContainer.setRefreshing(false);
                        isCallFromPullDown = false;
                    } else {
                        if (progressDialogForAPI != null) {
                            progressDialogForAPI.cancel();
                        }
                    }
                    if (t.getMessage() != null)
                        Log.e("error", t.getMessage());
                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MyRequestsActivity.this,HomePageActivity.class);
        startActivity(intent);
        finish();

        //super.onBackPressed();
    }
}

package com.andrew.selfserviceprototype.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Adapter.RatingAdapter;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class FinishActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
    }
}

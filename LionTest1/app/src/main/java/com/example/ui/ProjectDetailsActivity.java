package com.example.ui;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.com.example.base.BaseActivity;
import com.example.entity.Project;
import com.example.sean.liontest1.R;

public class ProjectDetailsActivity extends BaseActivity {

    WebView project_details;
    Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        setStatusBar();
        initView();
    }

    private void initView() {
        project = (Project)getIntent().getSerializableExtra("project");
        initTopBarForLeft(project.getTitle());
        project_details = (WebView)findViewById(R.id.project_details);
        project_details.loadUrl(project.getDetails_page());
    }

}

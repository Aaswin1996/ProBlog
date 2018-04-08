package com.developer.aaswin.problog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewBlogActivity extends AppCompatActivity {
    @BindView(R.id.NewBlog_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.NewBlog_caption)
    EditText mBLogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_blog);
        ButterKnife.bind(this);

        //Setup Tololbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post a New Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}

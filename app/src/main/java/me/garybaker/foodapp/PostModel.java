package me.garybaker.foodapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class PostModel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_model);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView author = findViewById(R.id.authormodel);
        TextView uid = findViewById(R.id.uidmodel);
        TextView title = findViewById(R.id.titlemodel);
        TextView body = findViewById(R.id.bodymodel);

        Bundle bundle = getIntent().getExtras();

        String authorStr = bundle.getString("author");
        author.setText(authorStr);

        String uidStr = bundle.getString("uid");
        uid.setText(uidStr);

        String titleStr = bundle.getString("title");
        title.setText(titleStr);

        String bodyStr = bundle.getString("body");
        body.setText(bodyStr);
    }
}
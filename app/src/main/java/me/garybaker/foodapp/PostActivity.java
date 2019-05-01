package me.garybaker.foodapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity extends BaseActivity {


    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.item_post);

//        Intent intent = getIntent().getAction(postK);


        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
        }

        Intent intent = getIntent();



        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String imageURI = intent.getStringExtra("imageURI");
        String body = intent.getStringExtra("body");
//        String starCount = intent.getStringExtra("starCount");
//        String uid = intent.getStringExtra("uid");



        ImageView imageView = findViewById(R.id.postAuthorPhoto);
        imageView.setImageURI(Uri.parse(post.imageURI));
//        imageView.getDrawable(imageURI);

        TextView titleTextView = findViewById(R.id.postTitle);
        titleTextView.setText(title);

        TextView authorTextView = findViewById(R.id.postAuthor);
        authorTextView.setText(author);

        TextView bodyTextView = findViewById(R.id.postBody);
        bodyTextView.setText(body);

//        TextView starTextView = findViewById(R.id.star);



//        setContentView(intent.getStringArrayExtra("title"));






//        textView.setText(data);
//





    }
}

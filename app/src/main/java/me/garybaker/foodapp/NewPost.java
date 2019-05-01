package me.garybaker.foodapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;



public class NewPost extends MainActivity {

    private static final CharSequence REQUIRED = "Required";
    private static final String TAG = "NewPost";

    private TextView mTitleField;
    public TextView mBodyField;
    public FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    public FirebaseDatabase mDB = FirebaseDatabase.getInstance();
    public ImageView selectImage;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final Button btnPost = findViewById(R.id.postData);

        mTitleField = findViewById(R.id.postText);
        mBodyField = findViewById(R.id.postBody);
        mBodyField.setText("this is the body of my shit here..");


        selectImage = findViewById(R.id.iv);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

                //one can be replaced with any action code



            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                submitPost();
            }
        });

    }


    /**
     *
     * submitPost()
     * this here well yeah idfek
     *
     */

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();



        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();

        mDatabase.getReference("users");
        mDatabase.getReference(userId);

        mDatabase.getReference().child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPost.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post



                            key = mDB.getReference().child("posts").push().getKey();


                            FirebaseStorage storage = FirebaseStorage.getInstance("gs://foodapp-fa8fb.appspot.com/");

                            // Create a storage reference from our app
                            StorageReference storageRef = storage.getReference();

                            // Create a reference to "mountains.jpg"
                            StorageReference mountainsRef = storageRef.child("users/"+getUid()+"/images/"+key+"/image.jpg");


                            // Get the data from an ImageView as bytes
                            selectImage.setDrawingCacheEnabled(true);
                            selectImage.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) selectImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] data = baos.toByteArray();



                            writeNewPost(userId, user.username, mountainsRef.getPath(), title, body);

                            // Create file metadata including the content type
                            final StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg")
                                    .build();

                            // Upload the file and metadata
                            UploadTask uploadTask = mountainsRef.putBytes(data, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...



//                                    mountainsRef.putFile(bitmap, metadata);

                                }
                            });









                        }
                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
//                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }


    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
//            mSubmitButton.show();
        } else {
//            mSubmitButton.hide();
        }
    }


    /**
     * @param userId
     * @param username
     * @param title
     * @param body
     * writeNewPost()
     *                 Create new post at /user-posts/$userid/$postid and at
     */

    // [START write_fan_out]
    public void writeNewPost(String userId, String username, String uriImage, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Post post = new Post(userId, username, uriImage, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDB.getReference().updateChildren(childUpdates);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK) {
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    selectImage.setImageURI(selectedImage);


                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    selectImage.setImageURI(selectedImage);
                }
                break;
        }
    }

}

package me.garybaker.foodapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String uid;

    public User() {

    }

    public User(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("uid", uid);

        return result;
    }

}
// [END blog_user_class]
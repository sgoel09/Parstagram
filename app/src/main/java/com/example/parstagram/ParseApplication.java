package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("shefali-parstagram") // should correspond to APP_ID env variable
                .clientKey("MCp1Zrainadf4485K")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://shefali-parstagram.herokuapp.com/parse/").build());
    }
}

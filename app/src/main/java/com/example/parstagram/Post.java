package com.example.parstagram;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Class that represents a post object.
 * */
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_PROFILE_PIC = "profilepic";
    public static final String KEY_LIKES = "likes";

    /** Required empty constructor. */
    public Post() {}

    /** @return the description of the post */
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    /** Set the description of the post */
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    /** @return the image of the post */
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    /** Set the image of the post */
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    /** @return the user of the post */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /** Set the user of the post */
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    /** @return the profile pic of the user of the post */
    public ParseFile getProfilePic() {
        return getUser().getParseFile(KEY_PROFILE_PIC);
    }

    /** @return the likes of the post */
    public ArrayList<String> getLikes() {
        return (ArrayList<String>) get(KEY_LIKES);
    }

    /** Set the likes of the post */
    public void setLikes(ArrayList<String> likes) {
        put(KEY_LIKES, likes);
    }

    /** @return the relative time of the post */
    protected String getRelativeTime() {
        long mills = getCreatedAt().getTime();
        return DateUtils.getRelativeTimeSpanString(mills).toString();
    }
}

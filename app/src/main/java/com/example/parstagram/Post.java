package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_PROFILE_PIC = "profilepic";

    public Post() {}

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseFile getProfilePic() {
        return getUser().getParseFile(KEY_PROFILE_PIC);
    }

    public static ParseFile getProfilePicFromUser(ParseUser user) {
        return user.getParseFile(KEY_PROFILE_PIC);
    }

    public void setProfilePic(ParseFile profilePic) {
        put(KEY_PROFILE_PIC, profilePic);
    }
}

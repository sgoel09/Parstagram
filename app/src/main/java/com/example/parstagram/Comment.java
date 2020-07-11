package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Class that represents a comment object.
 * */
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_BODY = "body";
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_PROFILE_PIC = "profilepic";

    /** Required empty constructor. */
    public Comment() {}

    /** @return the body of the comment */
    public String getBody() {
        return getString(KEY_BODY);
    }

    /** Set the body of the comment */
    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    /** @return the user of the comment */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /** Set the user of the comment */
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    /** @return the post the comment was made for */
    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    /** Set the post the comment was made for */
    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    /** @return the profile picture of the user of the comment */
    public ParseFile getProfilePic() {
        return getUser().getParseFile(KEY_PROFILE_PIC);
    }
}

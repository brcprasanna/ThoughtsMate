package ram.king.com.makebharathi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

// [START blog_user_class]
@IgnoreExtraProperties
public class UserForLikes implements Serializable {

    public String displayName;
    public String photoUrl;
    public String uID;

    public UserForLikes() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserForLikes(String displayName, String photoUrl, String uID) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.uID = uID;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
// [END blog_user_class]

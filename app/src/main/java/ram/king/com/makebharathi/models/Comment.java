package ram.king.com.makebharathi.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;
    public String photoUrl;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text, String photoUrl) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.photoUrl = photoUrl;
    }

}
// [END comment_class]

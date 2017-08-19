package ram.king.com.makebharathi.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public String photoUrl;
    public String dedicatedTo;
    public String courtesy;
    public String language;
    public String image;
    public Object timestamp;


    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String photoUrl, String dedicatedTo, String courtesy, String language, String image) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.photoUrl = photoUrl;
        this.dedicatedTo = dedicatedTo;
        this.courtesy = courtesy;
        this.language = language;
        this.image = image;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestamp = ServerValue.TIMESTAMP;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("photoUrl",photoUrl);
        result.put("dedicatedTo", dedicatedTo);
        result.put("courtesy", courtesy);
        result.put("language", language);
        result.put("image", image);
        result.put("timestamp", timestamp);
        return result;
    }

}
// [END post_class]

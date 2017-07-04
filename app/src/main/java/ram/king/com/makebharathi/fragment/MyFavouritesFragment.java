package ram.king.com.makebharathi.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyFavouritesFragment extends PostListFragment {

    public MyFavouritesFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        //showProgressDialog();
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("star-user-posts").child(myUserId);
        // [END my_top_posts_query]
        //hideProgressDialog();
        return myTopPostsQuery;
    }
}

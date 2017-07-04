package ram.king.com.makebharathi.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        //showProgressDialog();
        Query myPostsQuery = databaseReference.child("user-posts").child(getUid());
        //hideProgressDialog();
        return myPostsQuery;
    }
}

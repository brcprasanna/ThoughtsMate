package ram.king.com.makebharathi.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;

public class UserAllPostFragment extends PostListFragment {

    public UserAllPostFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        String prefUser = AppUtil.getString(activity, AppConstants.PREF_USER_POST_QUERY, "");
        Query myPostsQuery = databaseReference.child("user-posts").child(prefUser);
        //hideProgressDialog();
        return myPostsQuery;
    }
}

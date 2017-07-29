package ram.king.com.makebharathi.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;

public class RecentPostsFragment extends PostListFragment {

    public RecentPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        //showProgressDialog();
        Query recentPostsQuery;
        String preferredLanguage = AppUtil.getString(activity, AppConstants.PREFERRED_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);
        String limitToLast = AppUtil.getString(activity, AppConstants.QUERY_LIMIT_TO_LAST, AppConstants.DEFAULT_LIMIT_TO_LAST);
        boolean useLimitToLast = AppUtil.getBoolean(activity, AppConstants.USE_LIMIT_TO_LAST, AppConstants.DEFAULT_USE_LIMIT_TO_LAST);

        if (useLimitToLast) {
            recentPostsQuery = databaseReference.child("posts").orderByChild("language").equalTo(preferredLanguage).limitToLast(Integer.parseInt(limitToLast));
        } else {
            recentPostsQuery = databaseReference.child("posts").orderByChild("language").equalTo(preferredLanguage);
        }
        //hideProgressDialog();
        return recentPostsQuery;
    }
}

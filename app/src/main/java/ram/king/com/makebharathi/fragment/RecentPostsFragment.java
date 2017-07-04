package ram.king.com.makebharathi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.util.AppConstants;

public class RecentPostsFragment extends PostListFragment {

    public RecentPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        //showProgressDialog();
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        String preferredLanguage = sharedPref.getString(AppConstants.PREFERRED_LANGUAGE,AppConstants.DEFAULT_LANGUAGE);
        Query recentPostsQuery = databaseReference.child("posts").orderByChild("language").equalTo(preferredLanguage);
        //hideProgressDialog();
        return recentPostsQuery;
    }
}

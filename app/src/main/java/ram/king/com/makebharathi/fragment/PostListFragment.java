package ram.king.com.makebharathi.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.activity.PostDetailActivity;
import ram.king.com.makebharathi.models.Comment;
import ram.king.com.makebharathi.models.Post;
import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;
import ram.king.com.makebharathi.util.MessageEvent;
import ram.king.com.makebharathi.viewholder.PostViewHolder;

public abstract class PostListFragment extends BaseFragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private ProgressBar mProgressBar;

    private List<String> mCommentIds = new ArrayList<>();
    private List<Comment> mComments = new ArrayList<>();
    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        return rootView;
    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(activity);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        setupAdapterWithQuery();
    }

    public void setupAdapterWithQuery()
    {
        Query postsQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(activity, PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Create a deep link and display it in the UI
                //Uri deepLink = null;
                //deepLink = Uri.parse(createShortDynamicLink(Uri.parse(AppConstants.DEEP_LINK_URL+"/"+postKey), 0, viewHolder, model.author, model.title));
                //createShortDynamicLink(Uri.parse(AppConstants.DEEP_LINK_URL+"/"+postKey), 0, viewHolder,model.author,model.title);
                //((TextView) findViewById(R.id.link_view_send)).setText(deepLink.toString());

                // Share button click listener
                //final Uri finalDeepLink = deepLink;
                viewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            createShortDynamicLink(Uri.parse(AppConstants.DEEP_LINK_URL+"/"+postKey), 0, viewHolder,model.author,model.title);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //shareDeepLink(finalDeepLink.toString().replace(" ","%20"),model.author,model.title);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
/*
                Glide.with(activity).load(model.photoUrl)
                        .placeholder(getResources().getDrawable(R.drawable.ic_action_account_circle_40))
                        .into(viewHolder.authorPhoto)*/;

                Glide
                        .with(activity)
                        .load(model.photoUrl)
                        .into(viewHolder.authorPhoto);

                //getting count of comments

                DatabaseReference postCommentsRef = mDatabase.child("post-comments").child(postRef.getKey());
                postCommentsRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //ToDo Need to create a separate model for this
                                //may be come here
                                Comment comment = dataSnapshot.getValue(Comment.class);
                                if (comment == null)
                                    viewHolder.commentView.setVisibility(View.GONE);
                                else
                                    viewHolder.commentView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                if (model != null && model.uid.equals(getUid())) {
                    viewHolder.deleteView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.deleteView.setVisibility(View.GONE);
                }


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions

                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                },new View.OnClickListener(){
                    public void onClick(View deleteView) {
                        // Need to write to both places the post is stored

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!activity.isFinishing()){
                                    new AlertDialog.Builder(activity)
                                            .setTitle(getResources().getString(R.string.delete_header))
                                            .setMessage(getResources().getString(R.string.delete_message))
                                            .setCancelable(false)
                                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                                                    DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                                                    DatabaseReference starUserPostRef = mDatabase.child("star-user-posts").child(getUid()).child(postRef.getKey());// Run two transactions
                                                    DatabaseReference commentPostRef = mDatabase.child("post-comments").child(postRef.getKey());
                                                    globalPostRef.removeValue();
                                                    userPostRef.removeValue();
                                                    starUserPostRef.removeValue();
                                                    commentPostRef.removeValue();
                                                }
                                            }).setNegativeButton("CANCEL", null).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                mProgressBar.setVisibility(View.GONE);
                mRecycler.setVisibility(View.VISIBLE);
                AppUtil.getDynamicLink(activity);
            }
        };

        //come here
        //https://stackoverflow.com/questions/35506347/loading-view-before-data-is-loaded-into-recycler-view
        //https://firebase.google.com/docs/database/android/offline-capabilities

/*
        mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //mProgressBar.setVisibility(View.GONE);

            }

        });
*/

        mRecycler.setAdapter(mAdapter);
    }

    /**
     * Build a Firebase Dynamic Link.
     * https://firebase.google.com/docs/dynamic-links/android/create#create-a-dynamic-link-from-parameters
     *
     * @param deepLink the deep link your app will open. This link must be a valid URL and use the
     *                 HTTP or HTTPS scheme.
     * @param minVersion the {@code versionCode} of the minimum version of your app that can open
     *                   the deep link. If the installed app is an older version, the user is taken
     *                   to the Play store to upgrade the app. Pass 0 if you do not
     *                   require a minimum version.
     */
    @VisibleForTesting
    public String buildDeepLink(@NonNull Uri deepLink, int minVersion) throws UnsupportedEncodingException {
        String domain = getString(R.string.app_code) + ".app.goo.gl/";

        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setImageUrl(Uri.parse("https://static.wixstatic.com/media/5227b1_0baa4b32227c486d9d868e83a3bf5f2e~mv2.png/v1/fill/w_132,h_132,al_c,usm_0.66_1.00_0.01/5227b1_0baa4b32227c486d9d868e83a3bf5f2e~mv2.png"))
                                .setTitle(activity.getResources().getString(R.string.app_name))
                                .build())
                .setLink(deepLink);

        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return  java.net.URLDecoder.decode(String.valueOf(link.getUri()), "UTF-8");
    }

    public String createDynamicLink() throws UnsupportedEncodingException {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://example.com/"))
                .setDynamicLinkDomain("abc123.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.example.android")
                                .setMinimumVersion(125)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setImageUrl(Uri.parse(String.valueOf(R.drawable.ic_launcher)))
                                .setTitle("Example of a Dynamic Link")
                                .setDescription("This link works whether the app is installed or not!")
                                .build())
                .buildDynamicLink();
        return  java.net.URLDecoder.decode(String.valueOf(dynamicLink.getUri()), "UTF-8");
    }

    private void createShortDynamicLink(@NonNull Uri deepLink, int minVersion, final PostViewHolder viewHolder, final String author, final String title) throws UnsupportedEncodingException {
        String domain = getString(R.string.app_code) + ".app.goo.gl/";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDeepLink(deepLink,minVersion)))
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            final Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            shareDeepLink(shortLink.toString().replace(" ","%20"),author,title);
                        } else {
                            // Error
                            // ...
                        }

                    }
                });
    }

    private void shareDeepLink(String deepLink,String author, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT,author+" wrote on "+title+" "+deepLink+" via ThoughtsMate");

        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if(event.getMessage().equals("changed")) {
            setupAdapterWithQuery();
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else if (event.getMessage().equals("refresh"))
        {
            mManager.scrollToPositionWithOffset(mAdapter.getItemCount(), 0);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

    }

    // [START post_stars_transaction]
    private void onStarClicked(final DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                    mDatabase.child("star-user-posts").child(getUid()).child(postRef.getKey()).removeValue();
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                    Map<String, Object> postValues = p.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/star-user-posts/" + getUid() + "/" + postRef.getKey(), postValues);

                        mDatabase.updateChildren(childUpdates);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}

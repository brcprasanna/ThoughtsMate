package ram.king.com.makebharathi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.Comment;
import ram.king.com.makebharathi.models.Post;
import ram.king.com.makebharathi.models.User;
import ram.king.com.makebharathi.util.AppConstants;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";
    PrettyTime prettyTime;
    Post post;
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private boolean mFocusComment;
    private CommentAdapter mAdapter;
    private CircularImageView mAuthorPhoto;
    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private TextView mDateView;
    private TextView mDedicatedToView;
    private TextView mCourtesyView;
    private TextInputEditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private DatabaseReference mDatabase;
    private Menu menu;
    private Button mViewCommentButton;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        String action =  intent.getAction();
        Uri data = intent.getData();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(AppConstants.EXTRA_POST_KEY);
        mFocusComment = getIntent().getBooleanExtra(AppConstants.EXTRA_FOCUS_COMMENT, false);


        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);

        // Initialize Views
        mAuthorPhoto = (CircularImageView) findViewById(R.id.post_author_photo);
        mAuthorView = (TextView) findViewById(R.id.post_author);
        mTitleView = (TextView) findViewById(R.id.post_title);
        mBodyView = (TextView) findViewById(R.id.post_body);
        mDateView = (TextView) findViewById(R.id.post_date);
        mDedicatedToView = (TextView) findViewById(R.id.post_dedicated_to);
        mCourtesyView= (TextView) findViewById(R.id.post_courtesy);
        mViewCommentButton = (Button) findViewById(R.id.button_view_comment);

        //mTitleView.setVisibility(View.GONE);

        mCommentField = (TextInputEditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        prettyTime = new PrettyTime();

        toggleComment(mFocusComment);

        mViewCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleComment(true);
            }
        });

        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setMessage("if you enjoy using this app, please take a moment to rate it. Thanks for your support!")
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    private void toggleComment(boolean mSwitch) {
        if (!mSwitch) {
            mViewCommentButton.setVisibility(View.VISIBLE);
            mCommentField.setVisibility(View.GONE);
            mCommentsRecycler.setVisibility(View.GONE);
            mCommentButton.setVisibility(View.GONE);
        } else {
            mViewCommentButton.setVisibility(View.GONE);
            mCommentField.setVisibility(View.VISIBLE);
            mCommentsRecycler.setVisibility(View.VISIBLE);
            mCommentButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    //@VisibleForTesting
    //AdView getAdView() {
    //   return mAdView;
    // }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(PostDetailActivity.this).load(post.photoUrl)
                            .into(mAuthorPhoto);

                    mAuthorView.setText(post.author);

                    if (!TextUtils.isEmpty(post.title)) {
                        mTitleView.setVisibility(View.VISIBLE);
                        mTitleView.setText("Title : " + post.title);
                    } else {
                        mTitleView.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(post.dedicatedTo)) {
                        mDedicatedToView.setVisibility(View.VISIBLE);
                        mDedicatedToView.setText("Dedicated To : " + post.dedicatedTo);
                    } else {
                        mDedicatedToView.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(post.courtesy)) {
                        mCourtesyView.setVisibility(View.VISIBLE);
                        mCourtesyView.setText("Courtesy : " + post.courtesy);
                    } else {
                        mCourtesyView.setVisibility(View.GONE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        mBodyView.setText(Html.fromHtml(post.body, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
                    else
                        mBodyView.setText(Html.fromHtml(post.body), TextView.BufferType.SPANNABLE);

                    long yourmilliseconds = (long) post.timestamp;
                    if (prettyTime != null)
                        mDateView.setText(prettyTime.format(new Date(yourmilliseconds)));

                    if (menu != null) {
                        MenuItem itemLike = menu.findItem(R.id.menu_like);
                        MenuItem itemLikeNum = menu.findItem(R.id.menu_like_num);
                        MenuItem itemDelete = menu.findItem(R.id.menu_delete);

                        if (post != null && post.stars.containsKey(getUid())) {
                            itemLike.setIcon(R.drawable.ic_favorite_white_24dp);
                        } else {
                            itemLike.setIcon(R.drawable.ic_favorite_border_white_24dp);
                        }

                        if (post != null) {
                            itemLikeNum.setTitle(String.valueOf(post.starCount));
                        }

                        if (post != null && post.uid.equals(getUid())) {
                            itemDelete.setVisible(true);
                        } else {
                            itemDelete.setVisible(false);
                        }
                    }
                } else {
                    mBodyView.setText("This post is removed by the Author");
                    mDateView.setText("");
                    mCommentsRecycler.setVisibility(View.GONE);
                    mCommentButton.setVisibility(View.GONE);
                    mViewCommentButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };


        /*final Button buttonToggle = (Button) this.findViewById(R.id.button_toggle);
        buttonToggle.setVisibility(View.VISIBLE);
        buttonToggle.setBackgroundResource(R.drawable.ic_expand_more);
// set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        mBodyView.setAnimationDuration(1000L);

        // set interpolators for both expanding and collapsing animations
        mBodyView.setInterpolator(new OvershootInterpolator());

// or set them separately
        mBodyView.setExpandInterpolator(new OvershootInterpolator());
        mBodyView.setCollapseInterpolator(new OvershootInterpolator());

// toggle the mBodyView
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mBodyView.toggle();
                buttonToggle.setText(mBodyView.isExpanded() ? "Collapse" : "Expand");
            }
        });

// but, you can also do the checks yourself
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (mBodyView.isExpanded())
                {
                    buttonToggle.setBackgroundResource(R.drawable.ic_expand_more);
                    mBodyView.collapse();
                }
                else
                {
                    buttonToggle.setBackgroundResource(R.drawable.ic_expand_less);
                    mBodyView.expand();
                    if (mBodyView.getLineCount() <= 5)
                        buttonToggle.setVisibility(View.GONE);

                }
            }
        });

// listen for expand / collapse events
        mBodyView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView expanded");
            }

            @Override
            public void onCollapse(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView collapsed");
            }
        });
*/
        mPostReference.addValueEventListener(postListener);
        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);

        mCommentsReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        setCommentCount((Map<String, Object>) dataSnapshot.getValue());
                        //NewPostActivity.this.usersListAdapterForDedicatedTo.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    private void setCommentCount(Map<String, Object> value) {
        int count = 0;
        if (value != null) {
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                count++;
            }
            if (count > 0)
                mViewCommentButton.setText(getString(R.string.view_comments) + " (" + count + ")");
            else
                mViewCommentButton.setText(getString(R.string.write_a_comment));
        } else
            mViewCommentButton.setText(getString(R.string.write_a_comment));
    }


    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.displayName;

                        if (authorName == null)
                            authorName = user.username;

                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        if (TextUtils.isEmpty(commentText))
                            return;

                        Comment comment;
                        if (user.photoUrl != null && authorName!= null)
                            comment = new Comment(uid, authorName, commentText,user.photoUrl);
                        else
                            comment = new Comment(uid, authorName, commentText,user.photoUrl);

                        //Comment comment = new Comment(uid, authorName, commentText,user.photoUrl);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);


                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem itemLike = menu.findItem(R.id.menu_like);
        MenuItem itemLikeNum = menu.findItem(R.id.menu_like_num);
        MenuItem itemDelete = menu.findItem(R.id.menu_delete);

        if (post != null && post.stars.containsKey(getUid())) {
            itemLike.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            itemLike.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        if (post != null)
            itemLikeNum.setTitle(String.valueOf(post.starCount));


        if (post != null && post.uid.equals(getUid())) {
            itemDelete.setVisible(true);
        } else {
            itemDelete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_like) {
            onClickStar();
            return true;
        } else if (i == R.id.menu_share) {
            try {
                createShortDynamicLink(Uri.parse(AppConstants.DEEP_LINK_URL+"/"+mPostReference.toString()), 0, post.author,post.title);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        } else if (i == R.id.menu_delete) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()) {
                        new AlertDialog.Builder(PostDetailActivity.this)
                                .setTitle(getResources().getString(R.string.delete_header))
                                .setMessage(getResources().getString(R.string.delete_message))
                                .setCancelable(false)
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference globalPostRef = mDatabase.child("posts").child(mPostReference.getKey());
                                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(mPostReference.getKey());
                                        DatabaseReference starUserPostRef = mDatabase.child("star-user-posts").child(getUid()).child(mPostReference.getKey());// Run two transactions
                                        DatabaseReference commentPostRef = mDatabase.child("post-comments").child(mPostReference.getKey());
                                        globalPostRef.removeValue();
                                        userPostRef.removeValue();
                                        starUserPostRef.removeValue();
                                        commentPostRef.removeValue();
                                        finish();
                                    }
                                }).setNegativeButton("CANCEL", null).show();
                    }

                }
            });
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void onClickStar() {

        // Need to write to both places the post is stored
        DatabaseReference globalPostRef = mDatabase.child("posts").child(mPostReference.getKey());
        DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(mPostReference.getKey());

        // Run two transactions

        onStarClicked(globalPostRef);
        onStarClicked(userPostRef);

    }

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

    private void createShortDynamicLink(@NonNull Uri deepLink, int minVersion, final String author, final String title) throws UnsupportedEncodingException {
        String domain = getString(R.string.app_code) + ".app.goo.gl/";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDeepLink(deepLink,minVersion)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
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
                                .setTitle(getResources().getString(R.string.app_name))
                                .build())
                .setLink(deepLink);

        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return  java.net.URLDecoder.decode(String.valueOf(link.getUri()), "UTF-8");
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;
        public CircularImageView authorPhoto;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorPhoto = (CircularImageView) itemView.findViewById(R.id.comment_photo);
            authorView = (TextView) itemView.findViewById(R.id.comment_author);
            bodyView = (TextView) itemView.findViewById(R.id.comment_body);

        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
            Glide.with(PostDetailActivity.this).load(comment.photoUrl)
                    .into(holder.authorPhoto);


        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }



}

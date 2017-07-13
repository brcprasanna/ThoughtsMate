package ram.king.com.makebharathi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.Comment;
import ram.king.com.makebharathi.models.Post;
import ram.king.com.makebharathi.models.User;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
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

    PrettyTime prettyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        String action =  intent.getAction();
        Uri data = intent.getData();

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
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

        //mTitleView.setVisibility(View.GONE);

        mCommentField = (TextInputEditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        prettyTime = new PrettyTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(PostDetailActivity.this).load(post.photoUrl)
                        .into(mAuthorPhoto);

                //getSupportActionBar().setTitle(post.title);

                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                if (!TextUtils.isEmpty(post.dedicatedTo)) {
                    mDedicatedToView.setVisibility(View.VISIBLE);
                    mDedicatedToView.setText("Dedicated To : " + post.dedicatedTo);
                }
                else {
                    mDedicatedToView.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(post.courtesy)) {
                    mCourtesyView.setVisibility(View.VISIBLE);
                    mCourtesyView.setText("Courtesy : " + post.courtesy);
                }
                else {
                    mCourtesyView.setVisibility(View.GONE);
                }

                mBodyView.setText(post.body);


                long yourmilliseconds = (long) post.timestamp;
                if (prettyTime != null)
                    mDateView.setText(prettyTime.format(new Date(yourmilliseconds)));

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

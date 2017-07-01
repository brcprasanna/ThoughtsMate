package ram.king.com.makebharathi.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.adapter.CourtesyUsersAdapter;
import ram.king.com.makebharathi.adapter.DedicatedToUsersAdapter;
import ram.king.com.makebharathi.models.Post;
import ram.king.com.makebharathi.models.User;
import ram.king.com.makebharathi.util.AppConstants;


public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private TextInputEditText mTitleField;
    private TextInputEditText mDedicatedToField;
    private TextInputEditText mCourtesyField;
    private TextInputEditText mBodyField;
    private FloatingActionButton mSubmitButton;

    FirebaseListAdapter<User> mAdapter;
    // List view
    private ListView lvUsersForDedication;
    private ListView lvUsersForCourtesy;
    // Listview Adapter
    ArrayAdapter<User> usersListAdapterForDedicatedTo;
    ArrayAdapter<User> usersListAdapterForCourtesy;
    // ArrayList for Listview
    ArrayList<User> usersList = new ArrayList<>();

    Spinner mLanguageSpinner;

    private TextInputLayout mDedicationTextLayout;
    private TextInputLayout mCourtesyTextLayout;

    private Button mDedicationButton;
    private Button mCourtesyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        lvUsersForDedication = (ListView) findViewById(R.id.users_list_view_dedication);
        lvUsersForCourtesy = (ListView) findViewById(R.id.users_list_view_courtesy);

        lvUsersForDedication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               User user = (User) lvUsersForDedication.getItemAtPosition(i);
               mDedicatedToField.setText(user.displayName);
               mDedicatedToField.setSelection(mDedicatedToField.getText().length());
               lvUsersForDedication.setVisibility(View.GONE);
           }
        });

        lvUsersForCourtesy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) lvUsersForCourtesy.getItemAtPosition(i);
                mCourtesyField.setText(user.displayName);
                mCourtesyField.setSelection(mCourtesyField.getText().length());
                lvUsersForCourtesy.setVisibility(View.GONE);
            }
        });
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mTitleField = (TextInputEditText) findViewById(R.id.field_title);
        mDedicatedToField = (TextInputEditText) findViewById(R.id.field_dedicated_to);
        mCourtesyField = (TextInputEditText) findViewById(R.id.field_courtesy);
        mBodyField = (TextInputEditText) findViewById(R.id.field_body);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mDedicationButton = (Button) findViewById(R.id.button_dedication);
        mCourtesyButton = (Button) findViewById(R.id.button_courtesy);
        mDedicationTextLayout = (TextInputLayout) findViewById(R.id.textLayoutDedicateTo);
        mCourtesyTextLayout = (TextInputLayout) findViewById(R.id.textLayoutCourtesy);

        mDedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDedicationButton.getText().toString().startsWith("+")) {
                    mDedicationButton.setText(R.string.minus_dedication);
                    mDedicationTextLayout.setVisibility(View.VISIBLE);
                    mDedicatedToField.setText("");
                }
                else {
                    mDedicationButton.setText(R.string.plus_dedication);
                    mDedicationTextLayout.setVisibility(View.GONE);
                }
            }
        });

        mCourtesyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCourtesyButton.getText().toString().startsWith("+")) {
                    mCourtesyButton.setText(R.string.minus_courtesy);
                    mCourtesyTextLayout.setVisibility(View.VISIBLE);
                    mCourtesyField.setText("");
                }
                else {
                    mCourtesyButton.setText(R.string.plus_courtesy);
                    mCourtesyTextLayout.setVisibility(View.GONE);
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });


        mLanguageSpinner= (Spinner)findViewById(R.id.spinner_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, AppConstants.languages);
        mLanguageSpinner.setAdapter(adapter);
        mLanguageSpinner.setSelection(21);

        mDedicatedToField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocussed) {
                if (!isFocussed)
                    lvUsersForDedication.setVisibility(View.GONE);
            }
        });

        mCourtesyField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocussed) {
                if (!isFocussed)
                    lvUsersForCourtesy.setVisibility(View.GONE);
            }
        });

        mDedicatedToField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (cs.length() >= 3 && usersListAdapterForDedicatedTo != null) {
                    NewPostActivity.this.usersListAdapterForDedicatedTo.getFilter().filter(cs);
                    lvUsersForDedication.bringToFront();
                    lvUsersForDedication.setVisibility(View.VISIBLE);
                }
                else
                    lvUsersForDedication.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        mCourtesyField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (cs.length() >= 3 && usersListAdapterForCourtesy != null) {
                    NewPostActivity.this.usersListAdapterForCourtesy.getFilter().filter(cs);
                    lvUsersForCourtesy.bringToFront();
                    lvUsersForCourtesy.setVisibility(View.VISIBLE);
                }
                else
                    lvUsersForCourtesy.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectUsers((Map<String,Object>) dataSnapshot.getValue());
                        //NewPostActivity.this.usersListAdapterForDedicatedTo.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        // Adding items to listview


    }

    private void collectUsers(Map<String,Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            User user = new User();
            Map singleUser = (Map) entry.getValue();
            //Get display name field and append to list
            user.displayName = (String) singleUser.get("displayName");
            user.photoUrl = (String) singleUser.get("photoUrl");
            usersList.add(user);
        }
        if (usersList != null) {
            usersListAdapterForDedicatedTo = new DedicatedToUsersAdapter(this, R.layout.list_users, usersList);
            usersListAdapterForCourtesy = new CourtesyUsersAdapter(this, R.layout.list_users, usersList);
            lvUsersForDedication.setAdapter(usersListAdapterForDedicatedTo);
            lvUsersForCourtesy.setAdapter(usersListAdapterForCourtesy);
        }
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String dedicatedTo = mDedicatedToField.getText().toString();
        final String courtesy = mCourtesyField.getText().toString();
        final String body = mBodyField.getText().toString();
        final String language = mLanguageSpinner.getSelectedItem().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(dedicatedTo)) {
            mDedicatedToField.setText("");
        }

        if (TextUtils.isEmpty(courtesy)) {
            mCourtesyField.setText("");
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.displayName, title, body,user.photoUrl, dedicatedTo, courtesy, language);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String displayName, String title, String body, String photoUrl, String dedicatedTo, String courtesy, String language) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, displayName, title, body, photoUrl, dedicatedTo, courtesy, language);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]
}

package ram.king.com.makebharathi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.adapter.CourtesyUsersAdapter;
import ram.king.com.makebharathi.adapter.DedicatedToUsersAdapter;
import ram.king.com.makebharathi.models.User;
import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;
import ram.king.com.makebharathi.util.MessageEvent;


public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final int SELECT_IMAGE = 2;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    FirebaseListAdapter<User> mAdapter;
    // [END declare_database_ref]
    // Listview Adapter
    ArrayAdapter<User> usersListAdapterForDedicatedTo;
    ArrayAdapter<User> usersListAdapterForCourtesy;
    // ArrayList for Listview
    ArrayList<User> usersList = new ArrayList<>();
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private TextInputEditText mTitleField;
    private TextInputEditText mDedicatedToField;
    private TextInputEditText mCourtesyField;
    // List view
    private ListView lvUsersForDedication;
    private ListView lvUsersForCourtesy;
    private TextInputLayout mDedicationTextLayout;
    private TextInputLayout mCourtesyTextLayout;
    /*private Button mDedicationButton;
    private Button mCourtesyButton;
*/
    private String mBackupComposeText;
    private TextInputEditText mSetImageField;
    private ImageButton mAttachment;
    private String selectedPath = "";

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String getRealPathFromDocumentUri(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            //Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{imgId}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        String prefLanguage = AppUtil.getString(this, AppConstants.PREFERRED_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);
        getSupportActionBar().setTitle("New Thought"+" "+"("+prefLanguage+")");

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

        // mDedicationButton = (Button) findViewById(R.id.button_dedication);
        // mCourtesyButton = (Button) findViewById(R.id.button_courtesy);

        mDedicationTextLayout = (TextInputLayout) findViewById(R.id.textLayoutDedicateTo);
        mCourtesyTextLayout = (TextInputLayout) findViewById(R.id.textLayoutCourtesy);

        mSetImageField = (TextInputEditText) findViewById(R.id.field_image);

        mAttachment = (ImageButton) findViewById(R.id.btnAttach);
        mAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalleryImage();
            }
        });

        /*mDedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDedicationButton.getText().toString().startsWith("+")) {
                    mDedicationButton.setText(R.string.minus_dedication);
                    mDedicationTextLayout.setVisibility(View.VISIBLE);
                    mDedicatedToField.requestFocus();
                    mDedicatedToField.setText("");
                    lvUsersForCourtesy.setVisibility(View.GONE);
                }
                else {
                    mDedicationButton.setText(R.string.plus_dedication);
                    mDedicationTextLayout.setVisibility(View.GONE);
                    lvUsersForCourtesy.setVisibility(View.GONE);
                }
            }
        });

        mCourtesyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCourtesyButton.getText().toString().startsWith("+")) {
                    mCourtesyButton.setText(R.string.minus_courtesy);
                    mCourtesyTextLayout.setVisibility(View.VISIBLE);
                    mCourtesyField.requestFocus();
                    mCourtesyField.setText("");
                    lvUsersForDedication.setVisibility(View.GONE);
                }
                else {
                    mCourtesyButton.setText(R.string.plus_courtesy);
                    mCourtesyTextLayout.setVisibility(View.GONE);
                    lvUsersForDedication.setVisibility(View.GONE);
                }
            }
        });
*/
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
                } else
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
                } else
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
        verifyStoragePermissions(this);

    }

    public void openGalleryImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_IMAGE);
    }

    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Audio.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    private void handleSendText(Intent intent) {
        mBackupComposeText = intent.getStringExtra(Intent.EXTRA_TEXT);
        mBackupComposeText = mBackupComposeText.replace("\n", "<br>");
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

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        /*if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_choose_lang) {
            buildLangDialogList();
            return true;
        }else if (i == R.id.action_next)
        {
            proceedNext();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void buildLangDialogList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(NewPostActivity.this);
        builderSingle.setIcon(R.drawable.ic_language_black_24dp);
        builderSingle.setTitle("Select a Language:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewPostActivity.this, android.R.layout.select_dialog_singlechoice, AppConstants.languages);

        int selectedIndex = Arrays.asList(AppConstants.languages).indexOf(AppUtil.getString(this, AppConstants.PREFERRED_LANGUAGE, AppConstants.DEFAULT_LANGUAGE));
        builderSingle.setSingleChoiceItems(AppConstants.languages,selectedIndex,null);

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strName = arrayAdapter.getItem(which);
                getSupportActionBar().setTitle("New Thought"+" "+"("+strName+")");

                AlertDialog.Builder builderInner = new AlertDialog.Builder(NewPostActivity.this);
                //builderInner.setMessage(strName);
                AppUtil.putString(NewPostActivity.this, AppConstants.PREFERRED_LANGUAGE,strName);
                EventBus.getDefault().post(new MessageEvent("changed"));
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    public void proceedNext() {
        //sending values
        final String title = mTitleField.getText().toString().trim();
        final String dedicatedTo = mDedicatedToField.getText().toString().trim();
        final String courtesy = mCourtesyField.getText().toString().trim();
        final String image = mSetImageField.getText().toString().trim();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            //mTitleField.setError(REQUIRED);
            //return;
            mTitleField.setText("");
        }

        // Title is required
        if (TextUtils.isEmpty(image)) {
            //mTitleField.setError(REQUIRED);
            //return;
            mSetImageField.setText("");
        }

        if (TextUtils.isEmpty(dedicatedTo)) {
            mDedicatedToField.setText("");
        }

        if (TextUtils.isEmpty(courtesy)) {
            mCourtesyField.setText("");
        }


        if (TextUtils.isEmpty(mBackupComposeText)) {
            mBackupComposeText="";
        }

        // Disable button so there are no multi-posts
        //setEditingEnabled(false);
        Intent intent = new Intent(NewPostActivity.this,
                TextEditorActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("dedicated_to", dedicatedTo);
        intent.putExtra("courtesy", courtesy);
        intent.putExtra("ComposeText", mBackupComposeText);
        intent.putExtra("image", image);

        startActivityForResult(intent, AppConstants.SAVE_WRITE_POST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                System.out.println("SELECT_IMAGE");
                Uri selectedImageUri = data.getData();
                //selectedImageUri.getPath();
                selectedPath = getRealPathFromDocumentUri(NewPostActivity.this, selectedImageUri);
                System.out.println("SELECT_IMAGE Path : " + selectedPath);
                doFileUpload(selectedPath);
            }
        }
        if (requestCode == AppConstants.SAVE_WRITE_POST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                mBackupComposeText = bundle.get("ComposeText").toString();

                //mBodyField.fromHtml(data.getExtras().get("content").toString());
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            } else {
                finish();
            }
        }
    }

    private void doFileUpload(String selectedPath) {
        final FirebaseStorage storageRef = FirebaseStorage.getInstance();
        Uri file = Uri.fromFile(new File(selectedPath));
        //StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        StorageReference riversRef = storageRef.getReference(file.toString());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mSetImageField.setText(downloadUrl.toString());
            }
        });
    }
}

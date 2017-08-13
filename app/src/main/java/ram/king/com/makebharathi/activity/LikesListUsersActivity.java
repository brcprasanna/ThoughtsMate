package ram.king.com.makebharathi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.adapter.LikesListUsersAdapter;
import ram.king.com.makebharathi.models.UserForLikes;
import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;

public class LikesListUsersActivity extends BaseActivity {

    ArrayList<UserForLikes> usersList = new ArrayList<>();
    ArrayAdapter<UserForLikes> likesListUsersAdapter;

    ListView lvLikesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_likeslist);

        Intent intent = getIntent();
        usersList = (ArrayList<UserForLikes>) intent.getSerializableExtra(AppConstants.LIKES_LIST);

        lvLikesList = (ListView) findViewById(R.id.lvLikesUsers);


        if (usersList != null) {
            likesListUsersAdapter = new LikesListUsersAdapter(this, R.layout.list_users, usersList);
            lvLikesList.setAdapter(likesListUsersAdapter);
        }

        lvLikesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserForLikes user = (UserForLikes) lvLikesList.getItemAtPosition(i);
                String uID = user.uID;
                AppUtil.putString(LikesListUsersActivity.this, AppConstants.PREF_USER_POST_QUERY, uID);
                Intent intent = new Intent(LikesListUsersActivity.this, UserAllPostActivity.class);
                intent.putExtra(AppConstants.EXTRA_DISPLAY_NAME, user.displayName);
                startActivity(intent);
                //finish();
            }
        });
    }


}

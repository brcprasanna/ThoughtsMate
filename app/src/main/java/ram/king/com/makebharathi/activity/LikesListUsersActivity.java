package ram.king.com.makebharathi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.adapter.LikesListUsersAdapter;
import ram.king.com.makebharathi.models.User;
import ram.king.com.makebharathi.util.AppConstants;

public class LikesListUsersActivity extends BaseActivity {

    ArrayList<User> usersList = new ArrayList<>();
    ArrayAdapter<User> likesListUsersAdapter;

    ListView lvLikesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_likeslist);

        Intent intent = getIntent();
        usersList = (ArrayList<User>) intent.getSerializableExtra(AppConstants.LIKES_LIST);

        lvLikesList = (ListView) findViewById(R.id.lvLikesUsers);


        if (usersList != null) {
            likesListUsersAdapter = new LikesListUsersAdapter(this, R.layout.list_users, usersList);
            lvLikesList.setAdapter(likesListUsersAdapter);
        }
    }


}

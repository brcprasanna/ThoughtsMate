package ram.king.com.makebharathi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.User;

public class LikesListUsersAdapter extends ArrayAdapter<User> {

    private List<User> planetList;

    public LikesListUsersAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.list_users, null);
        }

        final User user = getItem(position);
        if (user != null) {
            //image
            CircularImageView photo = (CircularImageView) view.findViewById(R.id.user_photo);
            Glide.with(getContext()).load(user.photoUrl)
                    .into(photo);

            //display name
            TextView name = (TextView) view.findViewById(R.id.user);
            if (name != null) {
                name.setText(user.displayName);
            }
        }

        return view;
    }
}

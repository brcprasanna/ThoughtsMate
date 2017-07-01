package ram.king.com.makebharathi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.User;

public class CourtesyUsersAdapter extends ArrayAdapter<User> implements Filterable {

    private List<User> planetList;
    private Context context;
    private Filter planetFilter;
    private List<User> origPlanetList;

    public CourtesyUsersAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.planetList = objects;
        this.context = context;
        this.origPlanetList = objects;

    }

    public int getCount() {
        return planetList.size();
    }

    public User getItem(int position) {
        return planetList.get(position);
    }

    public long getItemId(int position) {
        return planetList.get(position).hashCode();
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

    public void resetData() {
        planetList = origPlanetList;
    }

    /*
	 * We create our filter
	 */

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter();

        return planetFilter;
    }

    private class PlanetFilter extends Filter {



        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origPlanetList;
                results.count = origPlanetList.size();
            }
            else {
                // We perform filtering operation
                List<User> nPlanetList = new ArrayList<User>();

                for (User p : origPlanetList) {
                    if (p.getDisplayName().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
                planetList = (List<User>) results.values;
                notifyDataSetChanged();

        }

    }
}

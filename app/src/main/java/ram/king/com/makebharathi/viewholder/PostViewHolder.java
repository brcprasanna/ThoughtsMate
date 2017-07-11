package ram.king.com.makebharathi.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.Post;
import ram.king.com.makebharathi.util.AppConstants;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public CircularImageView authorPhoto;
    public TextView titleView;
    public TextView authorView;
    public TextView date;
    public ImageView starView;
    public ImageView deleteView;
    public ImageView commentView;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView dedicatedTo;
    public TextView courtesy;
    public ImageView share;
    PrettyTime p;


    public PostViewHolder(View itemView) {
        super(itemView);
        authorPhoto = (CircularImageView) itemView.findViewById(R.id.post_author_photo);
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        date = (TextView) itemView.findViewById(R.id.post_date);
        starView = (ImageView) itemView.findViewById(R.id.star);
        deleteView = (ImageView) itemView.findViewById(R.id.delete);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        dedicatedTo = (TextView) itemView.findViewById(R.id.post_dedicated_to);
        courtesy = (TextView) itemView.findViewById(R.id.post_courtesy);
        commentView = (ImageView) itemView.findViewById(R.id.comment);
        share  = (ImageView)itemView.findViewById(R.id.share);

        p = new PrettyTime();

        titleView.setMaxLines(1);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener,
                           View.OnClickListener deleteClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);

        long yourmilliseconds = (long) post.timestamp;
        if (p != null)
            date.setText(p.format(new Date(yourmilliseconds)));

        if (!TextUtils.isEmpty(post.dedicatedTo)) {
            dedicatedTo.setVisibility(View.VISIBLE);
            dedicatedTo.setText("Dedicated To : " + post.dedicatedTo);
        }
        else {
            dedicatedTo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(post.courtesy)) {
            courtesy.setVisibility(View.VISIBLE);
            courtesy.setText("Courtesy : " + post.courtesy);
        }
        else {
            courtesy.setVisibility(View.GONE);
        }

        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        starView.setOnClickListener(starClickListener);
        deleteView.setOnClickListener(deleteClickListener);
    }
}

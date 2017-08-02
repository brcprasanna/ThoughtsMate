package ram.king.com.makebharathi.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.models.Post;


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
    public ImageButton more;
    public LinearLayout content;
    public TextView commentCountView;
    public RelativeLayout topUserLayout;
    PrettyTime p;


    public PostViewHolder(View itemView) {
        super(itemView);
        authorPhoto = (CircularImageView) itemView.findViewById(R.id.post_author_photo);
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        date = (TextView) itemView.findViewById(R.id.post_date);
        starView = (ImageView) itemView.findViewById(R.id.button_star);
        deleteView = (ImageView) itemView.findViewById(R.id.delete);
        numStarsView = (TextView) itemView.findViewById(R.id.star_count);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        dedicatedTo = (TextView) itemView.findViewById(R.id.post_dedicated_to);
        courtesy = (TextView) itemView.findViewById(R.id.post_courtesy);
        commentView = (ImageView) itemView.findViewById(R.id.button_comment);
        share = (ImageView) itemView.findViewById(R.id.button_share);
        more = (ImageButton) itemView.findViewById(R.id.more);
        content = (LinearLayout) itemView.findViewById(R.id.content_layout);
        commentCountView = (TextView) itemView.findViewById(R.id.comment_count);
        topUserLayout = (RelativeLayout) itemView.findViewById(R.id.post_author_layout);
        p = new PrettyTime();

    }

    public void bindToPost(Post post, View.OnClickListener starClickListener,
                           View.OnClickListener deleteClickListener, View.OnClickListener moreClickListener, View.OnClickListener contentListener,
                           View.OnClickListener commentListener, View.OnClickListener shareListener, View.OnClickListener topUserLayoutListener) {
        if (!TextUtils.isEmpty(post.title)) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText("Title : " + post.title);
        } else {
            titleView.setVisibility(View.GONE);
        }

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

        if (post.starCount > 0) {
            numStarsView.setVisibility(View.VISIBLE);
            numStarsView.setText(String.valueOf(post.starCount));
        } else
            numStarsView.setVisibility(View.GONE);

        bodyView.setText(Html.fromHtml(post.body), TextView.BufferType.SPANNABLE);
        starView.setOnClickListener(starClickListener);
        deleteView.setOnClickListener(deleteClickListener);
        more.setOnClickListener(moreClickListener);
        content.setOnClickListener(contentListener);
        commentView.setOnClickListener(commentListener);
        share.setOnClickListener(shareListener);
        topUserLayout.setOnClickListener(topUserLayoutListener);
    }
}

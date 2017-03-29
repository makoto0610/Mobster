package Objects.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.the_great_amoeba.mobster.R;

import java.util.List;

import Objects.Comment;
import Objects.DisplayQuestion;

/**
 * Created by anireddy on 3/29/17.
 */

public class CustomListViewAdapterComment extends ArrayAdapter<Comment> {


    Context context;

    public CustomListViewAdapterComment(Context context, int resourceId, //resourceId=your layout
                                 List<Comment> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView commentText;
        TextView userText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CustomListViewAdapterComment.ViewHolder holder = null;
        Comment c = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view_comment, null);
            holder = new CustomListViewAdapterComment.ViewHolder();
            holder.commentText = (TextView) convertView.findViewById(R.id.commentText);
            holder.userText = (TextView) convertView.findViewById(R.id.userText);
            convertView.setTag(holder);
        } else
            holder = (CustomListViewAdapterComment.ViewHolder) convertView.getTag();

        holder.commentText.setText(c.getComment());
        holder.userText.setText(c.getUser() + ": ");

        return convertView;
    }
}
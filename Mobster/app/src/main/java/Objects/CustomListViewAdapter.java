package Objects;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.the_great_amoeba.mobster.R;

import java.util.List;

/**
 * Created by anireddy on 2/14/17.
 */

public class CustomListViewAdapter extends ArrayAdapter<DisplayQuestion> {


    Context context;

    public CustomListViewAdapter(Context context, int resourceId, //resourceId=your layout
                             List<DisplayQuestion> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageUpVote;
        ImageView imageDownVote;
        TextView textQuestion;
        TextView textDuration;
        TextView textRating;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        DisplayQuestion question = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view, null);
            holder = new ViewHolder();
            holder.imageUpVote = (ImageView) convertView.findViewById(R.id.imageView_upVote);
            holder.imageDownVote = (ImageView) convertView.findViewById(R.id.imageView_downVote);
            holder.textQuestion = (TextView) convertView.findViewById(R.id.textView_Question);
            holder.textDuration = (TextView) convertView.findViewById(R.id.textView_Duration);
            holder.textRating = (TextView) convertView.findViewById(R.id.textView_Rating);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textQuestion.setText(question.getQuestion());
        holder.textRating.setText(question.getRating() + "");
        holder.textDuration.setText(question.getDuration().toStandardHours().toString());

        return convertView;
    }
}
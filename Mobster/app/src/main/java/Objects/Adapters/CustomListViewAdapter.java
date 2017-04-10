package Objects.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.the_great_amoeba.mobster.R;

import org.joda.time.Duration;

import java.util.List;

import Constants.Constant;
import Objects.DisplayQuestion;

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
        ImageView imageFavorite;
        ImageView imageFlag;
        TextView textQuestion;
        TextView textDuration;
        TextView textRating;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayQuestion question = getItem(position);

        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view, null);
            holder = new ViewHolder();
            holder.imageFavorite = (ImageView) convertView.findViewById(R.id.imageView_favorite);
            holder.imageFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.imageFavorite.setImageResource(R.drawable.ic_star);
                }
            });

            holder.imageFlag = (ImageView) convertView.findViewById(R.id.imageView_flag);
            holder.imageFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //handle code for flag button press
                }
            });

            holder.textQuestion = (TextView) convertView.findViewById(R.id.tv_Map_Question);
            holder.textDuration = (TextView) convertView.findViewById(R.id.textView_Duration);
            holder.textRating = (TextView) convertView.findViewById(R.id.textView_Rating);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textQuestion.setText(question.getQuestion());
        holder.textRating.setText(question.getRating() + "");

        holder.textDuration.setText(getDurationDisplay(question.getDuration()));

        return convertView;
    }

    private String getDurationDisplay(Duration duration) {
        long days = duration.getStandardDays();
        long hours = duration.getStandardMinutes();
        long minutes = duration.getStandardMinutes();
        long seconds = duration.getStandardSeconds();
        if (days != 0) {
            return days + " days";
        } else if (hours != 0) {
            return hours + " hours";
        }
        if (minutes <= 0 && seconds > 0) {
            minutes = 1;
        }
        return minutes + " minutes";
    }
}
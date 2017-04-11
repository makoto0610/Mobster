package Objects.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.the_great_amoeba.mobster.R;
import com.the_great_amoeba.mobster.SaveSharedPreferences;

import org.joda.time.Duration;

import java.util.LinkedList;
import java.util.List;

import Constants.Constant;
import Helper.Log;
import Objects.DisplayQuestion;

/**
 * Created by anireddy on 2/14/17.
 */

public class CustomListViewAdapter extends ArrayAdapter<DisplayQuestion> {


    Context context;
    private DatabaseReference mDatabase;


    public CustomListViewAdapter(Context context, int resourceId, //resourceId=your layout
                             List<DisplayQuestion> items) {
        super(context, resourceId, items);
        this.context = context;
        this.mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);

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
        final DisplayQuestion question = getItem(position);

        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {


            convertView = mInflater.inflate(R.layout.list_view, null);
            holder = new ViewHolder();


            holder.imageFavorite = (ImageView) convertView.findViewById(R.id.imageView_favorite);

            //set the star and flag ImageViews based on whether the user has favorited/flagged the question already
            final String currentUser = SaveSharedPreferences.getUserName(getContext());

            if (question.getFavoritedUsers().contains(currentUser)) {
                holder.imageFavorite.setImageResource(R.drawable.ic_star);
            }


            holder.imageFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (question.getFavoritedUsers().contains(currentUser)) {
                        //if the user has alreadyFavorited, we must "unfavorite"


                        //begin unfavorite transaction
                        DatabaseReference accessFavorite = mDatabase.child("questions").child(question.getQuestionId()).child("num_favorites");
                        accessFavorite.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                //Helper.Log.d(Constant.DEBUG, "in doTransaction()");
                                Long currentValue = (Long) mutableData.getValue();
                                if (currentValue == null) {
                                    //This should never happen
                                    mutableData.setValue(0);
                                } else {
                                    mutableData.setValue(currentValue - 1);
                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                if (databaseError == null) {
                                    Helper.Log.d(Constant.DEBUG, "Transaction finished.");
                                    String currUser = SaveSharedPreferences.getUserName(getContext());
                                    Query contain = mDatabase.child("questions").child(question.getQuestionId()).child("favoritedUsers")
                                            .orderByKey();

                                    contain.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //find corresponding user in DB list
                                            DataSnapshot snapshot = dataSnapshot;
                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                String user = (String) postSnapshot.getValue();
                                                Log.d(Constant.DEBUG, user);
                                                Log.d(Constant.DEBUG, postSnapshot.getKey());
                                                if (user.equals(currentUser)) {
                                                    snapshot = postSnapshot;
                                                    break;
                                                }
                                            }
                                            //remove from DB list
                                            Log.d(Constant.DEBUG, snapshot.getKey());
                                            Log.d(Constant.DEBUG, snapshot.getValue().toString());
                                            mDatabase.child("questions").child(question.getQuestionId()).child("favoritedUsers").child(snapshot.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    holder.imageFavorite.setImageResource(R.drawable.ic_star_border);

                                                    question.setRating(question.getRating() - 1);
                                                    question.getFavoritedUsers().remove(currentUser);
                                                    holder.textRating.setText(question.getRating() + "");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //TODO: Unfavoriting error
                                        }
                                    });

                                }
                                else Helper.Log.d(Constant.DEBUG, "Transaction finished w/ database error " + databaseError.toString());
                            }
                        });


                    } else {
                        holder.imageFavorite.setImageResource(R.drawable.ic_star);

                        question.setRating(question.getRating() + 1);
                        holder.textRating.setText(question.getRating() + "");

                        //begin favorite transaction
                        DatabaseReference accessFavorite = mDatabase.child("questions").child(question.getQuestionId()).child("num_favorites");
                        accessFavorite.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                //Helper.Log.d(Constant.DEBUG, "in doTransaction()");
                                Long currentValue = (Long) mutableData.getValue();
                                if (currentValue == null) {
                                    //Helper.Log.d(Constant.DEBUG, "doTransaction() data returned null");
                                    mutableData.setValue(1);
                                } else {
                                    mutableData.setValue(currentValue + 1);
                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                if (databaseError == null) {
                                    Helper.Log.d(Constant.DEBUG, "Transaction finished.");
                                    DatabaseReference votedUsersRef = mDatabase.child("questions").child(question.getQuestionId())
                                            .child("favoritedUsers").push();
                                    votedUsersRef.setValue(currentUser);
                                    question.getFavoritedUsers().add(currentUser);
                                } else
                                    Helper.Log.d(Constant.DEBUG, "Transaction finished w/ database error " + databaseError.toString());
                            }
                        });
                    }


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
        long hours = duration.getStandardHours();
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

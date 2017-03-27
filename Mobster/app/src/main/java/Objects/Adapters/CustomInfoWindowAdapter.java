package Objects.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.the_great_amoeba.mobster.R;


import Constants.Constant;
import Objects.DisplayQuestion;

/**
 * Created by anireddy on 2/19/17.
 */

/**
 * I don't know what this class does anymore - Ani
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View layout;
    public DisplayQuestion dq;
    ViewHolder vh;

    /*private view holder class*/
    private class ViewHolder {
        TextView textQuestion;
        TextView snippet;
    }

    public void setDq(DisplayQuestion dq) {
        this.dq = dq;
    }

    public CustomInfoWindowAdapter(Context context) {
        LayoutInflater li = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.layout = li.inflate(R.layout.info_window, null);

    }


    @Override
    public View getInfoWindow(Marker marker) {
//        Helper.Log.d(Constant.DEBUG, "in getInfoWindow()");
//        populateLayout();
//        return layout;
        return null;
    }

    public void populateLayout() {
        vh = new ViewHolder();
//        vh.textQuestion = (TextView) ...


        if (this.dq == null) {
            Helper.Log.d(Constant.DEBUG, "getInfoWindow called w/ null Question");
            return;
        }

        vh.textQuestion.setText(dq.getQuestion());

        layout.setTag(vh);
    }



    @Override
    public View getInfoContents(Marker marker) {
        Helper.Log.d(Constant.DEBUG, "in getInfoContents()");
        vh = new ViewHolder();

        String title = marker.getTitle();
        String snippet = marker.getSnippet();

        vh.textQuestion = (TextView) layout.findViewById(R.id.tv_Map_Question);
        SpannableString spannableTitle = new SpannableString(marker.getTitle());
        spannableTitle.setSpan(new ForegroundColorSpan(Color.rgb(13, 93, 90)), 0, spannableTitle.length(), 0);
        vh.textQuestion.setText(spannableTitle);

        vh.snippet = (TextView) layout.findViewById(R.id.tv_Map_Duration);
        vh.snippet.setText(marker.getSnippet());
        vh.snippet.setTextColor(Color.BLACK);
        layout.setTag(vh);

        return layout;
    }
}

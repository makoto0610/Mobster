package Objects.Adapters;

import android.app.Activity;
import android.content.Context;
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

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View layout;
    public DisplayQuestion dq;
    ViewHolder vh;

    /*private view holder class*/
    private class ViewHolder {
        RelativeLayout rl;
        TextView textQuestion;
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
        Helper.Log.d(Constant.DEBUG, "in getInfoWindow()");
        populateLayout();
        return layout;
    }

    public void populateLayout() {
        vh = new ViewHolder();
        vh.rl = (RelativeLayout) layout.findViewById(R.id.rl_map);
        vh.textQuestion = (TextView) vh.rl.findViewById(R.id.tv_Map_Question);


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
        return null;
    }
}

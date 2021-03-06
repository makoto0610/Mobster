package com.the_great_amoeba.mobster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;

/**
 * Rules fragment class.
 *
 * @author Christine
 * @version 1.0
 */
public class RulesFragment extends Fragment {

    private HashMap<Integer, String> map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initDetails();
        View view = inflater.inflate(R.layout.rules_layout, container, false);
        ListView lv = (ListView) view.findViewById(R.id.faq_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onRuleClick(getView(), position);
            }
        });
        return view;
    }

    /**
     * On click method for the rule.
     * @param view current view.
     * @param pos position of the click.
     */
    private void onRuleClick(View view, int pos) {
        AlertDialog details = new AlertDialog.Builder(getActivity()).create();
        details.setTitle("Answer");
        details.setMessage(map.get(pos));
        details.setButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        details.show();
    }

    /**
     * Initialize all the contents of the rules.
     */
    private void initDetails() {
        map = new HashMap<>();
        map.put(0, "1. No personal information should be disclosed. \n\n"
                + "2. No profanity. \n\n"
                + "Any questions that violate these rules will be removed.");
        map.put(1, "Ask a question by selecting the icon at the top right corner.");
        map.put(2, "Answer a question by selecting the question you want to answer " +
                ", choosing the option(s) that you want, and then pressing 'Submit'.");
        map.put(3, "You can upvote a question if you think it's interesting and want it to "
                + "gain visibility.");
        map.put(4, "If you you allow Mobster access to your location, you will be able to "
            + "view questions near your geographical location.");
    }
}

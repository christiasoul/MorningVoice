package edu.cogswell.morningvoice;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Christian on 4/12/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private String[] groups;
    private String[][] children;
    private String[] group_values;
    private String[][] children_values;
    private boolean [][] checkedState;
    private Context _context;


    public ExpandableListAdapter(Context context, String[] newGroups, String[] newGroupVals,
                                 String[][] newChildren, String[][] newChildrenValues, boolean [][] checked) {
        groups = newGroups;
        children = newChildren;
        group_values = newGroupVals;
        children_values = newChildrenValues;
        checkedState = checked;
        _context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPositon) {
        return children[groupPosition][childPositon];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View grid;

        final int grpPos = groupPosition;
        final int childPos = childPosition;

        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.list_item, parent, false);

        TextView header = (TextView)grid.findViewById(R.id.lblListHeader);
        header.setText(getChild(groupPosition, childPosition).toString());

        final View tick = grid.findViewById(R.id.image);

        if(checkedState[grpPos][childPos])
            tick.setVisibility(View.VISIBLE);
        else
            tick.setVisibility(View.GONE);


        grid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkedState[grpPos][childPos] ^= true;
                tick.setVisibility(View.VISIBLE);
                Options.getInstance().setBool(grpPos, childPos, checkedState[grpPos][childPos]);
                // have check for if the main is clicked, all others become gray and not bold
                // Also edit file version of item
            }
        });


        return grid;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View grid;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.list_group, null);
        }else{
            grid = convertView;
        }

        TextView lblListHeader = (TextView) grid
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(getGroup(groupPosition).toString());

        return grid;
    }

    @Override
    public boolean hasStableIds() {
        return true; // !!!! IF VIEW GETS WEIRD CHANGE   !!!! ^^^^^^^^^^^^^^^
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

package com.hdfc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.DependentModel;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class DependentViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public MultiBitmapLoader multiBitmapLoader;
    private Context _ctxt;
    private ArrayList data;

    public DependentViewAdapter(Context ctxt, ArrayList d) {
        _ctxt = ctxt;
        data = d;
        inflater = (LayoutInflater) _ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        multiBitmapLoader = new MultiBitmapLoader(_ctxt);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.dependent_list, null);

            holder = new ViewHolder();
            holder.textName = (TextView) vi.findViewById(R.id.textViewName);
            holder.textRelation = (TextView) vi.findViewById(R.id.textViewRealtion);
            holder.image = (ImageView) vi.findViewById(R.id.imageViewDpndt);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() > 0) {

            try {

                DependentModel dependentModel = (DependentModel) data.get(position);

                holder.textName.setText(dependentModel.getStrName());

                if (!dependentModel.getStrName().equalsIgnoreCase(_ctxt.getResources().getString(R.string.add_dependent))
                        && !dependentModel.getStrRelation().equalsIgnoreCase("")) {
                    holder.textRelation.setVisibility(View.VISIBLE);
                    holder.textRelation.setText(dependentModel.getStrRelation());

                    if (!dependentModel.getStrImagePath().equalsIgnoreCase("")) {
                        multiBitmapLoader.loadBitmap(dependentModel.getStrImagePath().trim(),
                                holder.image);
                    }

                } else {
                    Bitmap imageBitmap = BitmapFactory.decodeResource(_ctxt.getResources(),
                            R.drawable.plus_icon);

                    holder.textRelation.setVisibility(View.GONE);
                    holder.image.setImageBitmap(imageBitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final View.OnClickListener makeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String strName =
                            ((TextView) v.findViewById(R.id.textViewName)).getText().toString();

                    if (strName != null && strName.equalsIgnoreCase(_ctxt.getResources().getString(R.string.add_dependent))) {
                        Intent selection = new Intent(_ctxt, DependentDetailPersonalActivity.class);
                        ((Activity) _ctxt).finish();
                        _ctxt.startActivity(selection);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        vi.setOnClickListener(makeListener);

        return vi;
    }

    public static class ViewHolder {
        public TextView textName;
        public TextView textRelation;
        public ImageView image;
    }
}

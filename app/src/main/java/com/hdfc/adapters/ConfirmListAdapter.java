package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ClientModel;
import com.hdfc.models.ConfirmCustomerModel;
import com.hdfc.models.ConfirmDependentModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ConfirmListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    //private List<ConfirmCustomerModel> _listDataHeader;
    private List<CustomerModel> _listDataHeader;
    // child data in format of header title, child title
//    private HashMap<ConfirmCustomerModel, List<ConfirmDependentModel>> _listDataChild;
    private HashMap<CustomerModel, List<DependentModel>> _listDataChild;
    private Utils utils;
    private MultiBitmapLoader multiBitmapLoader;
//    private ExpandableListView expListView;

    public ConfirmListAdapter(){
    }

    public ConfirmListAdapter(Context context, List<CustomerModel> listDataHeader,
                              HashMap<CustomerModel, List<DependentModel>> listChildData) {
        this._context = context;
        utils = new Utils(_context);
        multiBitmapLoader = new MultiBitmapLoader(_context);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final DependentModel confirmDependentModel = (DependentModel) getChild(groupPosition, childPosition);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_dependents, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.client = (ImageView) convertView.findViewById(R.id.imageClients);
            viewHolder.age = (TextView)convertView.findViewById(R.id.textViewClient_age);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.client.setTag(confirmDependentModel);

        viewHolder.name.setText(confirmDependentModel.getStrName());
        viewHolder.address.setText(confirmDependentModel.getStrAddress());
        viewHolder.age.setText("60");

        System.out.println("Enhance your app : "+confirmDependentModel.getStrImagePath());
        File fileImage = new File(confirmDependentModel.getStrImagePath());
        //  File fileImage = Utils.createFileInternal("images/" + "rushikesh_img");

        if (fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.client);
        } else {
            viewHolder.client.setImageDrawable(_context.getResources().getDrawable(R.drawable.person_icon));
        }

        viewHolder.client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Config.dependentModel = (DependentModel) v.getTag();
                Config.customerModel = null;

                Intent intent = new Intent(_context, DependentDetailPersonalActivity.class);
                _context.startActivity(intent);
            }
        });

//        TextView txtListChild = (TextView) convertView
//                .findViewById(R.id.lblListItem);
//
//        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
//        final CustomerModel confirmCustomerModel = (CustomerModel) getGroup(groupPosition);
        final CustomerModel customerModel = (CustomerModel)getGroup(groupPosition);

        ExpandableListView expandableListView = (ExpandableListView)parent;
        expandableListView.expandGroup(groupPosition);

        System.out.println("Welcome : "+customerModel);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_customers, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.contact = (TextView)convertView.findViewById(R.id.textViewContact);
            viewHolder.customer = (ImageView) convertView.findViewById(R.id.imageClients);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*viewHolder.name.setText(confirmCustomerModel.getStrName());
        viewHolder.contact.setText(confirmCustomerModel.getStrContacts());
        viewHolder.address.setText(confirmCustomerModel.getStrAddress());*/

        viewHolder.name.setText(customerModel.getStrName());
        viewHolder.contact.setText(customerModel.getStrContacts());
        viewHolder.address.setText(customerModel.getStrAddress());

       // File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(confirmCustomerModel.getStrCustomerID()));
        //File fileImage = new File(customerModel.getStrImgPath());
        System.out.println("Yogiraj");

       File fileImage = new File("/storage/sdcard0/Android/data/com.hdfc.caretaker/files/Pictures/1464327754189.jpeg");

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.customer);
        }else{
            viewHolder.customer.setImageDrawable(_context.getResources().getDrawable(R.drawable.carla1));
        }

        viewHolder.customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config.customerModel = (CustomerModel) v.getTag();
                Config.dependentModel = null;
              /*  Intent intent = new Intent(_context, ClientProfileActivity.class);
                _context.startActivity(intent);*/
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public  class ViewHolder{
        TextView name, address, contact, age;
        ImageView client, customer;
    }
}
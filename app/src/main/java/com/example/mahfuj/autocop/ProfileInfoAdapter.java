package com.example.mahfuj.autocop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ProfileInfoAdapter extends BaseAdapter {
    private Context activity;
    private ArrayList<RegistrationInfo> userInfo = new ArrayList<>();
    private LayoutInflater layoutInflater = null;

    private static class ViewHolder{
        private TextView name, father_name, mobile_no, email, vehicle_no, pass;
    }
    private ViewHolder viewHolder = null;
    public ProfileInfoAdapter(Context ctx, ArrayList<RegistrationInfo> userInfo) {
        activity = ctx;
        this.userInfo = userInfo;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return userInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        final int pos = position;
        if(view == null){
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.row_cell_user,null);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.father_name = view.findViewById(R.id.father_name);
            viewHolder.mobile_no = view.findViewById(R.id.mob_no);
            viewHolder.email= view.findViewById(R.id.email);
            viewHolder.vehicle_no= view.findViewById(R.id.veh_no);
           viewHolder.pass= view.findViewById(R.id.pass_word);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(userInfo.get(pos).getFull_name());
        viewHolder.father_name.setText(userInfo.get(pos).getFather());
        viewHolder.mobile_no.setText(userInfo.get(pos).getMobile_number());
        viewHolder.email.setText(userInfo.get(pos).getEmail());
        viewHolder.vehicle_no.setText(userInfo.get(pos).getVehicle_no());
        viewHolder.pass.setText(userInfo.get(pos).getPass());
        return view;

}}

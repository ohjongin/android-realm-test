package me.ji5.realmtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.ji5.realmtest.R;
import me.ji5.realmtest.model.Contact;


public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_item_contact, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
            viewHolder.ivFace = (ImageView) convertView.findViewById(R.id.iv_face);
            viewHolder.ivPhoneType = (ImageView) convertView.findViewById(R.id.iv_type);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = getItem(position);

        viewHolder.ivFace.setImageResource(android.R.drawable.gallery_thumb);

        viewHolder.tvName.setText(contact.getDisplayName());
        if (contact.getPhones().size() > 0) {
            viewHolder.tvPhone.setText(contact.getPhones().get(0).getPhoneNumber());
        }

        viewHolder.ivPhoneType.setVisibility(View.GONE);

        return convertView;
    }

    public class ViewHolder {
        public CheckBox chk;
        TextView tvName;
        TextView tvPhone;
        ImageView ivFace;
        TextView tvContactType;
        TextView tvInitial;
        ImageView ivPhoneType;
    }
}

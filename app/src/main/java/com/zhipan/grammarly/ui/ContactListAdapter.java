package com.zhipan.grammarly.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhipan.grammarly.R;
import com.zhipan.grammarly.data.model.Contact;
import com.zhipan.grammarly.util.Constants;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView name;
        private TextView phone;

        public ContactViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.contact_avatar);
            name = itemView.findViewById(R.id.contact_display_name);
            phone = itemView.findViewById(R.id.contact_phone_number);
        }
    }

    private static final String TAG = ContactListAdapter.class.getSimpleName();
    private static final String AVATAR_ID_DEFAULT = "an_avatar";

    private RecyclerViewItemClickListener itemClickListener;

    private List<Contact> contacts;

    ContactListAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,
                parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        String avatarId = contact.getAvatarId();
        if (avatarId == null)
            avatarId = AVATAR_ID_DEFAULT;

        String avatarUrl = String.format(Constants.AVATAR_BASE_URL, avatarId);

        Picasso.get().load(avatarUrl).error(R.drawable.an_avatar).fit().into(holder.avatar);

        holder.name.setText(contact.getDisplayName());
        holder.phone.setText(contact.getPhoneNumberList().get(0));

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(v, contact.getId()));
    }

    @Override
    public int getItemCount() {
        if (contacts == null)
            return 0;

        return contacts.size();
    }

    public void setItemClickListener(RecyclerViewItemClickListener listener) {
        itemClickListener = listener;
    }
}

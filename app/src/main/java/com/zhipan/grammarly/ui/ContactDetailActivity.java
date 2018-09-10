package com.zhipan.grammarly.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhipan.grammarly.R;
import com.zhipan.grammarly.data.model.Contact;
import com.zhipan.grammarly.di.AppModule;
import com.zhipan.grammarly.di.ContactsModule;
import com.zhipan.grammarly.di.DaggerAppComponent;
import com.zhipan.grammarly.util.Constants;
import com.zhipan.grammarly.viewmodel.ContactListViewModel;
import com.zhipan.grammarly.viewmodel.ContactListViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ContactDetailActivity extends AppCompatActivity {

    private ContactListViewModel contactListViewModel;

    @Inject
    ContactListViewModelFactory contactListViewModelFactory;


    private ImageView avatar;
    private TextView displayName;
    private TextView phoneNumber;
    private TextView emailAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> supportFinishAfterTransition());

        avatar = findViewById(R.id.contact_avatar);
        displayName = findViewById(R.id.contact_display_name);
        phoneNumber = findViewById(R.id.contact_phone_number);
        emailAddress = findViewById(R.id.contact_email_address);

        DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .contactsModule(new ContactsModule(this))
                .build()
                .inject(this);

        contactListViewModel = ViewModelProviders.of(this, contactListViewModelFactory)
                .get(ContactListViewModel.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.INTENT_KEY_CONTACT_ID)) {
            String contactId = intent.getStringExtra(Constants.INTENT_KEY_CONTACT_ID);
            contactListViewModel.setContactIdForDetailPage(contactId);
            String avatarTransitionName = String.format(Constants.AVATAR_TRANSITION_NAME, contactId);
//            String nameTransitionName = String.format(Constants.DISPLAY_NAME_TRANSITION_NAME, contactId);
            avatar.setTransitionName(avatarTransitionName);
//            displayName.setTransitionName(nameTransitionName);
        }

        contactListViewModel.getContactList().observe(this, this::updateContactDetails);
        updateContactDetails(contactListViewModel.getContactList().getValue());
    }

    private void updateContactDetails(List<Contact> contactList) {
        String contactId = contactListViewModel.getContactIdForDetailPage();
        Contact contact = null;
        if (contactId != null && contactList != null) {
            for (Contact c : contactList) {
                if (contactId.equals(c.getId())) {
                    contact = c;
                    break;
                }
            }
        }

        if (contact == null) {
            //TODO: handle invalid contact data

        } else {
            Picasso.get()
                    .load(String.format(Constants.AVATAR_BASE_URL, contact.getAvatarId()))
                    .error(R.drawable.an_avatar)
                    .fit()
                    .into(avatar);

            if (contact.getDisplayName() == null) {
                displayName.setVisibility(View.GONE);
            } else {
                displayName.setVisibility(View.VISIBLE);
                displayName.setText(contact.getDisplayName());
            }

            if (contact.getPhoneNumberList() == null || contact.getPhoneNumberList().size() == 0) {
                phoneNumber.setVisibility(View.GONE);
            } else {
                phoneNumber.setVisibility(View.VISIBLE);
                StringBuilder phoneNumbers = new StringBuilder(contact.getPhoneNumberList().size());

                for (String num : contact.getPhoneNumberList()) {
                    phoneNumbers.append(num).append('\n');
                }

                phoneNumber.setText(phoneNumbers);
            }

            View emailTitle = findViewById(R.id.contact_email_address_title);
            if (contact.getEmailList() == null || contact.getEmailList().size() == 0) {
                emailTitle.setVisibility(View.GONE);
                emailAddress.setVisibility(View.GONE);
            } else {
                emailTitle.setVisibility(View.VISIBLE);
                emailAddress.setVisibility(View.VISIBLE);
                StringBuilder emails = new StringBuilder(contact.getEmailList().size());

                for (String email : contact.getEmailList()) {
                    emails.append(email).append('\n');
                }
                emailAddress.setText(emails);
            }
        }
    }
}

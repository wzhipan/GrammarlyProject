package com.zhipan.grammarly.ui;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zhipan.grammarly.R;
import com.zhipan.grammarly.data.model.Contact;
import com.zhipan.grammarly.di.DaggerAppComponent;
import com.zhipan.grammarly.di.AppComponent;
import com.zhipan.grammarly.di.AppModule;
import com.zhipan.grammarly.di.ContactsModule;
import com.zhipan.grammarly.util.Constants;
import com.zhipan.grammarly.viewmodel.ContactListViewModel;
import com.zhipan.grammarly.viewmodel.ContactListViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ContactListActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

    private RecyclerView contactList;
    private ContactListAdapter contactListAdapter;

    private ContactListViewModel contactListViewModel;

    @Inject
    ContactListViewModelFactory contactListViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .contactsModule(new ContactsModule(this))
                .build();
        appComponent.inject(this);

        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.permission_needed_read_contacts)
                        .setPositiveButton(R.string.ok_button, (dialog, which) -> {
                            requestPermissions();
                        }).setNegativeButton(R.string.cancel_button, (dialog, which) -> {
                            handlePermissionsDenied();
                        })
                        .create()
                        .show();
            } else {
                requestPermissions();
            }
        } else {
            handlePermissionsGranted();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_READ_CONTACTS);
    }

    private void handlePermissionsDenied() {
        Snackbar snackBar = Snackbar.make(findViewById(R.id.contact_list_layout),
                R.string.permission_denied_read_contact,
                Snackbar.LENGTH_INDEFINITE);

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            snackBar.setAction(R.string.grant_button, v -> {requestPermissions();});
        }

        snackBar.show();
    }

    private void handlePermissionsGranted() {
        contactList = findViewById(R.id.contact_list);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        contactList.setLayoutManager(lm);
        contactList.setHasFixedSize(true);

        contactListViewModel = ViewModelProviders.of(this, contactListViewModelFactory).get(ContactListViewModel.class);

        LiveData<List<Contact>> contacts = contactListViewModel.getContactList();

        contactListAdapter = new ContactListAdapter(contacts.getValue());

        contacts.observe(this, (newContacts -> {
            contactListAdapter.setContacts(newContacts);
        }));
        contactList.setAdapter(contactListAdapter);

        contactListAdapter.setItemClickListener((v, id) -> startContactDetailActivity(v, (String)id));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    handlePermissionsDenied();
                } else {
                    handlePermissionsGranted();
                }
                break;
            default:
                break;
        }
    }

    private void startContactDetailActivity(View view, String contactId) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(Constants.INTENT_KEY_CONTACT_ID, contactId);

        ActivityOptionsCompat options = null;

        if (view != null) {
            View avatar = view.findViewById(R.id.contact_avatar);
            View name = view.findViewById(R.id.contact_display_name);
            String avatarTransitionName = String.format(Constants.AVATAR_TRANSITION_NAME, contactId);
//            String nameTransitionName = String.format(Constants.DISPLAY_NAME_TRANSITION_NAME, contactId);
            ViewCompat.setTransitionName(avatar, avatarTransitionName);
//            ViewCompat.setTransitionName(name, nameTransitionName);

            Pair<View, String> p1 = new Pair<>(avatar, avatarTransitionName);
//            Pair<View, String> p2 = new Pair<>(name, nameTransitionName);

            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
        }

        if (options != null)
            startActivity(intent, options.toBundle());
        else
            startActivity(intent);
    }
}

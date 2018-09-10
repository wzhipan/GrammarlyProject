package com.zhipan.grammarly.data;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.zhipan.grammarly.data.model.Contact;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContactsFetcher {
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Contactables.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Contactables.HAS_PHONE_NUMBER
    };

    private static final String SELECTION = ContactsContract.CommonDataKinds.Contactables.HAS_PHONE_NUMBER+" <> 0";
    private static final String SORT_ORDER = ContactsContract.CommonDataKinds.Contactables.CONTACT_ID;

    private MutableLiveData<List<Contact>> mContacts;
    private Context mContext;

    @Inject
    ContactsFetcher(Context context) {
        mContext = context;
        mContacts = new MutableLiveData<>();
    }

    public MutableLiveData<List<Contact>> getContacts() {
        return mContacts;
    }

    public void fetchContacts() {
        ContentResolver cr = mContext.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        Uri queryUri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;

        try (Cursor cur = cr.query(queryUri, PROJECTION, SELECTION, null, SORT_ORDER)) {

            if ((cur != null ? cur.getCount() : 0) > 0) {

                int contactIdColIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.CONTACT_ID);
                int dispNameColIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int mimeTypeColIndex = cur.getColumnIndex(ContactsContract.Data.MIMETYPE);
                int phoneNumColIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int emailColIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);

                Contact contact = null;
                while (cur.moveToNext()) {
                    String id = cur.getString(contactIdColIndex);

                    if (contact == null || !contact.getId().equals(id)) {

                        if (contact != null)
                            contacts.add(contact);

                        contact = new Contact(id);
                    }

                    String name = cur.getString(dispNameColIndex);
                    String mimeType = cur.getString(mimeTypeColIndex);
                    contact.setDisplayName(name);

                    if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                        String phone = cur.getString(phoneNumColIndex);
                        contact.getPhoneNumberList().add(phone);
                    } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
                        String email = cur.getString(emailColIndex);
                        contact.getEmailList().add(email);
                    }
                }

                if (contact != null)
                    contacts.add(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContacts.postValue(contacts);
    }
}

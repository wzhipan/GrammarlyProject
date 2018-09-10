package com.zhipan.grammarly.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zhipan.grammarly.data.model.Contact;
import com.zhipan.grammarly.data.ContactsRepository;

import java.util.List;

import javax.inject.Inject;

public class ContactListViewModel extends ViewModel {

    private static final String TAG = ContactListViewModel.class.getSimpleName();

    private ContactsRepository contactsRepository;

    private String contactIdForDetailPage;

    @Inject
    public ContactListViewModel(ContactsRepository repository) {
        contactsRepository = repository;
    }

    public LiveData<List<Contact>> getContactList() {
        return contactsRepository.getContacts();
    }

    public String getContactIdForDetailPage() {
        return contactIdForDetailPage;
    }

    public void setContactIdForDetailPage(String id) {
        contactIdForDetailPage = id;
    }
}

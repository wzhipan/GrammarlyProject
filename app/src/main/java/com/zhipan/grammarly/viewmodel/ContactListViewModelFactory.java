package com.zhipan.grammarly.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zhipan.grammarly.data.ContactsRepository;

import javax.inject.Inject;

public class ContactListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private ContactsRepository mRepository;

    @Inject
    public ContactListViewModelFactory(ContactsRepository repository) {
        this.mRepository = repository;
        Log.i("ViewModelFactory", "creating ContactListViewModelFactory: repo is empty? "+(mRepository == null));
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        Log.i("ViewModelFactory", "creating instance: repo is empty? "+(mRepository == null));
        return (T) new ContactListViewModel(mRepository);
    }
}

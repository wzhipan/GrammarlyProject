package com.zhipan.grammarly.di;

import android.app.Activity;

import com.zhipan.grammarly.data.database.ContactDao;
import com.zhipan.grammarly.data.database.ContactsDatabase;
import com.zhipan.grammarly.data.ContactsFetcher;
import com.zhipan.grammarly.data.ContactsRepository;
import com.zhipan.grammarly.data.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContactsModule {
    private ContactsDatabase database;

    public ContactsModule(Activity activity) {
        database = ContactsDatabase.getsInstance(activity.getApplicationContext());
    }

    @Singleton
    @Provides
    ContactsDatabase provideContactsDatabase() {
        return database;
    }

    @Singleton
    @Provides
    ContactDao provideContactDao() {
        return database.getContactDao();
    }

    @Singleton
    @Provides
    @Inject
    ContactsRepository provideContactsRepository(ContactsFetcher fetcher) {

        return ContactsRepository.getInstance(database.getContactDao(),
                fetcher,
                provideAppExecutors());
    }

    @Singleton
    @Provides
    AppExecutors provideAppExecutors() {
        return AppExecutors.getInstance();
    }
}

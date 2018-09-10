package com.zhipan.grammarly.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zhipan.grammarly.data.database.ContactDao;
import com.zhipan.grammarly.data.model.Avatar;
import com.zhipan.grammarly.data.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ContactsRepository {
    private static final Object LOCK = new Object();

    private static volatile ContactsRepository sInstance;

    private final ContactDao mContactDao;
    private final ContactsFetcher mContactsFetcher;
    private final AppExecutors mAppExecutors;

    private MutableLiveData<List<Contact>> mContactList;

    private boolean mInitialized = false;

    private ContactsRepository(ContactDao contactDao, ContactsFetcher fetcher, AppExecutors executors) {
        mContactDao = contactDao;
        mContactsFetcher = fetcher;
        mAppExecutors = executors;

        mContactList = new MutableLiveData<>();

        fetcher.getContacts().observeForever(newContacts -> {

            mAppExecutors.diskIO().execute(() -> {

                if (newContacts != null && newContacts.size() > 0) {
                    int i = 0, j = 0;

                    Collections.sort(newContacts, (o1, o2) -> {
                        if (o2 == null)
                            return -1;

                        if (o1 == null)
                            return 1;

                        return o1.getId().compareTo(o2.getId());
                    });

                    List<Avatar> oldContacts = mContactDao.getAllInRawForm();
                    if (oldContacts != null && oldContacts.size() > 0) {

                        while (i < oldContacts.size() && j < newContacts.size()) {

                            int compare = oldContacts.get(i).getId().compareTo(newContacts.get(j).getId());
                            if (compare == 0) {
                                newContacts.get(j++).setAvatarId(oldContacts.get(i++).getAvatarId());
                            } else if (compare < 0) {
                                i++;
                            }else {
                                String avatarId = UUID.randomUUID().toString();
                                newContacts.get(j).setAvatarId(avatarId);
                                mContactDao.insert(newContacts.get(j++));
                            }
                        }
                    }

                    oldContacts = new ArrayList<>();
                    for (; j<newContacts.size(); j++) {
                        newContacts.get(j).setAvatarId(UUID.randomUUID().toString());
                        oldContacts.add(newContacts.get(j));
                    }

                    Collections.sort(newContacts, (o1, o2) -> {
                        if (o2 == null || o2.getDisplayName() == null)
                            return -1;

                        if (o1 == null || o1.getDisplayName() == null)
                            return 1;

                        return o1.getDisplayName().compareTo(o2.getDisplayName());
                    });

                    mContactList.postValue(newContacts);

                    if (oldContacts.size() > 0)
                        mContactDao.bulkInsert(oldContacts);
                }
            });
        });
    }

    public static ContactsRepository getInstance(ContactDao contactDao,
                                                 ContactsFetcher fetcher, AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new ContactsRepository(contactDao, fetcher, executors);
                }
            }
        }

        return sInstance;
    }

    private synchronized void initializeData() {
        if (!mInitialized) {
            mInitialized = true;

            mAppExecutors.diskIO().execute(mContactsFetcher::fetchContacts);
        }
    }

    public LiveData<List<Contact>> getContacts() {
        initializeData();

        return mContactList;
    }
}

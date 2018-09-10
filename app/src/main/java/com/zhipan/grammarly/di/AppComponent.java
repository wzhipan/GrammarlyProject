package com.zhipan.grammarly.di;

import com.zhipan.grammarly.ui.ContactDetailActivity;
import com.zhipan.grammarly.ui.ContactListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ContactsModule.class})
public interface AppComponent {

    void inject(ContactListActivity activity);

    void inject(ContactDetailActivity activity);

}
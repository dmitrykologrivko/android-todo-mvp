package com.dmitrykologrivkogmail.todolist.presenters;

import com.dmitrykologrivkogmail.todolist.data.DataManager;
import com.dmitrykologrivkogmail.todolist.injection.PerActivity;
import com.dmitrykologrivkogmail.todolist.ui.SplashView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@PerActivity
public class SplashPresenter extends BasePresenter<SplashView> {

    @Inject
    public SplashPresenter(CompositeSubscription cs, DataManager dataManager) {
        super(cs, dataManager);
    }

    public void isAuthenticated() {
        checkViewAttached();

        Subscription subscription = mDataManager.isAuthenticated()
                .delay(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        getView().finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean isAuthenticated) {
                        if (isAuthenticated) {
                            getView().startMainActivity();
                        } else {
                            getView().startSignInActivity();
                        }
                    }
                });

        addSubscription(subscription);
    }
}
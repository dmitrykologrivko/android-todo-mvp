package com.dmitrykologrivkogmail.todolist.ui.tasks;

import android.view.MenuItem;

import com.dmitrykologrivkogmail.todolist.R;
import com.dmitrykologrivkogmail.todolist.data.DataManager;
import com.dmitrykologrivkogmail.todolist.data.api.models.TaskDTO;
import com.dmitrykologrivkogmail.todolist.injection.PerActivity;
import com.dmitrykologrivkogmail.todolist.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@PerActivity
public class TasksPresenter extends BasePresenter<TasksView> {

    @Inject
    public TasksPresenter(CompositeSubscription cs, DataManager dataManager) {
        super(cs, dataManager);
    }

    public void getTasks() {
        checkViewAttached();

        Subscription subscription = mDataManager.getTasks()
                .subscribe(new Observer<List<TaskDTO>>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().dismissProgress();
                        getView().showError(e.toString());
                    }

                    @Override
                    public void onNext(List<TaskDTO> tasks) {
                        if (tasks.isEmpty()) {
                            getView().showTasksEmpty();
                        } else {
                            getView().showTasks(tasks);
                        }
                    }
                });

        getView().showProgress();

        addSubscription(subscription);
    }

    public void markTask(long id, boolean isDone) {
        checkViewAttached();

        Subscription subscription = mDataManager.markTask(id, isDone)
                .subscribe(new Observer<TaskDTO>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showError(e.toString());
                    }

                    @Override
                    public void onNext(TaskDTO task) {
                        getView().updateTask(task);
                    }
                });

        addSubscription(subscription);
    }

    public void createTask() {
        checkViewAttached();

        String description = getView().getDescription();

        if (description == null || description.isEmpty()) {
            getView().showError(R.string.tasks_empty_description_error);
            return;
        }

        Subscription subscription = mDataManager.createTask(description)
                .subscribe(new Observer<TaskDTO>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgress();
                        getView().clearDescription();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().dismissProgress();
                        getView().showError(e.toString());
                    }

                    @Override
                    public void onNext(TaskDTO task) {
                        getView().addTask(task);
                    }
                });

        getView().showProgress();

        addSubscription(subscription);
    }

    public void signOut() {
        checkViewAttached();

        Subscription subscription = mDataManager.signOut()
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        getView().runSignInActivity();
                        getView().finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

    public void onTaskMarked(long id, boolean isDone) {
        markTask(id, isDone);
    }

    public void onDescriptionEditorAction() {
        createTask();
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            signOut();
            return true;
        }
        return false;
    }
}

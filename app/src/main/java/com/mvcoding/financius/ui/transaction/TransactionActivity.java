/*
 * Copyright (C) 2015 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.financius.ui.transaction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mvcoding.financius.R;
import com.mvcoding.financius.core.model.TransactionState;
import com.mvcoding.financius.core.model.TransactionType;
import com.mvcoding.financius.data.model.Place;
import com.mvcoding.financius.data.model.Tag;
import com.mvcoding.financius.data.model.Transaction;
import com.mvcoding.financius.ui.ActivityComponent;
import com.mvcoding.financius.ui.BaseActivity;
import com.mvcoding.financius.util.rx.Event;

import java.math.BigDecimal;
import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Observable;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;
import rx.subjects.PublishSubject;

public class TransactionActivity extends BaseActivity<TransactionPresenter.View, TransactionComponent> implements TransactionPresenter.View {
    private static final String EXTRA_TRANSACTION = "EXTRA_TRANSACTION";

    private static final String RESULT_EXTRA_TRANSACTION = "RESULT_EXTRA_TRANSACTION";

    private static final PublishSubject<TransactionType> transactionTypeSubject = PublishSubject.create();
    private static final PublishSubject<TransactionState> transactionStateSubject = PublishSubject.create();
    private static final PublishSubject<Long> dateSubject = PublishSubject.create();
    private static final PublishSubject<BigDecimal> amountSubject = PublishSubject.create();
    private static final PublishSubject<String> currencySubject = PublishSubject.create();
    private static final PublishSubject<Place> placeSubject = PublishSubject.create();
    private static final PublishSubject<Set<Tag>> tagsSubject = PublishSubject.create();

    @Bind(R.id.transactionTypeRadioGroup) RadioGroup transactionTypeRadioGroup;
    @Bind(R.id.transactionStateRadioGroup) RadioGroup transactionStateRadioGroup;
    @Bind(R.id.dateButton) Button dateButton;
    @Bind(R.id.timeButton) Button timeButton;
    @Bind(R.id.amountButton) Button amountButton;
    @Bind(R.id.currencyButton) Button currencyButton;
    @Bind(R.id.placeButton) Button placeButton;
    @Bind(R.id.tagsButton) Button tagsButton;
    @Bind(R.id.noteEditText) EditText noteEditText;
    @Bind(R.id.saveButton) Button saveButton;

    @Inject TransactionPresenter presenter;

    public static void startForResult(@NonNull Context context, int requestCode, @Nullable Transaction transaction) {
//        ActivityStarter.with(context, TransactionActivity.class).extra(EXTRA_TRANSACTION, transaction).startForResult(requestCode);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transaction;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transactionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> transactionTypeSubject.onNext(checkedId == R.id.transactionTypeExpenseRadioButton ? TransactionType.Expense : TransactionType.Income));
        transactionStateRadioGroup.setOnCheckedChangeListener((group, checkedId) -> transactionStateSubject.onNext(checkedId == R.id.transactionStateConfirmedRadioButton ? TransactionState.Confirmed : TransactionState.Pending));
    }

    @NonNull @Override protected TransactionComponent createComponent(@NonNull ActivityComponent component) {
        // TODO: Pass in the transaction
        return component.plus(new TransactionModule(null));
    }

    @Override protected void inject(@NonNull TransactionComponent component) {
        component.inject(this);
    }

    @NonNull @Override protected TransactionPresenter getPresenter() {
        return presenter;
    }

    @NonNull @Override protected TransactionPresenter.View getPresenterView() {
        return this;
    }

    @NonNull @Override public Observable<TransactionType> onTransactionTypeChanged() {
        return transactionTypeSubject;
    }

    @NonNull @Override public Observable<TransactionState> onTransactionStateChanged() {
        return transactionStateSubject;
    }

    @NonNull @Override public Observable<Long> onDateChanged() {
        return dateSubject;
    }

    @NonNull @Override public Observable<BigDecimal> onAmountChanged() {
        return amountSubject;
    }

    @NonNull @Override public Observable<String> onCurrencyChanged() {
        return currencySubject;
    }

    @NonNull @Override public Observable<Place> onPlaceChanged() {
        return placeSubject;
    }

    @NonNull @Override public Observable<Set<Tag>> onTagsChanged() {
        return tagsSubject;
    }

    @NonNull @Override public Observable<String> onNoteChanged() {
        return WidgetObservable.text(noteEditText).map(onTextChangeEvent -> onTextChangeEvent.text().toString());
    }

    @NonNull @Override public Observable<Event> onSave() {
        return ViewObservable.clicks(saveButton).map(onClickEvent -> new Event());
    }

    @Override public void showTransaction(@NonNull Transaction transaction) {

    }

    @Override public void startResult(@NonNull Transaction transaction) {

    }
}
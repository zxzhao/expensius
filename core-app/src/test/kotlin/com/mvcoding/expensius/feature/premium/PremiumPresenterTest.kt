/*
 * Copyright (C) 2016 Mantas Varnagiris.
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

package com.mvcoding.expensius.feature.premium

import com.mvcoding.expensius.Settings
import com.mvcoding.expensius.SubscriptionType
import com.mvcoding.expensius.SubscriptionType.FREE
import com.mvcoding.expensius.SubscriptionType.PREMIUM_PAID
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable.just
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject

class PremiumPresenterTest {
    val refreshSubject = PublishSubject<Unit>()
    val subscriptionTypeSubject = BehaviorSubject(FREE)

    val remoteBillingProductsService = mock<RemoteBillingProductsService>()
    val billingProductsProvider = BillingProductsProvider(remoteBillingProductsService)
    val settings = mock<Settings>()
    val view = mock<PremiumPresenter.View>()
    val presenter = PremiumPresenter(settings, billingProductsProvider)
    val inOrder = inOrder(view)

    @Before
    fun setUp() {
        whenever(view.onRefresh()).thenReturn(refreshSubject)
        whenever(settings.subscriptionTypes()).thenReturn(subscriptionTypeSubject)
        whenever(remoteBillingProductsService.billingProducts()).thenReturn(just(emptyList()))
    }

    @Test
    fun showsFreeUserWhenUserIsFree() {
        setSubscriptionType(FREE)

        presenter.onViewAttached(view)

        verify(view).showFreeUser()
    }

    @Test
    fun showsPremiumUserWhenUserIsPremium() {
        setSubscriptionType(PREMIUM_PAID)

        presenter.onViewAttached(view)

        verify(view).showPremiumUser()
    }

    @Test
    fun showsPremiumBillingProductsWhenUserIsUsingFreeVersionAndDoesNotOwnPremiumProducts() {
        setSubscriptionType(FREE)
        val billingProducts = listOf(
                aBillingProduct().asPremium().notOwned(),
                aBillingProduct().asDonation(),
                aBillingProduct().asPremium().notOwned())
        whenever(remoteBillingProductsService.billingProducts()).thenReturn(just(billingProducts))

        presenter.onViewAttached(view)

        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()
        inOrder.verify(view).hideEmptyView()
        inOrder.verify(view).showBillingProducts(billingProducts.filter { it.subscriptionType == FREE })
    }

    @Test
    fun convertsUserToPremiumPaidIfSheWasAFreeUserButHasOwnedPremiumBillingProducts() {
        setSubscriptionType(FREE)
        val billingProducts = listOf(
                aBillingProduct().asPremium().owned(),
                aBillingProduct().asDonation(),
                aBillingProduct().asPremium().notOwned())
        whenever(remoteBillingProductsService.billingProducts()).thenReturn(just(billingProducts))

        presenter.onViewAttached(view)

        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()
        inOrder.verify(view).hideEmptyView()
        inOrder.verify(view).showBillingProducts(billingProducts.filter { it.subscriptionType == PREMIUM_PAID })
        verify(settings).subscriptionType = PREMIUM_PAID
    }

    @Test
    fun showsDonationBillingProductsWhenUserIsUsingPremiumPaidVersion() {
        setSubscriptionType(PREMIUM_PAID)
        val billingProducts = listOf(aBillingProduct().asPremium(), aBillingProduct().asDonation(), aBillingProduct().asPremium())
        whenever(remoteBillingProductsService.billingProducts()).thenReturn(just(billingProducts))

        presenter.onViewAttached(view)

        inOrder.verify(view).showLoading()
        inOrder.verify(view).hideLoading()
        inOrder.verify(view).hideEmptyView()
        inOrder.verify(view).showBillingProducts(billingProducts.filter { it.subscriptionType == PREMIUM_PAID })
    }

    //    @Test
    //    fun donationsAreLoadedAfterSuccessfulPremiumPurchase() {
    //        setSubscriptionType(FREE)
    //        val premiumPurchases = listOf(aBillingProduct(), aBillingProduct(), aBillingProduct())
    //        val donationPurchases = listOf(aBillingProduct(), aBillingProduct())
    //        whenever(remoteBillingProductsService.premiumPurchases()).thenReturn(just(premiumPurchases))
    //        whenever(remoteBillingProductsService.donationPurchases()).thenReturn(just(donationPurchases))
    //        presenter.onViewAttached(view)
    //
    //        setSubscriptionType(PREMIUM_PAID)
    //
    //        inOrder.verify(view).showFreeUser()
    //        inOrder.verify(view).showLoading()
    //        inOrder.verify(view).hideLoading()
    //        inOrder.verify(view).hideEmptyView()
    //        inOrder.verify(view).showBillingProducts(premiumPurchases)
    //        inOrder.verify(view).showPremiumUser()
    //        inOrder.verify(view).showLoading()
    //        inOrder.verify(view).hideLoading()
    //        inOrder.verify(view).hideEmptyView()
    //        inOrder.verify(view).showBillingProducts(donationPurchases)
    //    }
    //
    //    @Test
    //    fun showsEmptyViewWhenThereAreNoPurchases() {
    //        setSubscriptionType(FREE)
    //        whenever(remoteBillingProductsService.premiumPurchases()).thenReturn(just(emptyList()))
    //
    //        presenter.onViewAttached(view)
    //
    //        inOrder.verify(view).showLoading()
    //        inOrder.verify(view).hideLoading()
    //        inOrder.verify(view).showEmptyView()
    //        inOrder.verify(view).showBillingProducts(emptyList())
    //    }
    //
    //    @Test
    //    fun showsUpdatedPurchasesAfterRefresh() {
    //        setSubscriptionType(FREE)
    //        val purchases = listOf(aBillingProduct(), aBillingProduct(), aBillingProduct())
    //        val updatedPurchases = listOf(aBillingProduct(), aBillingProduct())
    //        whenever(remoteBillingProductsService.premiumPurchases()).thenReturn(just(purchases), just(updatedPurchases))
    //        presenter.onViewAttached(view)
    //
    //        refresh()
    //
    //        inOrder.verify(view).showLoading()
    //        inOrder.verify(view).hideLoading()
    //        inOrder.verify(view).hideEmptyView()
    //        inOrder.verify(view).showBillingProducts(purchases)
    //        inOrder.verify(view).showLoading()
    //        inOrder.verify(view).hideLoading()
    //        inOrder.verify(view).hideEmptyView()
    //        inOrder.verify(view).showBillingProducts(updatedPurchases)
    //    }

    private fun setSubscriptionType(subscriptionType: SubscriptionType) = subscriptionTypeSubject.onNext(subscriptionType)
    private fun refresh() = refreshSubject.onNext(Unit)
}
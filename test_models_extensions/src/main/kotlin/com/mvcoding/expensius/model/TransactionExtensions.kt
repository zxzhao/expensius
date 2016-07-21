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

package com.mvcoding.expensius.model

fun aTransactionId() = TransactionId(aStringId())
fun aTransactionType() = TransactionType.values().aRandomItem()
fun aTransactionState() = TransactionState.values().aRandomItem()
fun aTimestamp() = Timestamp(aLongTimestamp())
fun aMoney() = Money(anAmount(), aCurrency(), anAmount())
fun aNote() = Note(aString("note"))
fun aTransaction() = Transaction(
        aTransactionId(),
        aModelState(),
        aTransactionType(),
        aTransactionState(),
        aTimestamp(),
        aMoney(),
        someTags(),
        aNote())
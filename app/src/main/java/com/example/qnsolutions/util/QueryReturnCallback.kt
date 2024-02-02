package com.example.qnsolutions.util

import org.jetbrains.annotations.NotNull


interface QueryReturnCallback
{
    fun onReturnValue(@NotNull response: Any, message: String)

    fun onQueryFailed(@NotNull fail: String)

    fun onQueryError(@NotNull error: String)
}
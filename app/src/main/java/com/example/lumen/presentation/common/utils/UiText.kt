package com.example.lumen.presentation.common.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    @Composable
    abstract fun asString(): String

    abstract fun asString(context: Context): String

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText() {
        @Composable
        override fun asString(): String = stringResource(resId, *args)

        override fun asString(context: Context): String = context.getString(resId, *args)

        // Manual equals check to handle resId and vararg array content for easier testing
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int = 31 * resId + args.contentHashCode()
    }
}

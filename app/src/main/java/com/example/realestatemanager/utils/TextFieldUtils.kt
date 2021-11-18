package com.example.realestatemanager.utils

import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputLayout

class TextFieldUtils {

    companion object {
        fun hasText(textInputLayout: TextInputLayout, error_message: String): Boolean {
            val text = textInputLayout.editText?.text.toString().trim()
            textInputLayout.error = null
            if (text.isEmpty()) {
                textInputLayout.error = error_message
                return false
            }
            return true
        }

        fun isNumber(textInputLayout: TextInputLayout, error_message: String): Boolean {
            val text = textInputLayout.editText?.text.toString()
            if (!text.isDigitsOnly()) {
                textInputLayout.error = error_message
                return false
            }
            return true
        }

    }

}
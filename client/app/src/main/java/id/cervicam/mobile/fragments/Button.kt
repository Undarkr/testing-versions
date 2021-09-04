
package id.cervicam.mobile.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import id.cervicam.mobile.R
import kotlinx.android.synthetic.main.fragment_button.*


class Button : Fragment() {
    // Enums
    enum class ButtonType {
        FILLED, OUTLINE, CLEAN
    }

    companion object {
        // Argument keys
        private const val ARG_LABEL: String = "LABEL"
        private const val ARG_TYPE: String = "TYPE"
        private const val ARG_CLICKABLE: String = "CLICKABLE"
        private const val ARG_COLOR: String = "COLOR"

        fun newInstance(
            label: String,
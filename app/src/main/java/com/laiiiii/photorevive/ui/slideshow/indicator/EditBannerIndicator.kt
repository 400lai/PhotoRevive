package com.laiiiii.photorevive.ui.slideshow.indicator

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.laiiiii.photorevive.R

class EditBannerIndicator(
    private val context: Context,
    private val container: LinearLayout,
    private val count: Int
) {

    init {
        createIndicators()
    }

    // Create the indicators
    private fun createIndicators() {
        container.removeAllViews()
        for (i in 0 until count) {
            val dot = View(context)

            val size = 8.dpToPx()  // Dot size
            val margin = 4.dpToPx()  // Margin between dots

            val params = LinearLayout.LayoutParams(size, size).apply {
                setMargins(margin, 0, margin, 0)
            }
            dot.layoutParams = params
            dot.setBackgroundResource(R.drawable.shape_indicator_dot)  // Default dot background
            container.addView(dot)
        }
        updateIndicators(0)  // Mark the first dot as selected
    }

    // Update the indicators based on selected index
    fun updateIndicators(selectedIndex: Int) {
        for (i in 0 until container.childCount) {
            val isSelected = i == selectedIndex
            container.getChildAt(i).setBackgroundResource(
                if (isSelected)
                    R.drawable.shape_indicator_dot_selected
                else
                    R.drawable.shape_indicator_dot
            )
        }
    }

    // dp to px conversion utility
    private fun Int.dpToPx(): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}

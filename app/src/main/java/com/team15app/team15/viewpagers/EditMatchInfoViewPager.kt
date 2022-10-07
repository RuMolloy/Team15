package com.team15app.team15.viewpagers

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet

class EditMatchInfoViewPager : ViewPager {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val numRows = 0.175
        var hms = heightMeasureSpec

        super.onMeasure(widthMeasureSpec, hms)

        var height = 0
        for (i in 0 until childCount) {

            val child = getChildAt(i)

            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            val h = child.measuredHeight*numRows

            if (h > height) height = h.toInt()

        }

        hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, hms)
    }

}
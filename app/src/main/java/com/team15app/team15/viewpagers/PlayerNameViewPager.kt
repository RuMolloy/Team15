package com.team15app.team15.viewpagers

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class PlayerNameViewPager : ViewPager {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val numRows = 1.5
        var heightMeasureSpec = heightMeasureSpec

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var height = 0
        for (i in 0 until childCount) {

            val child = getChildAt(i)

            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            val h = child.measuredHeight*numRows

            if (h > height) height = h.toInt()

        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}
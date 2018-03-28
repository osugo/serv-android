package app.android.serv.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by kombo on 31/12/2017.
 */
class SquareImageView : ImageView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        setMeasuredDimension(width, width)
    }
}
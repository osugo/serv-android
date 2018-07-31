package app.android.serv.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by kombo on 04/12/2017.
 */
class SpacesItemDecoration(private val spacesInPixels: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spacesInPixels;
        outRect.right = spacesInPixels;
        outRect.bottom = spacesInPixels;

        // Add top margin only for the first item to avoid double spacesInPixels between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = spacesInPixels;
        } else {
            outRect.top = 0;
        }
    }
}
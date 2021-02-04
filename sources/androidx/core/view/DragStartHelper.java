package androidx.core.view;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class DragStartHelper {
    private boolean mDragging;
    private int mLastTouchX;
    private int mLastTouchY;
    private final OnDragStartListener mListener;
    private final View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        /* class androidx.core.view.DragStartHelper.AnonymousClass1 */

        public boolean onLongClick(View v) {
            return DragStartHelper.this.onLongClick(v);
        }
    };
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        /* class androidx.core.view.DragStartHelper.AnonymousClass2 */

        public boolean onTouch(View v, MotionEvent event) {
            return DragStartHelper.this.onTouch(v, event);
        }
    };
    private final View mView;

    public interface OnDragStartListener {
        boolean onDragStart(View view, DragStartHelper dragStartHelper);
    }

    public DragStartHelper(View view, OnDragStartListener listener) {
        this.mView = view;
        this.mListener = listener;
    }

    public void attach() {
        this.mView.setOnLongClickListener(this.mLongClickListener);
        this.mView.setOnTouchListener(this.mTouchListener);
    }

    public void detach() {
        this.mView.setOnLongClickListener(null);
        this.mView.setOnTouchListener(null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0018, code lost:
        if (r2 != 3) goto L_0x004e;
     */
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    if (MotionEventCompat.isFromSource(event, 8194) && (event.getButtonState() & 1) != 0 && !this.mDragging && !(this.mLastTouchX == x && this.mLastTouchY == y)) {
                        this.mLastTouchX = x;
                        this.mLastTouchY = y;
                        boolean onDragStart = this.mListener.onDragStart(v, this);
                        this.mDragging = onDragStart;
                        return onDragStart;
                    }
                }
            }
            this.mDragging = false;
        } else {
            this.mLastTouchX = x;
            this.mLastTouchY = y;
        }
        return false;
    }

    public boolean onLongClick(View v) {
        return this.mListener.onDragStart(v, this);
    }

    public void getTouchPosition(Point point) {
        point.set(this.mLastTouchX, this.mLastTouchY);
    }
}

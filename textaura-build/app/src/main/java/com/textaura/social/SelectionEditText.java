package com.textaura.social;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

final class SelectionEditText extends EditText {
    interface Listener {
        void onSelectionChanged(int start, int end, boolean fromTouch);
    }

    private Listener listener;
    private boolean touchSequence;

    SelectionEditText(Context context) {
        super(context);
    }

    SelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    SelectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void setSelectionListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) touchSequence = true;
        boolean handled = super.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            post(() -> {
                if (listener != null) listener.onSelectionChanged(getSelectionStart(), getSelectionEnd(), true);
                touchSequence = false;
            });
        }
        return handled;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (listener != null) listener.onSelectionChanged(selStart, selEnd, touchSequence);
    }
}

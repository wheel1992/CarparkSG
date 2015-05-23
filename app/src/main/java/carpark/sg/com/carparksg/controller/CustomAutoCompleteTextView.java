package carpark.sg.com.carparksg.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

/**
 * Created by joseph on 4/5/2015.
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {


    public CustomAutoCompleteTextView(Context context){ super(context); }
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs){ super(context, attrs); }
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr){   super(context,attrs, defStyleAttr); }
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){  super(context,attrs, defStyleAttr, defStyleRes);    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            //if (mOnImeBack != null) mOnImeBack.onImeBack(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }


}

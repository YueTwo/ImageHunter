package com.example.sag.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.sag.R;


public class psw_edittext extends AppCompatEditText   {
    private Context context;
    private Drawable psw,clear;
    private boolean isShowClear;
    private MotionEvent event;

    public psw_edittext(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public psw_edittext(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public psw_edittext(@NonNull Context context) {
        super(context);
        this.context=context;
        init();
    }

    //图标资源的初始化
    private void init(){
        psw= ResourcesCompat.getDrawable(getResources(), R.drawable.password_0,null);
        clear= ResourcesCompat.getDrawable(getResources(),R.drawable.clear_0,null);
        setCompoundDrawablesWithIntrinsicBounds(psw,null,null,null);
    }

    //监听Edittext
    //被选中且有输入时显示清除图标
    @Override
    protected void onTextChanged(CharSequence text,int start,int lengthBefore,int lengthAfter){
        super.onTextChanged(text,start,lengthBefore,lengthAfter);
        setClearVisible(hasFocus()&&text.length()>0);
    }
    //正在输入（有焦点）且输入框内有文字时显示清除图标
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setClearVisible(focused && length()>0);
    }
    //isShowClear记录是否符合清除，执行显示清除图标的操作
    private void setClearVisible(boolean visible){
        isShowClear=visible;
        setCompoundDrawablesWithIntrinsicBounds(psw,null,
                clear,null);
    }

    //为清除图标区域注册点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isShowClear) {
            Drawable tmp1 = clear;
            if (tmp1 != null && event.getX() <= (getWidth() - getPaddingRight())
                    && event.getX() >= (getWidth() - getPaddingRight() - tmp1.getBounds().width())) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}



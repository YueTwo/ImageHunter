package com.example.sag.custom;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import com.example.sag.R;


public class search_edittext extends AppCompatEditText  {
    private Context context;
    private Drawable clear,search,voice;
    private boolean isShowClear;
    private MotionEvent event;

    public search_edittext(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public search_edittext(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public search_edittext(@NonNull Context context) {
        super(context);
        this.context=context;
        init();
    }
    //图标资源的初始化
    private void init(){
        clear= ResourcesCompat.getDrawable(getResources(), R.drawable.clear_0,null);
        search= ResourcesCompat.getDrawable(getResources(),R.drawable.search,null);
        voice= ResourcesCompat.getDrawable(getResources(),R.drawable.voice,null);
        setCompoundDrawablesWithIntrinsicBounds(search,null,voice,null);
    }

    //监听Edittext
    //被选中且有输入时显示清除图标
    @Override
    protected void onTextChanged(CharSequence text,int start,int lengthBefore,int lengthAfter){
        super.onTextChanged(text,start,lengthBefore,lengthAfter);
        setClear_2Visible(hasFocus()&&text.length()>0);
    }
    //正在输入（有焦点）且输入框内有文字时显示清除图标
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setClear_2Visible(focused && length()>0);
    }
    //isShowClear记录是否符合清除，执行去语音图标显示清除图标的操作
    private void setClear_2Visible(boolean visible){
        isShowClear=visible;
        setCompoundDrawablesWithIntrinsicBounds(search,null,
                visible?clear:voice,null);
    }

    //为清除图标区域注册点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                if(isShowClear){
                    Drawable tmp1=clear;
                    if(tmp1!=null&&event.getX()<=(getWidth()-getPaddingRight())
                            &&event.getX()>=(getWidth()-getPaddingRight()-tmp1.getBounds().width())){
                        setText("");
                        Toast.makeText(context,"已清除搜索框内文本信息！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Drawable tmp2=voice;
                    if(tmp2!=null&&event.getX()<=(getWidth()-getPaddingRight())
                            &&event.getX()>=(getWidth()-getPaddingRight()-tmp2.getBounds().width())){
                        Toast.makeText(context,"语音输入功能待开发！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}


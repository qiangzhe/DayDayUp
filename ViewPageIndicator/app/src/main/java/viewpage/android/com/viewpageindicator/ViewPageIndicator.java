package viewpage.android.com.viewpageindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mr Yang on 2017/8/13 0013 14:35
 */

public class ViewPageIndicator extends LinearLayout{

    private Paint mPaint;

    private Path mPath;

    private int mTriangleWidth;

    private int mTriangleHeight;

    private int mTabVisibleCount;

    private static final int COUNT_DEFAULT_TAB=4;

    private static final float RADIO_TRIANGLE_WIDTH=1/6F;

    /**
     * 三角形底边的最大宽度
     */
    private final int DIMENSION_TRIANGLE_WIDTH_MAX=(int)(getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);

    private static final int COLOR_TEXT_NORMAL=0X77FFFFFF;

    private static final int COLOR_TEXT_HIGHLIGHT=0XFFFFFFFF;

    private int mInitTranslationX;//默认偏移位置

    private int mTranslationX;//滑动移动位置

    List<String> mTitles;

    private ViewPager mViewPager;

    public PageOnchangeListener mListener;


    public ViewPageIndicator(Context context) {
        this(context,null);
    }

    public ViewPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取可见Tab的数量
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.ViewPageIndicator);

        mTabVisibleCount=a.getInt(R.styleable.ViewPageIndicator_visible_tab_count,COUNT_DEFAULT_TAB);
        if(mTabVisibleCount<0){
            mTabVisibleCount=COUNT_DEFAULT_TAB;
        }

        a.recycle();
        //初始化画笔
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
      canvas.save();

        canvas.translate(mInitTranslationX+mTranslationX,getHeight()+2);
        canvas.drawPath(mPath,mPaint);
        canvas.restore();

       super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth= (int) (w/mTabVisibleCount*RADIO_TRIANGLE_WIDTH);
        mTriangleWidth=Math.min(mTriangleWidth,DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX=w/mTabVisibleCount/2-mTriangleWidth/2;

        initTriangle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount=getChildCount();
        if(cCount==0)return;
        for(int i=0;i<cCount;i++){
            View view=getChildAt(i);
            LinearLayout.LayoutParams lp= (LayoutParams) view.getLayoutParams();
            lp.weight=0;
            lp.width=getScreenWidth()/mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {

        mTriangleHeight=mTriangleWidth/2;

        mPath=new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mTriangleWidth,0);
        mPath.lineTo(mTriangleWidth/2,-mTriangleHeight);
        mPath.close();
    }

    /**
     * 指示器跟随手指滚动
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        int tabWidth=getWidth()/mTabVisibleCount;
        mTranslationX= (int) (tabWidth*(offset+position));

        //容器移动，在tab处于移动至最后一个时
        if(position>=(mTabVisibleCount-2)&&offset>0
                &&getChildCount()>mTabVisibleCount){
            if(mTabVisibleCount!=1){
                this.scrollTo((position-(mTabVisibleCount-2))*tabWidth
                        +(int)(tabWidth*offset),0);
            }else {
                this.scrollTo(position*tabWidth+(int)(tabWidth*offset),0);
            }
        }
        invalidate();
    }

    /**
     * 获得屏幕的宽度
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm= (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMrtrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMrtrics);
        return outMrtrics.widthPixels;
    }


    public void setTabItemTitles(List<String> titles){
        if(titles!=null&&titles.size()>0){
            this.removeAllViews();
            mTitles=titles;
            for(String title:mTitles){
                addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }



    /**
     * 设置可见的Tab数量
     * @param count
     */
    public void setVisibleTabCount(int count){
        mTabVisibleCount=count;
    }

    /**
     * 根据title创建Tab
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv=new TextView(getContext());
        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width=getScreenWidth()/mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }


    /**
     * 设置关联的viewPager
     * @param viewPager
     * @param pos
     */
    public void setViewPager(ViewPager viewPager,int pos){

        mViewPager=viewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //tabWidth*poisionOffset+poision*tabWidth
                scroll(position,positionOffset);
                if(mListener!=null){
                    mListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mListener!=null){
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mListener!=null){
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }




    public void setOnPageChangeListener(PageOnchangeListener listener){
        this.mListener=listener;
    }

    public interface PageOnchangeListener{
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        void onPageSelected(int position);
        void onPageScrollStateChanged(int state);
    }


    /**
     * 高亮某个Tab的文本
     * @param pos
     */
    private void highLightTextView(int pos){
        resetTextVeiwColor();
        View view=getChildAt(pos);
        if(view instanceof TextView){
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }


    /**
     * 重置Tab文本颜色
     */
    private void resetTextVeiwColor(){
        for(int i=0;i<getChildCount();i++){
            View view=getChildAt(i);
            if(view instanceof TextView){
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }


    /**
     * 设置Tab点击事件
     */
    private void setItemClickEvent(){
        int cCount=getChildCount();
        for(int i=0;i<cCount;i++){
            final int j=i;
            View view=getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(j,false);
                }
            });
        }
    }
}





































































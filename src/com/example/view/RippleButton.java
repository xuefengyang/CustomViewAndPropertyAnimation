package com.example.view;

import com.example.animation.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class RippleButton extends Button implements OnTouchListener{
	private int mMaxRadius;
	private int mRadius;
	private int mRadiuGap;
	private int mCenterX;
	private int mCenterY;
	private Paint mRipplePaint;
	private int mMinBetweenWidthAndHeight; 
	private boolean isShouldAnimation;
	private boolean isMove;
	private MotionEvent event;
	
	private final static int ISSHOULDDISPATCH=2;
	private final static int ISANMATIONING=1;
	private int mStatus =ISANMATIONING;
	private final static int DEFAULT_RIPPLE_COLOR=Color.parseColor("#63B8FF");
	private int mRippleColor;
	public RippleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RippleButton, defStyle, 0);
		mRippleColor =typedArray.getColor(R.styleable.RippleButton_ripple_color, DEFAULT_RIPPLE_COLOR);
		mRipplePaint.setColor(mRippleColor);
		typedArray.recycle();
		
	}
	public RippleButton(Context context) {		
		this(context,null, 0);
	}	
	public RippleButton(Context context, AttributeSet attrs) {	
		this(context,attrs,0);
	}			
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mMinBetweenWidthAndHeight=Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadiuGap =Math.min(getMeasuredWidth(), getMeasuredHeight())/8;
		mMaxRadius =Math.max(getHeight(), getWidth());
		setOnTouchListener(this);
	}	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.event =event;
		if(mStatus==ISSHOULDDISPATCH){
			mStatus=ISANMATIONING;
			return false;
		} 	
		mCenterX =(int) event.getX();
		mCenterY =(int) event.getY();
		isShouldAnimation=true;	
		caluateValuesAndAimate(mCenterX,mCenterY); 
		if(isShouldAnimation){ 		
			setPressed(false);
		}			
		if(event.getAction()==MotionEvent.ACTION_CANCEL||event.getAction()==MotionEvent.ACTION_UP){
			Log.d("TAG", "action_cancel---or action_up") ;
		}		
		return false;
	}		
	private void caluateValuesAndAimate(int x,int y){
			isMove=true;	
			mRadius =mRadiuGap*2;	
			doMoveAnimation(x,0,getMeasuredWidth(),getMeasuredWidth()-x,x);
	}			
	private void doMoveAnimation(int...values){
		ValueAnimator animator = ValueAnimator.ofInt(values[0],values[1],values[2],values[3],values[4]); 
		animator.setDuration(600);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Log.d("TAG", "-------"+mCenterX); 
				mCenterX=(Integer)animation.getAnimatedValue();
				mRadius++;
				postInvalidate(); 
			}	
		});
		animator.start();
		animator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				isMove =true;
			}	
			@Override
			public void onAnimationRepeat(Animator animation) {
			}	
			@Override
			public void onAnimationEnd(Animator animation) {
				clearAnimation();
				isMove=false;
				postInvalidate();
				mStatus=ISSHOULDDISPATCH;
				setPressed(true);
			}	
			@Override
			public void onAnimationCancel(Animator animation) {
				isMove=false;		
				clearAnimation();
				postInvalidate();
				setPressed(true);	
				if(event!=null){
					
				}
			}
		});
	}	
	private class PostEventRunnable implements Runnable{
		
		@Override
		public void run() {	
			dispatchTouchEvent(event);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!isShouldAnimation){
			return ;
		}	
		if(!isMove){
		
		if(mRadius>mMinBetweenWidthAndHeight){
			mRadius+=mRadiuGap*4;
		}else{
			mRadius+=mRadiuGap;
		}	
		if(mRadius>=mMaxRadius){
			isShouldAnimation=false;
			postInvalidateDelayed(30);
			mRadius=0;
			this.dispatchTouchEvent(event);
			
		}else{		
			postInvalidateDelayed(30);
		}		
			}
		Log.d("TAG", "Radius: " +mRadius);
		canvas.save();	
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mRipplePaint);
		canvas.restore();
	}
	
}

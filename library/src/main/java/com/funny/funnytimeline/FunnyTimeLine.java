package com.funny.funnytimeline;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import static com.funny.funnytimeline.FunnyCanvasUtils.drawCenterText;
import static com.funny.funnytimeline.FunnyUtils.px2sp;
public class FunnyTimeLine extends View
{
	public static int FPS=36;//帧率
	private static String TAG="FunnyTimeLine";

	public TimeData timeData;
	Context ctx;
	FunnyDrawThread thread;
	
	Paint p;
	Rect textRect;
	RectF timeRectF;//时间的圆角边框
	int lightColor=Color.parseColor("#aa000000");
	int normalColor=Color.parseColor("#dd000000");
	int stressColor=Color.parseColor("#ff000000");
	
	int h=100;
	int showNums=11;
	int viewWidth=1080,viewHeight=460;
	int centerX=viewWidth/2;
	int centerY=viewHeight/2;
	int l;
	
	float startX,startY;//触摸移动
	int scrollX;//惯性滑动。
	int maxVelocity,minVelocity;//惯性滑动
	VelocityTracker velocityTracker=null;
	DecelerateInterpolator decelerateInterpolatorSlow = new DecelerateInterpolator();
	DecelerateInterpolator decelerateInterpolatorMiddle = new DecelerateInterpolator();
	OvershootInterpolator overshootInterpolatorFast=new OvershootInterpolator();

	float scrollY = 0f;//纵向切换，滑动了多少
	float minXShift = 100;//横向小幅度移动
	
	Bitmap bufferBitmap;//双缓冲图
	Canvas bufferCanvas;//缓冲画板
	
	private ValueAnimator scrollValueAnimator;//惯性滑动的属性动画
	private Animator.AnimatorListener defaultAnimatorListener;
	public FunnyTimeLine(Context ctx){
		this(ctx,null);
	}
	
	public FunnyTimeLine(Context ctx,AttributeSet attr){
		super(ctx,attr);
		this.ctx=ctx;
		timeData=new TimeData(1780);

		TypedArray array = ctx.obtainStyledAttributes(attr,R.styleable.FunnyTimeLine);
		lightColor = array.getColor(R.styleable.FunnyTimeLine_lightColor,lightColor);
		normalColor = array.getColor(R.styleable.FunnyTimeLine_normalColor,normalColor);
		stressColor = array.getColor(R.styleable.FunnyTimeLine_stressColor,stressColor);
		showNums = array.getInt(R.styleable.FunnyTimeLine_showNums,11);
		timeData.curKind = (short) array.getInt(R.styleable.FunnyTimeLine_timeKind,0);
		timeData.setTimes(timeData.parseMS(array.getInt(R.styleable.FunnyTimeLine_time,1700)));
		array.recycle();


		
		p=new Paint();
		p.setColor(stressColor);
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		textRect=new Rect();
		timeRectF=new RectF();
		
		bufferCanvas=new Canvas();
		
		l=viewWidth/(showNums-1);
		
		maxVelocity=ViewConfiguration.get(ctx).getScaledMaximumFlingVelocity();
		minVelocity=ViewConfiguration.get(ctx).getScaledMinimumFlingVelocity();
		velocityTracker=VelocityTracker.obtain();
		scrollValueAnimator = new ValueAnimator();
		defaultAnimatorListener=new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				Log.i(TAG,"-----valueAnimator start");
			}

			@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
			@Override
			public void onAnimationEnd(Animator animator) {
				Log.i(TAG,"-----valueAnimator end");
				((ValueAnimator)animator).setCurrentFraction(0f);
				scrollX=0;
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				Log.i(TAG,"-----valueAnimator cancel");
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
				Log.i(TAG,"-----valueAnimator repeat");
			}
		};
		scrollValueAnimator.addListener(defaultAnimatorListener);

		thread=new FunnyDrawThread(this);
		thread.start();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(bufferCanvas);
		bufferCanvas.drawColor(Color.WHITE);
		//主线 中间线
		p.setStrokeWidth(2);
		p.setColor(stressColor);
		bufferCanvas.drawLine(0,h,viewWidth,h,p);
		bufferCanvas.drawLine(0,2*h,viewWidth,2*h,p);
		p.setStrokeWidth(4);
		bufferCanvas.drawLine(centerX,0,centerX,h,p);
		bufferCanvas.drawLine(centerX,2*h,centerX,viewHeight,p);

		//画细线
		int halfShowNum=(showNums+1)/2;
		int halfLines=halfShowNum*5;
		float sp=timeData.getShiftPercent();
		float centerTextX=viewWidth/2.0f-sp*l;

		for(int i=-halfLines;i<halfLines;i++){
			float curX=centerTextX+i*l/5.0f;
			if(i%5==0){//粗线
				p.setColor(normalColor);
				p.setStrokeWidth(3);
				bufferCanvas.drawLine(curX,h/4.0f,curX,h,p);
				bufferCanvas.drawLine(curX,h*2,curX,h*11/4.0f,p);
				p.setColor(lightColor);
				p.setStrokeWidth(2);
			}else{
				bufferCanvas.drawLine(curX,h/2.0f,curX,h,p);
				bufferCanvas.drawLine(curX,h*2,curX,h*5/2.0f,p);
			}
		}

		//画中间字
//		bufferCanvas.save();
//		bufferCanvas.translate(0,scrollY);
//		int alphaDisappear = 255 - (int) (Math.abs(scrollY % (h/2f))/100*255);
//		Log.i(TAG,"----透明度是："+alphaDisappear);
//		p.setAlpha(alphaDisappear);
		//System.out.println("sp:"+sp);

		int leftTextSize=px2sp(ctx,(h/2f*(1f+sp)));//放大数字大小
		int rightTextSize=px2sp(ctx,h/2f*(2f-sp));//减小数字大小
		p.setColor(lightColor);
		p.setTextSize(rightTextSize);
		drawCenterText(bufferCanvas,""+timeData.getLatterValue(0),centerTextX,centerY,textRect,p);//缩小的字
		p.setColor(normalColor);
		p.setTextSize(leftTextSize);
		drawCenterText(bufferCanvas,""+timeData.getLatterValue(1),centerTextX+l,centerY,textRect,p);//放大的字

		//画其它字

		p.setColor(lightColor);
		p.setTextSize(px2sp(ctx,h/2.0f));
		p.setStrokeWidth(2);
		for(int i=-halfShowNum;i<halfShowNum;i++){
			if(i==0||i==1){continue;}//画过的字不画
			float curX=centerTextX+l*i;
			drawCenterText(bufferCanvas,timeData.getLatterValue(i),curX,centerY,textRect,p);
		}

		//画时间
		String text=timeData.toString();
		p.setTextSize(px2sp(ctx,h/2.0f));
		float textWidth=p.measureText(text);

		timeRectF.set(viewWidth-textWidth*1.2f,viewHeight-h/2.0f,viewWidth,viewHeight);
		p.setColor(Color.YELLOW);
		bufferCanvas.drawRoundRect(timeRectF,h/6f,h/6f,p);

		p.setColor(lightColor);
		drawCenterText(bufferCanvas,text,(timeRectF.left+timeRectF.right)/2f,viewHeight-h/4f,textRect,p);

		canvas.drawBitmap(bufferBitmap,0,0,p);
	}



	@SuppressLint("ClickableViewAccessibility")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		velocityTracker.computeCurrentVelocity(1000);
		velocityTracker.addMovement(event);
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(scrollValueAnimator !=null&& scrollValueAnimator.isRunning()){
					Log.i(TAG,"上次动画尚未结束！");
					scrollValueAnimator.end();
					scrollValueAnimator.cancel();
					//valueAnimator.setCurrentFraction(0);
				}
				startX=event.getX();
				startY=event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float shiftX=event.getX()-startX;
				float shiftY=event.getY()-startY;
				changeDataByRatio(shiftX);
				startX=event.getX();
//				startY=event.getY();
				break;
			case MotionEvent.ACTION_UP:
				//Log.i(TAG,"------现在抬手！");
				float velocityX=velocityTracker.getXVelocity();
				//Log.i(TAG,"------计算得到的VelocityX:"+velocityX);
				autoScroll(velocityX);
				velocityTracker.clear();
					//velocityTracker=null;
				break;
				
		}
		return true;
	}

	private void autoScroll(float velocityX) {
		if (scrollValueAnimator.isRunning()){
			return;
		}
		if(Math.abs(velocityX)<minVelocity){
			Log.i(TAG,"velocityX过小！");
			return;
		}
		else if(Math.abs(velocityX)<5000){//慢速
			scrollValueAnimator = ValueAnimator.ofInt(0,(int)velocityX/20);
			scrollValueAnimator.setDuration(Math.abs((int)velocityX/5));
			scrollValueAnimator.setInterpolator(decelerateInterpolatorSlow);
			scrollValueAnimator.addListener(defaultAnimatorListener);
		}else if(Math.abs(velocityX)<8000){//中速
			scrollValueAnimator =ValueAnimator.ofInt(0,(int)velocityX/10);
			scrollValueAnimator.setDuration(Math.abs((int)velocityX/10));
			scrollValueAnimator.setInterpolator(decelerateInterpolatorMiddle);
			scrollValueAnimator.addListener(defaultAnimatorListener);
		}else{//极快
			scrollValueAnimator =ValueAnimator.ofInt(0,(int)velocityX/5);
			scrollValueAnimator.setDuration(Math.abs((int)velocityX/7));
			scrollValueAnimator.setInterpolator(overshootInterpolatorFast);
			scrollValueAnimator.addListener(defaultAnimatorListener);
		}
		scrollValueAnimator.start();
	}

	private void processY(float shiftX,float shiftY){//处理Y方向的滑动事件
		if (shiftX<minXShift){//不处理横向才处理纵向
			scrollY = shiftY;
		}
	}

	public int[] getScrollValue(){
		if (scrollValueAnimator ==null){
			return new int[2];
		}
		if(scrollValueAnimator.isRunning()){
			int[] result={(int) scrollValueAnimator.getAnimatedValue()-scrollX,-1};
			scrollX=(int) scrollValueAnimator.getAnimatedValue();
			//Log.i(TAG,"valueAni 有那么多个listeners:"+valueAnimator.getListeners().size());
			//Log.i(TAG,String.format("----getScrollValue result:(%d,%d) ScrollX:%d",result[0],result[1],scrollX));
			return result;
		}else{
			return new int[2];
		}
	}

	public void repaint(){
		invalidate();
	}
	
	public void changeDataByRatio(float offsetX){
		//Log.i(TAG,"----changeData offSet is:"+offsetX);
		timeData.changeData(timeData.curKind,(int)(-offsetX/l*timeData.getCurMax()));
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		this.viewWidth=MeasureSpec.getSize(widthMeasureSpec);
		this.viewHeight=MeasureSpec.getSize(heightMeasureSpec)/4;
		this.h = viewHeight/3;
		this.centerX=viewWidth/2;
		this.centerY=viewHeight/2;
		Log.i(TAG,String.format("width：%d height:%d",viewWidth,viewHeight));

		bufferBitmap=Bitmap.createBitmap(viewWidth,viewHeight,Bitmap.Config.ARGB_8888);
		bufferCanvas.setBitmap(bufferBitmap);

		setMeasuredDimension(viewWidth,viewHeight);
	}

	public TimeData getTimeData() {
		return timeData;
	}

	public void setTimeData(TimeData timeData) {
		this.timeData = timeData;
	}

	public int getLightColor() {
		return lightColor;
	}

	public void setLightColor(int lightColor) {
		this.lightColor = lightColor;
	}

	public int getNormalColor() {
		return normalColor;
	}

	public void setNormalColor(int normalColor) {
		this.normalColor = normalColor;
	}

	public int getStressColor() {
		return stressColor;
	}

	public void setStressColor(int stressColor) {
		this.stressColor = stressColor;
	}

	public int getShowNums() {
		return showNums;
	}

	public void setShowNums(int showNums) {
		this.showNums = showNums;
	}

	public static void setFPS(int FPS) {
		FunnyTimeLine.FPS = FPS;
	}

	public void setTimeKind(short timeKind){
		this.timeData.curKind = timeKind;
	}

	public short getTimeKind(){
		return timeData.curKind;
	}

	public void setTimeMS(int ms){
		this.timeData.setTimes(this.timeData.parseMS(ms));
	}
}


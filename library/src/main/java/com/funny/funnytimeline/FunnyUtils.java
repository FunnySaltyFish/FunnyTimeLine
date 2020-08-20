package com.funny.funnytimeline;
import android.content.Context;

public class FunnyUtils
{
	/**
	 * 给color添加透明度
	 * @param alpha 透明度 0f～1f
	 * @param baseColor 基本颜色
	 * @return
	 */
	public static int getColorWithAlpha(float alpha, int baseColor) {
		int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
		int rgb = 0x00ffffff & baseColor;
		return a + rgb;
	}


	//度量转化
	/**
	 * convert px to its equivalent dp
	 * 
	 * 将px转换为与之相等的dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale =  context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * convert dp to its equivalent px
	 * 
	 * 将dp转换为与之相等的px
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	/**
	 * convert px to its equivalent sp 
	 * 
	 * 将px转换为sp
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}


	/**
	 * convert sp to its equivalent px
	 * 
	 * 将sp转换为px
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}

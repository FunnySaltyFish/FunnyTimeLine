package com.funny.funnytimeline;

import android.annotation.SuppressLint;

public class TimeData
{
	public int h;
	public int min;
	public int s;
	public int ms;
	public short curKind=0;

	private StringBuilder sb = new StringBuilder();
	public TimeData(int h, int min, int s, int ms) {
		this.h = h;
		this.min = min;
		this.s = s;
		this.ms = ms;
	}


	public TimeData(int ms){
		int[] result=parseMS(ms);
		this.h=result[0];
		this.min=result[1];
		this.s=result[2];
		this.ms=result[3];
	}

	public int getCurTimeMS(){
		return h*3600000+min*60000+s*1000+ms;
	}
	
	public int getCurValue(){
		switch(curKind){
			case 0:return this.s;
			case 1:return this.min;
			case 2:return this.h;
			default:return 0;
		}
	}
	
	public int getLatterValue(int n){
		int temp=0,max=0;
		switch(curKind){
			case 0:temp=this.s+n;max=60;break;
			case 1:temp=this.min+n;max=60;break;
			case 2:temp=this.h+n;max=24;break;
			default:return 0;
		}
		if(temp>=max){temp=temp-max;}
		else if(temp<0){temp=max+temp;}
		return temp;
	}
	
	public float getShiftPercent(){
		float sp=0;
		switch(curKind){
			case 0:sp=this.ms/1000f;break;
			case 1:sp=this.s/60f;break;
			case 2:sp=this.min/60f;break;
			default:return 0;
		}
		return sp;
	}
	
	public void changeData(int kind,int n){
		int temp=0,max=0;
		switch(kind){
			case 0:
				//int next=add(this.ms,n,1000);
				temp=this.ms+n;
				max=1000;
				while(temp>=max){temp=temp-max;changeData(1,1);}
				while(temp<0){temp=max+temp;changeData(1,-1);}
				this.ms=temp;
				//this.s+=next;
				break;
			case 1:
				temp=this.s+n;
				max=60;
				while(temp>=max){temp=temp-max;changeData(2,1);}
				while(temp<0){temp=max+temp;changeData(2,-1);}
				{this.s=temp;}
				break;
			case 2:
				temp=this.min+n;
				max=60;
				while(temp>=max){temp=temp-max;changeData(3,1);}
				while(temp<0){temp=max+temp;changeData(3,-1);}
				this.min=temp;
				break;
			case 3:
				temp=this.h+n;
				max=24;
				while(temp>=max){temp=temp-max;}
				while(temp<0){temp=max+temp;}
				this.h=temp;
				break;
			default:break;
		}
		if(temp>=max){temp=temp-max;}
		else if(temp<0){temp=max+temp;}
	}
	
	//工具方法
	public int[] parseMS(int time)
	{
		// TODO: Implement this method
		int h=0,m=0,s=0,ms=0;
		int t;
		t=(time/1000);
		ms=time-1000*t;
		h=(int)Math.floor(t/(60*60));
		t-=h*3600;
		m=(int)Math.floor(t/60);
		t-=m*60;
		s=t;
		int[] result={h,min,s,ms};
		return result;
	}

	public void setTimes(int[] times){
		this.h=times[0];
		this.min=times[1];
		this.s=times[2];
		this.ms=times[3];
	}

	public int getCurMax(){
		switch (curKind){
			case 1:
			case 2:
				return 60;
			default:
				return 1000;
		}
	}
	
	private int add(int base,int addNumber,int max){
		int temp=base+addNumber;
		if(temp>=max){
			temp=temp-max;
			base=temp;
			return 1;//进位
		}else if(temp<0){
			temp=temp+max;
			base=temp;
			return -1;
		}else{
			base=temp;
			return 0;
		}
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString()
	{
		sb.setLength(0);//清空
		sb.append(String.format("%02d",this.h));
		sb.append(":");
		sb.append(String.format("%02d",this.min));
		sb.append(":");
		sb.append(String.format("%02d",this.s));
//		sb.append(":");
//		sb.append(String.format("%04d",this.ms));
		return sb.toString();
	}
	
}

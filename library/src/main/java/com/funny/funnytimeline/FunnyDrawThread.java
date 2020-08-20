package com.funny.funnytimeline;

public class FunnyDrawThread extends Thread
{
	boolean flag=false;
	FunnyTimeLine view;
	public FunnyDrawThread(FunnyTimeLine funnyTimeLine){
		this.view=funnyTimeLine;
	}

	@Override
	public void start()
	{
		// TODO: Implement this method
		super.start();
		this.flag=true;
	}
	
	@Override
	public void run()
	{
		// TODO: Implement this method
		while(flag){
			try{
				view.changeDataByRatio(view.getScrollValue()[0]);
				view.repaint();
				sleep(1000/view.FPS);
			}catch(Exception e){
				e.printStackTrace();
				this.flag=false;
			}
		}
	}
	
}

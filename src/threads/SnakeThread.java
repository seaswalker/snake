package threads;

import service.SnakeService;

/**
 * 蛇运动线程
 * @author skywalker
 *
 */
public class SnakeThread extends Thread {
	
	private SnakeService snakeService;
	private boolean isRun = true;
	//速度，开始是400毫秒
	private long init = 400L;
	//速度，分数每增加10，提一次速
	private long speed = 20L;
	
	public SnakeThread(SnakeService snakeService) {
		this.snakeService = snakeService;
	}

	@Override
	public void run() {
		try {
			while(true) {
				synchronized (this) {
					if(!isRun) {
						this.wait();
					}
				}
				snakeService.move();
				//计算当前的间隔
				long timeInterval = init - (snakeService.getCurrentPoint() / 10) * speed;
				//极限是50毫秒
				timeInterval = (timeInterval < 50) ? 50 : timeInterval;
				Thread.sleep(timeInterval);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRun() {
		return isRun;
	}

	/**
	 * 暂停
	 */
	public void pause() {
		synchronized (this) {
			if(isRun) {
				isRun = false;
			}
		}
	}
			
	/**
	 * 恢复执行
	 */
	public void consume() {
		synchronized (this) {
			isRun = true;
			this.notify();
		}
	}
	
}

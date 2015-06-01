package threads;

import service.SnakeService;

/**
 * ���˶��߳�
 * @author skywalker
 *
 */
public class SnakeThread extends Thread {
	
	private SnakeService snakeService;
	private boolean isRun = true;
	//�ٶȣ���ʼ��400����
	private long init = 400L;
	//�ٶȣ�����ÿ����10����һ����
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
				//���㵱ǰ�ļ��
				long timeInterval = init - (snakeService.getCurrentPoint() / 10) * speed;
				//������50����
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
	 * ��ͣ
	 */
	public void pause() {
		synchronized (this) {
			if(isRun) {
				isRun = false;
			}
		}
	}
			
	/**
	 * �ָ�ִ��
	 */
	public void consume() {
		synchronized (this) {
			isRun = true;
			this.notify();
		}
	}
	
}

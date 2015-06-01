package service;

import java.awt.Point;
import java.util.Deque;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import model.Direction;
import threads.SnakeThread;
import util.PointUtil;

/**
 * ��Ϸ�߼�
 * @author skywalker
 *
 */
public class SnakeService {
	
	private JFrame mainFrame;
	private Deque<Point> blocks;
	private Point food;
	private Random random = new Random();
	/**Ĭ���˶���������**/
	private Direction currentDirection = Direction.LEFT;
	/**��ʾ��߷��Լ���ǰ�ֵ�label**/
	private JLabel maxPointLabel;
	private JLabel currentPointLabel;
	/**��߷��Լ���ǰ����**/
	private int maxPoint;
	private int currentPoint;
	/**��߷��Ƿ�ı���**/
	private boolean isMaxPointChanged = false;
	/**���߳�**/
	private SnakeThread snake;

	/**
	 * ��ʼ��Ϸ���������λ����ʾһ���ߣ�����Ϊ3������
	 * @param mainFrame ��Ϸ����
	 */
	public void start(JFrame mainFrame, Deque<Point> blocks, Point food, 
			JLabel maxPointLabel, JLabel currentPointLabel) {
		if(this.mainFrame == null) {
			this.mainFrame = mainFrame;
			this.blocks = blocks;
			this.food = food;
			this.maxPointLabel = maxPointLabel;
			this.currentPointLabel = currentPointLabel;
		}
		//�����
		this.blocks.clear();
		//��������ߵ�����
		int x = random.nextInt(30) + 6;
		int y = random.nextInt(40);
		this.blocks.offer(new Point(x, y));
		this.blocks.offer(new Point(x + 1, y));
		this.blocks.offer(new Point(x + 2, y));
		//�������ʳ������
		generateFood();
		//��ʼ������
		setPoints(true, false, true, false);
		//��ʼ���ƶ��߳�
		if(snake == null) {
			snake = new SnakeThread(this);
			snake.start();
		}else {
			snake.consume();
		}
	}
	
	/**
	 * ���÷���
	 * @param isInit ���Ϊtrue���ļ���ȡ���ֵ�����ҵ�ǰ������0
	 * @param isSetMaxPoint trueˢ�����ֵ
	 * @param isSetCurrentPoint trueˢ�µ�ǰֵ
	 * @param isSaveMaxPoint true����߷�д���ļ�
	 */
	private void setPoints(boolean isInit, boolean isSetMaxPoint, boolean isSetCurrentPoint, boolean isSaveMaxPoint) {
		if(isInit) {
			//��ʼ��
			maxPoint = PointUtil.readMaxPoint();
			maxPointLabel.setText("��ߵ÷�:" + maxPoint);
			currentPointLabel.setText("��ǰ�÷�:" + currentPoint);
		}else {
			if(isSetMaxPoint) {
				maxPointLabel.setText("��ߵ÷�:" + maxPoint);
			}
			if(isSaveMaxPoint && isMaxPointChanged) {
				PointUtil.writeMaxPoint(maxPoint);
			}
			if(isSetCurrentPoint) {
				currentPointLabel.setText("��ǰ�÷�:" + currentPoint);
			}
		}
	}
	
	/**
	 * ����ʳ��
	 */
	private void generateFood() {
		int x = random.nextInt(40);
		int y = random.nextInt(40);
		//����Ƿ�������ߵ��ڲ�
		boolean isInner = true;
		while(isInner) {
			for(Point point : blocks) {
				if(point.x == x && point.y == y) {
					x = random.nextInt(40);
					y = random.nextInt(40);
					break;
				}
			}
			isInner = false;
		}
		this.food.setLocation(x, y);
	}

	/**
	 * ��Ϸ����
	 * ��38 ��40 ��37 ��39
	 */
	public void control(int keyCode) {
		switch (keyCode) {
		case 38:
			if(this.currentDirection != Direction.DOWN)
				this.currentDirection = Direction.UP;
			break;
		case 40:
			if(this.currentDirection != Direction.UP)
				this.currentDirection = Direction.DOWN;
			break;
		case 37:
			if(this.currentDirection != Direction.RIGHT)
				this.currentDirection = Direction.LEFT;
			break;
		//p����Ϸ����ͣ��ָ�
		case 80:
			pauseOrConsume();
			break;
		default:
			if(this.currentDirection != Direction.LEFT)
				this.currentDirection = Direction.RIGHT;
			break;
		}
	}
	
	private void pauseOrConsume() {
		if(snake.isRun()) {
			snake.pause();
		}else {
			snake.consume();
		}
	}

	/**
	 * �˶�
	 */
	public void move() {
		this.mainFrame.repaint();
		switch (this.currentDirection) {
		case UP:
			if(isJoin(false, -1)) {
				gameOver();
			}
			edtFood(false, -1);
			moveUp();
			break;
		case DOWN:
			if(isJoin(false, 1)) {
				gameOver();
			}
			edtFood(false, 1);
			moveDown();
			break;
		case LEFT:
			if(isJoin(true, -1)) {
				gameOver();
			}
			edtFood(true, -1);
			moveLeft();
			break;
		default:
			if(isJoin(true, 1)) {
				gameOver();
			}
			edtFood(true, 1);
			moveRight();
			break;
		}
	}
	
	/**
	 * �жϴ�ʱ�Ƿ�ͷ�������������ཻ�ˣ�����Ƿ���true
	 */
	private boolean isJoin(boolean isX, int offset) {
		Point next = null;
		for(Point block : blocks) {
			if(next == null) {
				//ȷ����һ��������
				next = isX ? new Point(block.x + offset, block.y) : new Point(block.x, block.y  + offset);
			}else {
				if(next.equals(block)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * ��ʳ��
	 */
	private void edtFood(boolean isX, int offset) {
		Point head = blocks.element();
		Point next = isX ? new Point(head.x + offset, head.y) : new Point(head.x, head.y + offset);
		if(next.equals(food)) {
			//��ʳ����ӵ��ߵ�ͷ��
			blocks.addFirst(new Point(food.x, food.y));
			//��������ʳ��
			generateFood();
			//�ӷ�
			this.currentPoint ++;
			//�Ƿ������߷�
			if(this.currentPoint > this.maxPoint) {
				this.isMaxPointChanged = true;
				this.maxPoint = this.currentPoint;
				setPoints(false, true, true, false);
			}
			setPoints(false, false, true, false);
		}
	}

	private void moveLeft() {
		//�ж��Ƿ�ײǽ
		if(blocks.element().x == 0) {
			gameOver();
			return;
		}
		//�����ÿһ���ڵ㶼����ǰһ��
		//ע�⽻����������ֱ��=����
		Point pre = null;;
		Point temp = new Point();
		for(Point block : blocks) {
			if(pre == null) {
				pre = new Point();
				pre.setLocation(block.x, block.y);
				block.x --;
			}else {
				temp.setLocation(block.x, block.y);
				block.setLocation(pre.x, pre.y);
				pre.setLocation(temp.x, temp.y);
			}
		}
	}

	private void moveDown() {
		if(this.currentDirection == Direction.UP) {
			return;
		}
		//�ж��Ƿ�ײǽ
		if(blocks.element().y == 39) {
			gameOver();
			return;
		}
		//�����ÿһ���ڵ㶼����ǰһ��
		Point pre = null;;
		Point temp = new Point();
		for(Point block : blocks) {
			if(pre == null) {
				pre = new Point();
				pre.setLocation(block.x, block.y);
				block.y ++;
			}else {
				temp.setLocation(block.x, block.y);
				block.setLocation(pre.x, pre.y);
				pre.setLocation(temp.x, temp.y);
			}
		}
	}

	private void moveUp() {
		if(this.currentDirection == Direction.DOWN) {
			return;
		}
		//�ж��Ƿ�ײǽ
		if(blocks.element().y == 0) {
			gameOver();
			return;
		}
		//�����ÿһ���ڵ㶼����ǰһ��
		Point pre = null;;
		Point temp = new Point();
		for(Point block : blocks) {
			if(pre == null) {
				pre = new Point();
				pre.setLocation(block.x, block.y);
				block.y --;
			}else {
				temp.setLocation(block.x, block.y);
				block.setLocation(pre.x, pre.y);
				pre.setLocation(temp.x, temp.y);
			}
		}
	}

	private void moveRight() {
		if(this.currentDirection == Direction.LEFT) {
			return;
		}
		//�ж��Ƿ�ײǽ
		if(blocks.element().x == 39) {
			gameOver();
			return;
		}
		//�����ÿһ���ڵ㶼����ǰһ��
		Point pre = null;;
		Point temp = new Point();
		for(Point block : blocks) {
			if(pre == null) {
				pre = new Point();
				pre.setLocation(block.x, block.y);
				block.x ++;
			}else {
				temp.setLocation(block.x, block.y);
				block.setLocation(pre.x, pre.y);
				pre.setLocation(temp.x, temp.y);
			}
		}
	}
	
	/**
	 * ��Ϸ����
	 */
	private void gameOver() {
		//��ͣ�߳�
		snake.pause();
		//����0 �˳�1 ֱ�ӹرնԻ���-1
		int option = JOptionPane.showOptionDialog(null, "��Ϸ�������´κ���!", "��ʾ", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE, null, new Object[]{"����", "�˳�"}, null);
		switch (option) {
		case 1:
			//������ֵ������ô����
			if(isMaxPointChanged) 
				setPoints(false, false, false, true);
			System.exit(0);
			break;
		default:
			restart();
			break;
		}
	}

	/**
	 * ���¿�ʼ
	 */
	public void restart() {
		this.currentPoint = 0;
		this.currentDirection = Direction.LEFT;
		if(this.isMaxPointChanged) {
			//�������
			setPoints(false, false, false, true);
			this.isMaxPointChanged = false;
		}
		start(mainFrame, blocks, food, maxPointLabel, currentPointLabel);
	}
	
	/**
	 * ��ȡ��ǰ����
	 */
	public int getCurrentPoint() {
		return this.currentPoint;
	}

}

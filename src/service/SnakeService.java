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
 * 游戏逻辑
 * @author skywalker
 *
 */
public class SnakeService {
	
	private JFrame mainFrame;
	private Deque<Point> blocks;
	private Point food;
	private Random random = new Random();
	/**默认运动方向向左**/
	private Direction currentDirection = Direction.LEFT;
	/**显示最高分以及当前分的label**/
	private JLabel maxPointLabel;
	private JLabel currentPointLabel;
	/**最高分以及当前分数**/
	private int maxPoint;
	private int currentPoint;
	/**最高分是否改变了**/
	private boolean isMaxPointChanged = false;
	/**蛇线程**/
	private SnakeThread snake;

	/**
	 * 开始游戏，在随机的位置显示一条蛇，长度为3，横向
	 * @param mainFrame 游戏界面
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
		//清空蛇
		this.blocks.clear();
		//随机生成蛇的坐标
		int x = random.nextInt(30) + 6;
		int y = random.nextInt(40);
		this.blocks.offer(new Point(x, y));
		this.blocks.offer(new Point(x + 1, y));
		this.blocks.offer(new Point(x + 2, y));
		//随机生成食物坐标
		generateFood();
		//初始化分数
		setPoints(true, false, true, false);
		//初始化移动线程
		if(snake == null) {
			snake = new SnakeThread(this);
			snake.start();
		}else {
			snake.consume();
		}
	}
	
	/**
	 * 设置分数
	 * @param isInit 如果为true从文件读取最大值，并且当前分数设0
	 * @param isSetMaxPoint true刷新最大值
	 * @param isSetCurrentPoint true刷新当前值
	 * @param isSaveMaxPoint true把最高分写入文件
	 */
	private void setPoints(boolean isInit, boolean isSetMaxPoint, boolean isSetCurrentPoint, boolean isSaveMaxPoint) {
		if(isInit) {
			//初始化
			maxPoint = PointUtil.readMaxPoint();
			maxPointLabel.setText("最高得分:" + maxPoint);
			currentPointLabel.setText("当前得分:" + currentPoint);
		}else {
			if(isSetMaxPoint) {
				maxPointLabel.setText("最高得分:" + maxPoint);
			}
			if(isSaveMaxPoint && isMaxPointChanged) {
				PointUtil.writeMaxPoint(maxPoint);
			}
			if(isSetCurrentPoint) {
				currentPointLabel.setText("当前得分:" + currentPoint);
			}
		}
	}
	
	/**
	 * 生成食物
	 */
	private void generateFood() {
		int x = random.nextInt(40);
		int y = random.nextInt(40);
		//检测是否出现在蛇的内部
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
	 * 游戏控制
	 * 上38 下40 左37 右39
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
		//p键游戏的暂停与恢复
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
	 * 运动
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
	 * 判断此时是否头部和其它部分相交了，如果是返回true
	 */
	private boolean isJoin(boolean isX, int offset) {
		Point next = null;
		for(Point block : blocks) {
			if(next == null) {
				//确定下一个的坐标
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
	 * 吃食物
	 */
	private void edtFood(boolean isX, int offset) {
		Point head = blocks.element();
		Point next = isX ? new Point(head.x + offset, head.y) : new Point(head.x, head.y + offset);
		if(next.equals(food)) {
			//把食物添加到蛇的头部
			blocks.addFirst(new Point(food.x, food.y));
			//重新生成食物
			generateFood();
			//加分
			this.currentPoint ++;
			//是否大于最高分
			if(this.currentPoint > this.maxPoint) {
				this.isMaxPointChanged = true;
				this.maxPoint = this.currentPoint;
				setPoints(false, true, true, false);
			}
			setPoints(false, false, true, false);
		}
	}

	private void moveLeft() {
		//判断是否撞墙
		if(blocks.element().x == 0) {
			gameOver();
			return;
		}
		//后面的每一个节点都跟随前一个
		//注意交换方法，别直接=交换
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
		//判断是否撞墙
		if(blocks.element().y == 39) {
			gameOver();
			return;
		}
		//后面的每一个节点都跟随前一个
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
		//判断是否撞墙
		if(blocks.element().y == 0) {
			gameOver();
			return;
		}
		//后面的每一个节点都跟随前一个
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
		//判断是否撞墙
		if(blocks.element().x == 39) {
			gameOver();
			return;
		}
		//后面的每一个节点都跟随前一个
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
	 * 游戏结束
	 */
	private void gameOver() {
		//暂停线程
		snake.pause();
		//重玩0 退出1 直接关闭对话框-1
		int option = JOptionPane.showOptionDialog(null, "游戏结束，下次好运!", "提示", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE, null, new Object[]{"重玩", "退出"}, null);
		switch (option) {
		case 1:
			//如果最大值变了那么保存
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
	 * 重新开始
	 */
	public void restart() {
		this.currentPoint = 0;
		this.currentDirection = Direction.LEFT;
		if(this.isMaxPointChanged) {
			//保存分数
			setPoints(false, false, false, true);
			this.isMaxPointChanged = false;
		}
		start(mainFrame, blocks, food, maxPointLabel, currentPointLabel);
	}
	
	/**
	 * 获取当前分数
	 */
	public int getCurrentPoint() {
		return this.currentPoint;
	}

}

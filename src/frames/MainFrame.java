package frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import service.SnakeService;
import util.Frameutil;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 5779432574807776097L;
	/***方块尺寸*/
	public static final int BLOCKSIZE = 10;
	/***方块的颜色*/
	private static final Color BLOCKCOLOR = new Color(7,211,16);
	/**食物的颜色**/
	private static final Color FOODCOLOR = Color.YELLOW;
	/*** 方块坐标队列*/
	private Deque<Point> blocks = new LinkedList<Point>();
	/*** 食物坐标 */
	private Point food = new Point();
	private JPanel panel;
	private JPanel contentPane;

	public MainFrame() {
		
		try {
			//windows样式	
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception e) {
			e.printStackTrace();
		}
		//图标
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/snake.png")));
		SnakeService snakeService = new SnakeService();
		setResizable(false);
		setTitle("Greedy Snake");
		//居中
		Point point = Frameutil.getMiddlePoint(407, 460);
		setBounds(point.x, point.y, 407, 460);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//上方信息栏
		JPanel toolBar = new JPanel();
		toolBar.setBounds(0, 0, 407, 30);
		toolBar.setBackground(Color.GRAY);
		toolBar.setLayout(null);
		
		//显示最高分
		JLabel maxPoint = new JLabel();
		Font font = new Font("微软雅黑", Font.BOLD, 20);
		maxPoint.setBounds(10, 0, 120, 30);
		maxPoint.setForeground(Color.GREEN);
		maxPoint.setFont(font);
		toolBar.add(maxPoint);
		
		//显示当前分数
		JLabel currentPoint = new JLabel();
		currentPoint.setFont(font);
		currentPoint.setForeground(Color.GREEN);
		currentPoint.setBounds(160, 0, 120, 30);
		toolBar.add(currentPoint);
		
		//重新开始
		JLabel restart = new JLabel();
		restart.setFont(font);
		restart.setText("重   玩");
		restart.setBounds(330, 0, 110, 30);
		restart.setForeground(Color.GREEN);
		restart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				snakeService.restart();
			}
		});
		toolBar.add(restart);
		add(toolBar);
		
		//主panel
		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(0, 30, 407, 401);
		getContentPane().add(panel);
		
		//增加键盘事件监听
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				snakeService.control(e.getKeyCode());
			};
		});
		
		//开始游戏
		snakeService.start(this, blocks, food, maxPoint, currentPoint);
	}
	
	/**
	 * 重写，画出队列中的坐标
	 */
	@Override
	public void paint(Graphics g) {
		 // 在重绘函数中实现双缓冲机制     
        Image offScreenImage = this.createImage(WIDTH, HEIGHT);     
        // 获得截取图片的画布     
        Graphics gImage = offScreenImage.getGraphics();     
        // 获取画布的底色并且使用这种颜色填充画布,如果没有填充效果的画，则会出现拖动的效果     
        gImage.setColor(gImage.getColor());     
        gImage.fillRect(0, 0, WIDTH, HEIGHT); // 有清楚上一步图像的功能，相当于gImage.clearRect(0, 0, WIDTH, HEIGHT)     
        // 调用父类的重绘方法，传入的是截取图片上的画布，防止再从最底层来重绘     
        super.paint(gImage);     
        
		//首先调用父类的此方法，不然panel的背景色就没了
		super.paint(g);
		g.setColor(BLOCKCOLOR);
		for(Point point : blocks) {
			g.fillRect(3 + point.x * BLOCKSIZE, 56 + point.y * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
		}
		//画出食物
		g.setColor(FOODCOLOR);
		g.fillRect(3 + food.x * BLOCKSIZE, 56 + food.y * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
		
		 // 将接下来的图片加载到窗体画布上去，才能考到每次画的效果     
        g.drawImage(offScreenImage, 0, 0, null);     
	}
	
}

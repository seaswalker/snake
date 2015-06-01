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
	/***����ߴ�*/
	public static final int BLOCKSIZE = 10;
	/***�������ɫ*/
	private static final Color BLOCKCOLOR = new Color(7,211,16);
	/**ʳ�����ɫ**/
	private static final Color FOODCOLOR = Color.YELLOW;
	/*** �����������*/
	private Deque<Point> blocks = new LinkedList<Point>();
	/*** ʳ������ */
	private Point food = new Point();
	private JPanel panel;
	private JPanel contentPane;

	public MainFrame() {
		
		try {
			//windows��ʽ	
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception e) {
			e.printStackTrace();
		}
		//ͼ��
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/snake.png")));
		SnakeService snakeService = new SnakeService();
		setResizable(false);
		setTitle("Greedy Snake");
		//����
		Point point = Frameutil.getMiddlePoint(407, 460);
		setBounds(point.x, point.y, 407, 460);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//�Ϸ���Ϣ��
		JPanel toolBar = new JPanel();
		toolBar.setBounds(0, 0, 407, 30);
		toolBar.setBackground(Color.GRAY);
		toolBar.setLayout(null);
		
		//��ʾ��߷�
		JLabel maxPoint = new JLabel();
		Font font = new Font("΢���ź�", Font.BOLD, 20);
		maxPoint.setBounds(10, 0, 120, 30);
		maxPoint.setForeground(Color.GREEN);
		maxPoint.setFont(font);
		toolBar.add(maxPoint);
		
		//��ʾ��ǰ����
		JLabel currentPoint = new JLabel();
		currentPoint.setFont(font);
		currentPoint.setForeground(Color.GREEN);
		currentPoint.setBounds(160, 0, 120, 30);
		toolBar.add(currentPoint);
		
		//���¿�ʼ
		JLabel restart = new JLabel();
		restart.setFont(font);
		restart.setText("��   ��");
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
		
		//��panel
		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(0, 30, 407, 401);
		getContentPane().add(panel);
		
		//���Ӽ����¼�����
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				snakeService.control(e.getKeyCode());
			};
		});
		
		//��ʼ��Ϸ
		snakeService.start(this, blocks, food, maxPoint, currentPoint);
	}
	
	/**
	 * ��д�����������е�����
	 */
	@Override
	public void paint(Graphics g) {
		 // ���ػ溯����ʵ��˫�������     
        Image offScreenImage = this.createImage(WIDTH, HEIGHT);     
        // ��ý�ȡͼƬ�Ļ���     
        Graphics gImage = offScreenImage.getGraphics();     
        // ��ȡ�����ĵ�ɫ����ʹ��������ɫ��仭��,���û�����Ч���Ļ����������϶���Ч��     
        gImage.setColor(gImage.getColor());     
        gImage.fillRect(0, 0, WIDTH, HEIGHT); // �������һ��ͼ��Ĺ��ܣ��൱��gImage.clearRect(0, 0, WIDTH, HEIGHT)     
        // ���ø�����ػ淽����������ǽ�ȡͼƬ�ϵĻ�������ֹ�ٴ���ײ����ػ�     
        super.paint(gImage);     
        
		//���ȵ��ø���Ĵ˷�������Ȼpanel�ı���ɫ��û��
		super.paint(g);
		g.setColor(BLOCKCOLOR);
		for(Point point : blocks) {
			g.fillRect(3 + point.x * BLOCKSIZE, 56 + point.y * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
		}
		//����ʳ��
		g.setColor(FOODCOLOR);
		g.fillRect(3 + food.x * BLOCKSIZE, 56 + food.y * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
		
		 // ����������ͼƬ���ص����廭����ȥ�����ܿ���ÿ�λ���Ч��     
        g.drawImage(offScreenImage, 0, 0, null);     
	}
	
}

package util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * 界面工具类
 * @author skywalker
 *
 */
public class Frameutil {

	/**
	 * 根据给定的坐标值计算出居中的坐标
	 */
	public static Point getMiddlePoint(int x, int y) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double width = dimension.getWidth();
		double height = dimension.getHeight();
		Point point = new Point();
		point.setLocation((width - x) / 2, (height - y) / 2 - 100);
		return point;
	}
	
}

package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 读取以及保存最高分
 * @author skywalker
 *
 */
public class PointUtil {

	public static int readMaxPoint() {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = new FileInputStream("data/point.ini");
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String temp = br.readLine();
			if(StringUtil.isValidate(temp)) {
				int point = Integer.parseInt(temp.split("=")[1]);
				return point;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}finally {
			try {
				if(br != null) {
					br.close();
					br = null;
				}
				if(is != null) {
					is.close();
					is = null;
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static void writeMaxPoint(int maxPoint) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			File file = new File("data/point.ini");
			fos = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			String content = "point=" + maxPoint;
			bw.write(content);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(bw != null) {
					bw.close();
					bw = null;
				}
				if(fos != null) {
					fos.close();
					fos = null;
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

package servlet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * ���ܣ�����д�ľŹ����ռ�����Ԥ��ͼƬ
 * 
 * @author administrator
 */
public class CreateImg extends HttpServlet {

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = getServletContext().getRealPath("/");// �����Ŀ��ʵ·��
		// ��ֹ����
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "No-cache");
		response.setDateHeader("Expires", 0);
		// ָ�����ɵ���Ӧ��ͼƬ
		response.setContentType("image/jpeg");
		int width = 600; // ͼƬ�Ŀ��
		int height = 600; // ͼƬ�ĸ߶�
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics(); // ��ȡGraphics��Ķ���
		HttpSession session = request.getSession(true);
		String template = session.getAttribute("template").toString();
		String weather = session.getAttribute("weather").toString();

		// ��ȡͼƬ������·��
		weather = path + "images/" + weather + ".png";// ��ȡͼƬ������·��
		// ��ȡ����ͼƬ
		String[] content = (String[]) session.getAttribute("diary");
		File bgImgFile;
		if ("Ĭ��".equals(template)) {
			bgImgFile = new File(path + "images/bg_00.jpg");
			Image src = ImageIO.read(bgImgFile); // ����Image����
			g.drawImage(src, 0, 0, width, height, null); // ���Ʊ���ͼƬ
			outWord(g, content, weather, 0, 0);
		} else if ("Ů��".equals(template)) {
			bgImgFile = new File(path + "images/bg_01.jpg");
			Image src = ImageIO.read(bgImgFile); // ����Image����
			g.drawImage(src, 0, 0, width, height, null); // ���Ʊ���ͼƬ
			outWord(g, content, weather, 25, 110);

		} else {
			bgImgFile = new File(path + "images/bg_02.jpg");
			Image src = ImageIO.read(bgImgFile); // ����Image����
			g.drawImage(src, 0, 0, width, height, null); // ���Ʊ���ͼƬ
			outWord(g, content, weather, 30, 5);
		}
		/********************************************************/
		// �����ɵ��ռ�ͼƬ���浽Session��
		ImageIO.write(image, "PNG", response.getOutputStream());
		session.setAttribute("diaryImg", image);
	}

	/**
	 * ���ܣ����Ź����ռǵ�����д��ͼƬ��
	 * 
	 * @param g
	 * @param content
	 * @param offsetX
	 * @param offsetY
	 */
	public void outWord(Graphics g, String[] content, String weather,
			int offsetX, int offsetY) {
		Font mFont = new Font("΢���ź�", Font.PLAIN, 26); // ͨ��Font��������
		g.setFont(mFont); // ��������
		g.setColor(new Color(0, 0, 0)); // ������ɫΪ��ɫ
		int contentLen = 0;
		int x = 0; // ���ֵĺ�����
		int y = 0; // ���ֵ�������
		for (int i = 0; i < content.length; i++) {
			contentLen = content[i].length(); // ��ȡ���ݵĳ���
			x = 45 + (i % 3) * 170 + offsetX;
			y = 130 + (i / 3) * 140 + offsetY;
			if (content[i].equals("weathervalue")) {
				File bgImgFile = new File(weather);
				mFont = new Font("΢���ź�", Font.PLAIN, 14); // ͨ��Font��������
				g.setFont(mFont); // ��������
				Date date = new Date();
				String newTime = new SimpleDateFormat("yyyy��M��d�� E")
						.format(date);
				g.drawString(newTime, x - 12, y - 60);
				Image src;
				try {
					src = ImageIO.read(bgImgFile);
					g.drawImage(src, x + 10, y - 40, 80, 80, null); // ��������ͼƬ
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // ����Image����
				continue;
			}
			if (contentLen < 5) {
				switch (contentLen % 5) {
				case 1:
					mFont = new Font("΢���ź�", Font.PLAIN, 40); // ͨ��Font��������
					g.setFont(mFont); // ��������
					g.drawString(content[i], x + 40, y);
					break;
				case 2:
					mFont = new Font("΢���ź�", Font.PLAIN, 36); // ͨ��Font��������
					g.setFont(mFont); // ��������
					g.drawString(content[i], x + 25, y);
					break;
				case 3:
					mFont = new Font("΢���ź�", Font.PLAIN, 30); // ͨ��Font��������
					g.setFont(mFont); // ��������
					g.drawString(content[i], x + 20, y);
					break;
				case 4:
					mFont = new Font("΢���ź�", Font.PLAIN, 28); // ͨ��Font��������
					g.setFont(mFont); // ��������
					g.drawString(content[i], x + 10, y);
				}
			} else {
				mFont = new Font("΢���ź�", Font.PLAIN, 22); // ͨ��Font��������
				g.setFont(mFont); // ��������
				if (Math.ceil(contentLen / 5.0) == 1) {
					g.drawString(content[i], x, y);
				} else if (Math.ceil(contentLen / 5.0) == 2) {
					// ������д
					g.drawString(content[i].substring(0, 5), x, y - 20);
					g.drawString(content[i].substring(5), x, y + 10);
				} else if (Math.ceil(contentLen / 5.0) == 3) {
					// ������д
					g.drawString(content[i].substring(0, 5), x, y - 30);
					g.drawString(content[i].substring(5, 10), x, y);
					g.drawString(content[i].substring(10), x, y + 30);
				}
			}
		}
		g.dispose();
	}

}


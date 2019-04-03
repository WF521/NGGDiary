package servlet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DiaryDao;
import model.Comment;
import model.Diary;
import tools.MyPagination;

/**
 * Servlet implementation class DiaryServlet
 */
public class DiaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MyPagination pagination = null;// ���ݷ�ҳ��Ķ���
	DiaryDao dao = null;// �ռ���ص����ݿ������Ķ���

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DiaryServlet() {
		super();
		// TODO Auto-generated constructor stub
		dao = new DiaryDao();// ʵ�����ռ���ص����ݿ������Ķ���
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("preview".equals(action)) {
			preview(request, response); // Ԥ���Ź����ռ�
		} else if ("save".equals(action)) {
			save(request, response); // ����Ź����ռ�
		} else if ("listAllDiary".equals(action)) {
			listAllDiary(request, response); // ��ѯȫ���Ź����ռ�
		} else if ("listMyDiary".equals(action)) {
			listMyDiary(request, response); // ��ѯ�ҵ��ռ�
		} else if ("delDiary".equals(action)) {
			delDiary(request, response); // ɾ���ҵ��ռ�
		} else if ("getComment".equals(action)) {
			listComment(request, response);
		} else if ("clickLike".equals(action)) {
			clickLike(request, response);
		} else if ("postComment".equals(action)) {
			postComment(request, response);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);// ִ��doPost()����
	}

	/**
	 * ���ܣ�ɾ���ռ�
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void delDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id")); // ��ȡҪɾ�����ռǵ�ID
		String imgName = request.getParameter("imgName"); // ��ȡͼƬ��
		String url = request.getParameter("url"); // ��ȡ���ص�URL��ַ
		int rtn = dao.delDiary(id);// ɾ���ռ�
		PrintWriter out = response.getWriter();
		if (rtn > 0) {// ��ɾ���ռǳɹ�ʱ
			/************* ɾ���ռ�ͼƬ������ͼ ******************/
			String path = getServletContext().getRealPath("\\")
					+ "images\\diary\\";
			java.io.File file = new java.io.File(path + imgName + "scale.jpg");// ��ȡ����ͼ
			file.delete(); // ɾ��ָ�����ļ�
			file = new java.io.File(path + imgName + ".png");// ��ȡ�ռ�ͼƬ
			file.delete();
			/*******************************/
			out.println("<script>alert('ɾ���ռǳɹ���');window.location.href='DiaryServlet?action="
					+ url + "';</script>");
		} else {// ��ɾ���ռ�ʧ��ʱ
			out.println("<script>alert('ɾ���ռ�ʧ�ܣ����Ժ����ԣ�');history.back();</script>");
		}
	}

	/**
	 * ���ܣ���ѯ�ҵ��ռ�
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void listMyDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// ��ȡ�ռ�����
		String strPage = (String) request.getParameter("Page");// ��ȡ��ǰҳ��
		int Page = 1;
		List<Diary> list = null;
		if (strPage == null) {
			int userid = Integer.parseInt(session.getAttribute("uid")
					.toString()); // ��ȡ�û�ID��
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid  where d.userid="
					+ userid + " order by d.writeTime DESC";// Ӧ�������Ӳ�ѯ�ռ���Ϣ
			pagination = new MyPagination();
			list = dao.queryDiary(sql); // ��ȡ�ռ�����
			int pagesize = 4; // ָ��ÿҳ��ʾ�ļ�¼��
			list = pagination.getInitPage(list, Page, pagesize); // ��ʼ����ҳ��Ϣ
			request.getSession().setAttribute("pagination", pagination);// �����ҳ��Ϣ
		} else {
			pagination = (MyPagination) request.getSession().getAttribute(
					"pagination");// ��ȡ��ҳ��Ϣ
			Page = pagination.getPage(strPage);
			list = pagination.getAppointPage(Page); // ��ȡָ��ҳ����
		}
		request.setAttribute("diaryList", list); // ���浱ǰҳ���ռ���Ϣ
		request.setAttribute("Page", Page); // ����ĵ�ǰҳ��
		request.setAttribute("url", "listMyDiary");// ���浱ǰҳ��URL��ַ
		request.getRequestDispatcher("listAllDiary.jsp").forward(request,
				response);// �ض���ҳ�浽listAllDiary.jsp
	}

	/**
	 * ���ܣ�Ԥ���Ź����ռ�
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void preview(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String title = request.getParameter("title");// ��ȡ�ռǱ���
		String template = request.getParameter("template");// ��ȡ�ռ�ģ��
		String weather = request.getParameter("weather");// ��ȡ����
		// ��ȡ�ռ�����
		String[] content = request.getParameterValues("content");
		for (int i = 0; i < content.length; i++) {
			if (content[i].equals(null) || content[i].equals("")
					|| content[i].equals("���ڴ���������")) {
				content[i] = "ûɶ��˵��"; // Ϊû���������ݵ���Ŀ����Ĭ��ֵ
			}
		}
		HttpSession session = request.getSession(true); // ��ȡHttpSession
		session.setAttribute("template", template);// ����ѡ���ģ��
		session.setAttribute("weather", weather); // ��������
		session.setAttribute("title", title); // �����ռǱ���
		session.setAttribute("diary", content); // �����ռ�����
		request.getRequestDispatcher("preview.jsp").forward(request, response);// �ض���ҳ��
	}

	/**
	 * ���ܣ�����Ź����ռ�
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void save(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		BufferedImage image = (BufferedImage) session.getAttribute("diaryImg");
		String url = request.getRequestURL().toString();// ��ȡ�����URL��ַ
		url = request.getRealPath("/");// ��ȡ�����ʵ�ʵ�ַ
		long date = new Date().getTime();// ��ȡ��ǰʱ��
		Random r = new Random(date);
		long value = r.nextLong();// ����һ�������͵������
		url = url + "images/diary/" + value;// ����ͼƬ��URL��ַ
		String scaleImgUrl = url + "scale.jpg";// ��������ͼ��URL��ַ
		url = url + ".png";
		ImageIO.write(image, "PNG", new File(url));
		/***************** ����ͼƬ����ͼ ******************************************/
		File file = new File(url); // ��ȡԭ�ļ�
		Image src = ImageIO.read(file);
		int old_w = src.getWidth(null);// ��ȡԭͼƬ�Ŀ�
		int old_h = src.getHeight(null);// ��ȡԭͼƬ�ĸ�
		int new_w = 0;// ��ͼƬ�Ŀ�
		int new_h = 0;// ��ͼƬ�ĸ�
		double temp = 0;// ���ű���
		/********* �������ű��� ***************/
		double tagSize = 60;
		if (old_w > old_h) {
			temp = old_w / tagSize;
		} else {
			temp = old_h / tagSize;
		}
		/*************************************/
		new_w = (int) Math.round(old_w / temp);// ������ͼƬ�Ŀ�
		new_h = (int) Math.round(old_h / temp);// ������ͼƬ�ĸ�
		image = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		src = src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH);
		image.getGraphics().drawImage(src, 0, 0, new_w, new_h, null);
		ImageIO.write(image, "JPG", new File(scaleImgUrl)); // ��������ͼ�ļ�
		/***********************************************************************/
		/**** ����д���ռǱ��浽���ݿ��� *****/
		Diary diary = new Diary();
		diary.setAddress(String.valueOf(value));// ����ͼƬ��ַ
		diary.setTitle(session.getAttribute("title").toString());// �����ռǱ���
		diary.setUserid(Integer
				.parseInt(session.getAttribute("uid").toString()));// �����û�ID
		int rtn = dao.saveDiary(diary); // �����ռ�
		PrintWriter out = response.getWriter();
		if (rtn > 0) {// ������ɹ�ʱ
			out.println("<script>alert('����ɹ���');window.location.href='DiaryServlet?action=listAllDiary';</script>");
		} else {// ������ʧ��ʱ
			out.println("<script>alert('�����ռ�ʧ�ܣ����Ժ����ԣ�');history.back();</script>");
		}
		/*********************************/
	}

	/**
	 * ���ܣ���ѯȫ���Ź����ռ�
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void listAllDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String strPage = (String) request.getParameter("Page");// ��ȡ��ǰҳ��
		int Page = 1;
		List<Diary> list = null;
		if (strPage == null) {// ��ҳ���������
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid order by d.writeTime DESC limit 50";
			pagination = new MyPagination();
			list = dao.queryDiary(sql); // ��ȡ�ռ�����
			int pagesize = 4; // ָ��ÿҳ��ʾ�ļ�¼��
			list = pagination.getInitPage(list, Page, pagesize); // ��ʼ����ҳ��Ϣ
			request.getSession().setAttribute("pagination", pagination);// ��session��д������
		} else {
			pagination = (MyPagination) request.getSession().getAttribute(
					"pagination");
			Page = pagination.getPage(strPage);// ��ȡ��ǰҳ��
			list = pagination.getAppointPage(Page); // ��ȡָ��ҳ����
		}
		request.setAttribute("diaryList", list); // ���浱ǰҳ���ռ���Ϣ
		request.setAttribute("Page", Page); // ����ĵ�ǰҳ��
		request.setAttribute("url", "listAllDiary");// ���浱ǰҳ���URL
		request.getRequestDispatcher("listAllDiary.jsp").forward(request,
				response);// �ض���ҳ��
	}

	/**
	 * ��ȡ����
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listComment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// �ռ�id
		String commentDivID = (String) request.getParameter("commentDivID");// ������id
		List<Comment> list = dao.getComments(Integer.parseInt(diaryID));
		StringBuilder comments = new StringBuilder();
		comments.append(commentDivID + "<�ָ���>");
		for (int i = 0; i < list.size(); i++) {
			Comment c = list.get(i);
			comments.append("<B>" + c.getFromUserName() + "��</B>");
			comments.append(c.getContent() + "<br>");
		}
		request.setAttribute("returnValue", comments.toString());// ������ʾ��Ϣ
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// �ض���ҳ��
	}

	/**
	 * ����
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void clickLike(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// �ռ�id
		String username = (String) request.getParameter("username");// �����û���
		String likeDivID = (String) request.getParameter("likeDivID");// ���������
		boolean success = dao.updateLikesCount(Integer.parseInt(diaryID),
				username);
		StringBuilder str = new StringBuilder();
		str.append(likeDivID + "<�ָ���>");
		if (success) {
			str.append("OK" + "<�ָ���>");
			int count = dao.getLikesCount(Integer.parseInt(diaryID));
			str.append("��(" + count + ")");
		} else {
			str.append("false");
		}
		request.setAttribute("returnValue", str.toString());// ������ʾ��Ϣ
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// �ض���ҳ��
	}

	/**
	 * �������
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void postComment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// �ռ�id
		String username = (String) request.getParameter("username");// �����û���
		String commentDivID = (String) request.getParameter("commentDivID");// ���������
		String content = (String) request.getParameter("content");// ��������
		String textareaID = (String) request.getParameter("textareaID");// �����ı�����
		String commentsCountID = (String) request
				.getParameter("commentsCountID");// ���������
		boolean success = dao.addComments(Integer.parseInt(diaryID), username,
				content);
		StringBuilder comments = new StringBuilder();
		if (success) {
			List<Comment> list = dao.getComments(Integer.parseInt(diaryID));
			comments.append(commentDivID + "<�ָ���>");
			for (int i = 0; i < list.size(); i++) {
				Comment c = list.get(i);
				comments.append("<B>" + c.getFromUserName() + "��</B>");
				comments.append(c.getContent() + "<br>");
			}
			comments.append("<�ָ���>" + textareaID);
			comments.append("<�ָ���>" + commentsCountID);
			int commentsCount = list.size();
			comments.append("<�ָ���>����(" + commentsCount + ")");
		} else {
			comments.append("error");
		}
		request.setAttribute("returnValue", comments.toString());// ������ʾ��Ϣ
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// �ض���ҳ��
	}
}

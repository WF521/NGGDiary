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
	MyPagination pagination = null;// 数据分页类的对象
	DiaryDao dao = null;// 日记相关的数据库操作类的对象

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DiaryServlet() {
		super();
		// TODO Auto-generated constructor stub
		dao = new DiaryDao();// 实例化日记相关的数据库操作类的对象
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("preview".equals(action)) {
			preview(request, response); // 预览九宫格日记
		} else if ("save".equals(action)) {
			save(request, response); // 保存九宫格日记
		} else if ("listAllDiary".equals(action)) {
			listAllDiary(request, response); // 查询全部九宫格日记
		} else if ("listMyDiary".equals(action)) {
			listMyDiary(request, response); // 查询我的日记
		} else if ("delDiary".equals(action)) {
			delDiary(request, response); // 删除我的日记
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
		doPost(request, response);// 执行doPost()方法
	}

	/**
	 * 功能：删除日记
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void delDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id")); // 获取要删除的日记的ID
		String imgName = request.getParameter("imgName"); // 获取图片名
		String url = request.getParameter("url"); // 获取返回的URL地址
		int rtn = dao.delDiary(id);// 删除日记
		PrintWriter out = response.getWriter();
		if (rtn > 0) {// 当删除日记成功时
			/************* 删除日记图片及缩略图 ******************/
			String path = getServletContext().getRealPath("\\")
					+ "images\\diary\\";
			java.io.File file = new java.io.File(path + imgName + "scale.jpg");// 获取缩略图
			file.delete(); // 删除指定的文件
			file = new java.io.File(path + imgName + ".png");// 获取日记图片
			file.delete();
			/*******************************/
			out.println("<script>alert('删除日记成功！');window.location.href='DiaryServlet?action="
					+ url + "';</script>");
		} else {// 当删除日记失败时
			out.println("<script>alert('删除日记失败，请稍后重试！');history.back();</script>");
		}
	}

	/**
	 * 功能：查询我的日记
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void listMyDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// 获取日记内容
		String strPage = (String) request.getParameter("Page");// 获取当前页码
		int Page = 1;
		List<Diary> list = null;
		if (strPage == null) {
			int userid = Integer.parseInt(session.getAttribute("uid")
					.toString()); // 获取用户ID号
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid  where d.userid="
					+ userid + " order by d.writeTime DESC";// 应用内联接查询日记信息
			pagination = new MyPagination();
			list = dao.queryDiary(sql); // 获取日记内容
			int pagesize = 4; // 指定每页显示的记录数
			list = pagination.getInitPage(list, Page, pagesize); // 初始化分页信息
			request.getSession().setAttribute("pagination", pagination);// 保存分页信息
		} else {
			pagination = (MyPagination) request.getSession().getAttribute(
					"pagination");// 获取分页信息
			Page = pagination.getPage(strPage);
			list = pagination.getAppointPage(Page); // 获取指定页数据
		}
		request.setAttribute("diaryList", list); // 保存当前页的日记信息
		request.setAttribute("Page", Page); // 保存的当前页码
		request.setAttribute("url", "listMyDiary");// 保存当前页的URL地址
		request.getRequestDispatcher("listAllDiary.jsp").forward(request,
				response);// 重定向页面到listAllDiary.jsp
	}

	/**
	 * 功能：预览九宫格日记
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void preview(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String title = request.getParameter("title");// 获取日记标题
		String template = request.getParameter("template");// 获取日记模板
		String weather = request.getParameter("weather");// 获取天气
		// 获取日记内容
		String[] content = request.getParameterValues("content");
		for (int i = 0; i < content.length; i++) {
			if (content[i].equals(null) || content[i].equals("")
					|| content[i].equals("请在此输入文字")) {
				content[i] = "没啥可说的"; // 为没有设置内容的项目设置默认值
			}
		}
		HttpSession session = request.getSession(true); // 获取HttpSession
		session.setAttribute("template", template);// 保存选择的模板
		session.setAttribute("weather", weather); // 保存天气
		session.setAttribute("title", title); // 保存日记标题
		session.setAttribute("diary", content); // 保存日记内容
		request.getRequestDispatcher("preview.jsp").forward(request, response);// 重定向页面
	}

	/**
	 * 功能：保存九宫格日记
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
		String url = request.getRequestURL().toString();// 获取请求的URL地址
		url = request.getRealPath("/");// 获取请求的实际地址
		long date = new Date().getTime();// 获取当前时间
		Random r = new Random(date);
		long value = r.nextLong();// 生成一个长整型的随机数
		url = url + "images/diary/" + value;// 生成图片的URL地址
		String scaleImgUrl = url + "scale.jpg";// 生成缩略图的URL地址
		url = url + ".png";
		ImageIO.write(image, "PNG", new File(url));
		/***************** 生成图片缩略图 ******************************************/
		File file = new File(url); // 获取原文件
		Image src = ImageIO.read(file);
		int old_w = src.getWidth(null);// 获取原图片的宽
		int old_h = src.getHeight(null);// 获取原图片的高
		int new_w = 0;// 新图片的宽
		int new_h = 0;// 新图片的高
		double temp = 0;// 缩放比例
		/********* 计算缩放比例 ***************/
		double tagSize = 60;
		if (old_w > old_h) {
			temp = old_w / tagSize;
		} else {
			temp = old_h / tagSize;
		}
		/*************************************/
		new_w = (int) Math.round(old_w / temp);// 计算新图片的宽
		new_h = (int) Math.round(old_h / temp);// 计算新图片的高
		image = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		src = src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH);
		image.getGraphics().drawImage(src, 0, 0, new_w, new_h, null);
		ImageIO.write(image, "JPG", new File(scaleImgUrl)); // 保存缩略图文件
		/***********************************************************************/
		/**** 将填写的日记保存到数据库中 *****/
		Diary diary = new Diary();
		diary.setAddress(String.valueOf(value));// 设置图片地址
		diary.setTitle(session.getAttribute("title").toString());// 设置日记标题
		diary.setUserid(Integer
				.parseInt(session.getAttribute("uid").toString()));// 设置用户ID
		int rtn = dao.saveDiary(diary); // 保存日记
		PrintWriter out = response.getWriter();
		if (rtn > 0) {// 当保存成功时
			out.println("<script>alert('保存成功！');window.location.href='DiaryServlet?action=listAllDiary';</script>");
		} else {// 当保存失败时
			out.println("<script>alert('保存日记失败，请稍后重试！');history.back();</script>");
		}
		/*********************************/
	}

	/**
	 * 功能：查询全部九宫格日记
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void listAllDiary(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String strPage = (String) request.getParameter("Page");// 获取当前页码
		int Page = 1;
		List<Diary> list = null;
		if (strPage == null) {// 当页面初次运行
			String sql = "select d.*,u.username from tb_diary d inner join tb_user u on u.id=d.userid order by d.writeTime DESC limit 50";
			pagination = new MyPagination();
			list = dao.queryDiary(sql); // 获取日记内容
			int pagesize = 4; // 指定每页显示的记录数
			list = pagination.getInitPage(list, Page, pagesize); // 初始化分页信息
			request.getSession().setAttribute("pagination", pagination);// 向session中写入属性
		} else {
			pagination = (MyPagination) request.getSession().getAttribute(
					"pagination");
			Page = pagination.getPage(strPage);// 获取当前页码
			list = pagination.getAppointPage(Page); // 获取指定页数据
		}
		request.setAttribute("diaryList", list); // 保存当前页的日记信息
		request.setAttribute("Page", Page); // 保存的当前页码
		request.setAttribute("url", "listAllDiary");// 保存当前页面的URL
		request.getRequestDispatcher("listAllDiary.jsp").forward(request,
				response);// 重定向页面
	}

	/**
	 * 获取留言
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listComment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// 日记id
		String commentDivID = (String) request.getParameter("commentDivID");// 留言区id
		List<Comment> list = dao.getComments(Integer.parseInt(diaryID));
		StringBuilder comments = new StringBuilder();
		comments.append(commentDivID + "<分隔符>");
		for (int i = 0; i < list.size(); i++) {
			Comment c = list.get(i);
			comments.append("<B>" + c.getFromUserName() + "：</B>");
			comments.append(c.getContent() + "<br>");
		}
		request.setAttribute("returnValue", comments.toString());// 保存提示信息
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// 重定向页面
	}

	/**
	 * 点赞
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void clickLike(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// 日记id
		String username = (String) request.getParameter("username");// 点赞用户名
		String likeDivID = (String) request.getParameter("likeDivID");// 点赞区编号
		boolean success = dao.updateLikesCount(Integer.parseInt(diaryID),
				username);
		StringBuilder str = new StringBuilder();
		str.append(likeDivID + "<分隔符>");
		if (success) {
			str.append("OK" + "<分隔符>");
			int count = dao.getLikesCount(Integer.parseInt(diaryID));
			str.append("赞(" + count + ")");
		} else {
			str.append("false");
		}
		request.setAttribute("returnValue", str.toString());// 保存提示信息
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// 重定向页面
	}

	/**
	 * 添加留言
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void postComment(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String diaryID = (String) request.getParameter("diaryID");// 日记id
		String username = (String) request.getParameter("username");// 点赞用户名
		String commentDivID = (String) request.getParameter("commentDivID");// 点赞区编号
		String content = (String) request.getParameter("content");// 留言内容
		String textareaID = (String) request.getParameter("textareaID");// 留言文本域编号
		String commentsCountID = (String) request
				.getParameter("commentsCountID");// 评论数编号
		boolean success = dao.addComments(Integer.parseInt(diaryID), username,
				content);
		StringBuilder comments = new StringBuilder();
		if (success) {
			List<Comment> list = dao.getComments(Integer.parseInt(diaryID));
			comments.append(commentDivID + "<分隔符>");
			for (int i = 0; i < list.size(); i++) {
				Comment c = list.get(i);
				comments.append("<B>" + c.getFromUserName() + "：</B>");
				comments.append(c.getContent() + "<br>");
			}
			comments.append("<分隔符>" + textareaID);
			comments.append("<分隔符>" + commentsCountID);
			int commentsCount = list.size();
			comments.append("<分隔符>评论(" + commentsCount + ")");
		} else {
			comments.append("error");
		}
		request.setAttribute("returnValue", comments.toString());// 保存提示信息
		request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(request,
				response);// 重定向页面
	}
}

package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDao;
import model.CityMap;
import model.User;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDao = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
		super();
		userDao = new UserDao();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);// ִ��doPost()����
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("login".equals(action)) { // �û���¼
			this.login(request, response);
		} else if ("exit".equals(action)) {// �û��˳�
			this.exit(request, response);
		} else if ("save".equals(action)) { // �����û�ע����Ϣ
			this.save(request, response);
		} else if ("getProvince".equals(action)) { // ��ȡʡ����Ϣ
			this.getProvince(request, response);
		} else if ("getCity".equals(action)) { // ��ȡ������Ϣ
			this.getCity(request, response);
		} else if ("checkUser".equals(action)) {// ����û����Ƿ����
			this.checkUser(request, response);
		} else if ("forgetPwd1".equals(action)) { // �һ������һ��
			this.forgetPwd1(request, response);
		} else if ("forgetPwd2".equals(action)) { // �һ�����ڶ���
			this.forgetPwd2(request, response);
		}
	}

	/**
	 * ���ܣ��û���¼
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		User f = new User();
		f.setUsername(request.getParameter("username")); // ��ȡ�������û���
		f.setPwd(request.getParameter("pwd")); // ��ȡ����������
		int r = userDao.login(f);
		if (r > 0) {// ���û���¼�ɹ�ʱ
			HttpSession session = request.getSession();
			session.setAttribute("userName", f.getUsername());// �����û���
			session.setAttribute("uid", r);// �����û�ID
			request.setAttribute("returnValue", "��¼�ɹ���");// ������ʾ��Ϣ
			request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(
					request, response);// �ض���ҳ��
		} else {// ���û���¼���ɹ�ʱ
			request.setAttribute("returnValue", "��������û���������������������룡");
			request.getRequestDispatcher("ajaxReturnMessage.jsp").forward(
					request, response);// �ض���ҳ��
		}
	}

	/**
	 * ���ܣ��û��˳�
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void exit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();// ��ȡHttpSession�Ķ���
		session.invalidate();// ����session
		request.getRequestDispatcher("DiaryServlet?action=listAllDiary")
				.forward(request, response);// �ض���ҳ��
	}

	/**
	 * ����û����Ƿ�ע��
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void checkUser(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username"); // ��ȡ�û���
		username=java.net.URLDecoder.decode(username,"UTF-8");//�ַ�������
		String sql = "SELECT * FROM tb_user WHERE username='" + username + "'";
		String result = userDao.checkUser(sql); // ����UserDao���checkUser()�����ж��û��Ƿ�ע��
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(result); // ��������
		out.flush();
		out.close();
	}

	/**
	 * ��֤ע����û���Ϣ
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void save(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("user"); // ��ȡ�û���
		String pwd = request.getParameter("pwd"); // ��ȡ����
		String email = request.getParameter("email"); // ��ȡE-mail��ַ
		String city = request.getParameter("city"); // ��ȡ����
		String question = request.getParameter("question"); // ��ȡ������ʾ����
		String answer = request.getParameter("answer"); // ��ȡ������ʾ�����
		String sql = "INSERT INTO tb_user (username,pwd,email,question,answer,city) VALUE ('"
				+ username
				+ "','"
				+ pwd
				+ "','"
				+ email
				+ "','"
				+ question
				+ "','" + answer + "','" + city + "')";
		String result = userDao.save(sql);// �����û���Ϣ
		response.setContentType("text/html"); // ������Ӧ������
		PrintWriter out = response.getWriter();
		out.print(result); // ���ִ�н��
		out.flush();
		out.close();// �ر����������
	}

	/**
	 * ��ȡʡ�ݺ�ֱϽ��
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getProvince(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String result = "";
		CityMap cityMap = new CityMap();// ʵ��������ʡ����Ϣ��CityMap���ʵ��
		Map<String, String[]> map = cityMap.model;// ��ȡʡ����Ϣ���浽Map��
		Set<String> set = map.keySet(); // ��ȡMap�����еļ�������Set���Ϸ���
		Iterator it = set.iterator();
		while (it.hasNext()) { // ����ȡ��ʡ������Ϊһ���Զ��ŷָ����ַ���
			result = result + it.next() + ",";
		}
		result = result.substring(0, result.length() - 1); // ȥ�����һ������
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(result); // �����ȡ��ʡ���ַ���
		out.flush();
		out.close();// �ر����������
	}

	/**
	 * ��ȡ����
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getCity(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String result = "";
		String selProvince = request.getParameter("parProvince"); // ��ȡѡ���ʡ��
		selProvince=java.net.URLDecoder.decode(selProvince,"UTF-8");//�ַ�������
		Map<String, String[]> map = CityMap.model; // ��ȡʡ����Ϣ���浽Map��
		String[] arrCity = map.get(selProvince); // ��ȡָ������ֵ
		for (int i = 0; i < arrCity.length; i++) { // ����ȡ����������Ϊһ���Զ��ŷָ����ַ���
			result = result + arrCity[i] + ",";
		}
		result = result.substring(0, result.length() - 1); // ȥ�����һ������
		response.setContentType("text/html");// ����Ӧ����������
		PrintWriter out = response.getWriter();
		out.print(result); // �����ȡ�������ַ���
		out.flush();
		out.close();// �ر����������
	}

	/**
	 * �һ������һ��
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forgetPwd1(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username"); // ��ȡ�û���
		String question = userDao.forgetPwd1(username);// ִ���һ������һ����Ӧ�ķ�����ȡ������ʾ����
		PrintWriter out = response.getWriter();
		if ("".equals(question)) {// �ж�������ʾ�����Ƿ�Ϊ��
			out.println("<script>alert('��û������������ʾ���⣬�����һ����룡');history.back();</script>");
		} else if ("��������û��������ڣ�".equals(question)) {
			out.println("<script>alert('��������û��������ڣ�');history.back();</script>");
		} else {// ��ȡ������ʾ����ɹ�
			request.setAttribute("question", question);// ����������ʾ����
			request.setAttribute("username", username);// �����û���
			request.getRequestDispatcher("forgetPwd_2.jsp").forward(request,
					response);// �ض���ҳ��
		}
	}

	/**
	 * �һ�����ڶ���
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forgetPwd2(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username"); // ��ȡ�û���
		String question = request.getParameter("question");// ��ȡ������ʾ����
		String answer = request.getParameter("answer"); // ��ȡ��ʾ�����
		String pwd = userDao.forgetPwd2(username, question, answer);// ִ���һ�����ڶ����ķ����ж���ʾ������Ƿ���ȷ
		PrintWriter out = response.getWriter();

		if ("�������������ʾ����𰸴���".equals(pwd)) {// ��ʾ����𰸴���
			out.println("<script>alert('�������������ʾ����𰸴���');history.back();</script>");
		} else {// ��ʾ�������ȷ����������
			out.println("<script>alert('���������ǣ�\\r\\n"
					+ pwd
					+ "\\r\\n���μǣ�');window.location.href='DiaryServlet?action=listAllDiary';</script>");
		}
	}
}


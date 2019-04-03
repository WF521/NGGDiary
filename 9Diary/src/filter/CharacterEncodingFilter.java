package filter;

import java.io.IOException;
import javax.servlet.*;

public class CharacterEncodingFilter implements Filter {

	protected String encoding = null; // ��������ʽ����
	protected FilterConfig filterConfig = null; // ������������ö���

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig; // ��ʼ�����������ö���
		this.encoding = filterConfig.getInitParameter("encoding"); // ��ȡ�����ļ���ָ���ı����ʽ
	}

	// �������Ľӿڷ���������ִ�й���ҵ��
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (encoding != null) {
			request.setCharacterEncoding(encoding); // ��������ı���
			// ����Ӧ�������������ͣ����������ʽ��
			response.setContentType("text/html; charset=" + encoding);
		}
		chain.doFilter(request, response); // ���ݸ���һ��������
	}

	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

}

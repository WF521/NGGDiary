package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;
import tools.ConnDB;

public class UserDao {
	private ConnDB conn = null;

	public UserDao() {
		conn = new ConnDB();
	}

	// ��֤�û��ķ���������ֵΪ1��ʾ��¼�ɹ��������ʾ��¼ʧ��
	public int login(User user) {
		int flag = 0;
		String sql = "SELECT * FROM tb_user where userName='"
				+ user.getUsername() + "'";
		ResultSet rs = conn.executeQuery(sql);// ִ��SQL���
		try {
			if (rs.next()) {
				String pwd = user.getPwd();// ��ȡ����
				int uid = rs.getInt(1);// ��ȡ��һ�е�����
				if (pwd.equals(rs.getString(3))) {
					flag = uid;
					rs.last(); // ��λ�����һ����¼
					int rowSum = rs.getRow();// ��ȡ��¼����
					rs.first();// ��λ����һ����¼
					if (rowSum != 1) {
						flag = 0;
					}
				} else {
					flag = 0;
				}
			} else {
				flag = 0;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();// ����쳣��Ϣ
			flag = 0;
		} finally {
			conn.close();// �ر����ݿ�����
		}
		return flag;
	}

	/**
	 * ���ܣ�����û����Ƿ�Ϊ��
	 * 
	 * @param sql
	 * @return
	 */
	public String checkUser(String sql) {
		ResultSet rs = conn.executeQuery(sql); // ִ�в�ѯ���
		String result = "";
		try {
			if (rs.next()) {
				result = "�ܱ�Ǹ��[" + rs.getString(2) + "]�Ѿ���ע�ᣡ";
			} else {
				result = "1"; // ��ʾ�û�û�б�ע��
			}
		} catch (SQLException e) {
			e.printStackTrace(); // ����쳣��Ϣ
		} finally {
			conn.close(); // �ر����ݿ�����
		}
		return result; // �����жϽ��
	}

	/**
	 * ���ܣ������û�ע����Ϣ
	 * 
	 * @param sql
	 * @return
	 */
	public String save(String sql) {
		int rtn = conn.executeUpdate(sql); // ִ�и������
		String result = "";
		if (rtn > 0) {
			result = "�û�ע��ɹ���";
		} else {
			result = "�û�ע��ʧ�ܣ�";
		}
		conn.close(); // �ر����ݿ������
		return result; // ����ִ�н��
	}

	/**
	 * �һ������һ��
	 * 
	 * @param username
	 * @return
	 */
	public String forgetPwd1(String username) {
		String sql = "SELECT question FROM tb_user WHERE username='" + username
				+ "'";
		ResultSet rs = conn.executeQuery(sql);// ִ��SQL���
		String result = "";
		try {
			if (rs.next()) {
				result = rs.getString(1);// ��ȡ��һ�е�����
			} else {
				result = "��������û��������ڣ�"; // ��ʾ������û���������
			}
		} catch (SQLException e) {
			e.printStackTrace(); // ����쳣��Ϣ
			result = "��������û��������ڣ�"; // ��ʾ������û���������
		} finally {
			conn.close(); // �ر����ݿ�����
		}
		return result;
	}

	/**
	 * �һ�����ڶ���
	 * 
	 * @param username
	 * @return
	 */
	public String forgetPwd2(String username, String question, String answer) {
		String sql = "SELECT pwd FROM tb_user WHERE username='" + username
				+ "' AND question='" + question + "' AND answer='" + answer
				+ "'";
		ResultSet rs = conn.executeQuery(sql);// ִ��SQL���
		String result = "";
		try {
			if (rs.next()) {
				result = rs.getString(1);// ��ȡ��һ�е�����
			} else {
				result = "�������������ʾ����𰸴���"; // ��ʾ�����������ʾ����𰸴���
			}
		} catch (SQLException e) {
			e.printStackTrace();// ����쳣��Ϣ
		} finally {
			conn.close(); // �ر����ݿ�����
		}
		return result;
	}

	/**
	 * �����û�ID��ȡ�û�������Ϣ
	 * 
	 * @param id
	 *            - �û�ID
	 * @return
	 */
	public User getUserById(int id) {
		User user = new User();
		String sql = "select * from tb_user where id = " + id;
		ResultSet rs = conn.executeQuery(sql);// ִ��SQL���
		try {
			if (rs.next()) {// ������ڿɲ鵽������
				user.setId(id);// ��ȡIDֵ
				user.setUsername(rs.getString("username"));// ��ȡ�û���
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}
}


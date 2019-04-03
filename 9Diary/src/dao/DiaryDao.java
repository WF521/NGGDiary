package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.Comment;
import model.Diary;
import tools.ConnDB;

public class DiaryDao {
	private ConnDB conn = null;// �������ݿ����Ӷ���

	public DiaryDao() {
		conn = new ConnDB();// ʵ�������ݿ����Ӷ���
	}

	/**
	 * ��ѯ�ռ�
	 * 
	 * @param sql
	 * @return
	 */
	public List<Diary> queryDiary(String sql) {
		ResultSet rs = conn.executeQuery(sql);// ִ�в�ѯ���
		List<Diary> list = new ArrayList<Diary>();
		try {// �����쳣
			while (rs.next()) {
				Diary diary = new Diary();
				diary.setId(rs.getInt(1));// ��ȡ������ID
				diary.setTitle(rs.getString(2));// ��ȡ�������ռǱ���
				diary.setAddress(rs.getString(3));// ��ȡ������ͼƬ��ַ
				Date date;
				try {
					date = DateFormat.getDateTimeInstance().parse(
							rs.getString(4));
					diary.setWriteTime(date);// ����д�ռǵ�ʱ��
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();// ����쳣��Ϣ������̨
				}

				diary.setUserid(rs.getInt(5));// ��ȡ�������û�ID
				diary.setUsername(rs.getString(6));// ��ȡ�������û���
				diary.setComments(getComments(diary.getId()));// ��ȡ����
				diary.setLikes(getLikesCount(diary.getId()));// ��ȡ������
				list.add(diary);// ���ռ���Ϣ���浽list������

			}
		} catch (SQLException e) {
			e.printStackTrace();// ����쳣��Ϣ
		} finally {
			conn.close();// �ر����ݿ�����
		}
		return list;
	}

	/**
	 * ���ܣ�����Ź����ռǵ����ݿ�
	 * 
	 * @param diary
	 * @return
	 */
	public int saveDiary(Diary diary) {
		String sql = "INSERT INTO tb_diary (title,address,userid) VALUES('"
				+ diary.getTitle() + "','" + diary.getAddress() + "',"
				+ diary.getUserid() + ")"; // �������ݵ�SQL���
		int ret = conn.executeUpdate(sql);// ִ�и������
		conn.close();// �ر����ݿ�����
		return ret;
	}

	/**
	 * ɾ��ָ���ռ�
	 * 
	 * @param id
	 * @return
	 */
	public int delDiary(int id) {
		String sql = "DELETE FROM tb_diary WHERE id=" + id;
		int ret = 0;
		try {
			ret = conn.executeUpdate(sql);// ִ�и������
		} catch (Exception e) {
			e.printStackTrace();// ����쳣��Ϣ
		} finally {
			conn.close();// �ر���������
		}
		return ret;
	}

	/**
	 * ��ȡ����
	 * 
	 * @param diaryId
	 *            - ��־���
	 * @return ���ۼ���
	 */
	public List<Comment> getComments(int diaryId) {
		List<Comment> comments = new LinkedList<Comment>();
		String sql = "select c.id,u.username,c.content,c.create_time "
				+ "from tb_comments c,tb_user u "
				+ "where valid = 'Y' and c.from_user_id = u.id and c.diary_id ="
				+ diaryId;
		ResultSet rs = conn.executeQuery(sql);
		try {
			while (rs.next()) {
				Comment c = new Comment();
				c.setId(rs.getInt("id"));
				c.setFromUserName(rs.getString("username"));
				c.setContent(rs.getString("content"));
				String date = rs.getString("create_time");
				c.setCreate_time(date);
				comments.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return comments;
	}

	/**
	 * �������
	 * 
	 * @param diaryId
	 *            - ��־ID
	 * @param userId
	 *            - ������ID
	 * @param content
	 *            - ��������
	 * @return �Ƿ����ɹ�
	 */
	public boolean addComments(int diaryId, String fromUserName, String content) {
		boolean success = false;// �����Ƿ�ɹ�
		String sql = "insert into tb_comments(diary_id,from_user_id,content,create_time,valid) select d.id,u.id,'"
				+ content
				+ "',current_timestamp,'Y'  from tb_user u ,tb_diary d where d.id="
				+ diaryId + " and u.username = '" + fromUserName + "' ";
		int result = conn.executeUpdate(sql);// ������û����ۼ�¼
		if (result > 0) {
			success = true;
		}
		return success;
	}

	/**
	 * ��ȡ������
	 * 
	 * @param diaryId
	 *            ��־ID
	 * @return ������
	 */
	public int getLikesCount(int diaryId) {
		int count = 0;
		String sql = "select count(*) from tb_likes where diary_id =  "
				+ diaryId;
		ResultSet rs = conn.executeQuery(sql);// ִ�в�ѯ���
		try {
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return count;
	}

	/**
	 * ���µ�����
	 * 
	 * @param diaryId
	 *            - ��־ID
	 * @param fromUserID
	 *            - �����û�ID
	 * @return �Ƿ�ɹ�����
	 */
	public boolean updateLikesCount(int diaryId, String fromUserIDName) {
		// insert into tb_likes(diary_id,from_user_id) select d.id,u.id from
		// tb_user u,tb_diary d where u.username='h' and d.id=16;
		boolean success = false;// �����Ƿ�ɹ�
		ResultSet rs = conn
				.executeQuery("select count(l.id) from tb_likes l ,tb_user u where u.id = l.from_user_id and u.username='"
						+ fromUserIDName + "' and l.diary_id = " + diaryId);// ��ѯ�û��Ƿ��ѵ����
		try {
			rs.next();// �����ָ������
			int count = rs.getInt(1);// ��ȡ��ѯ���
			if (count == 0) {// ������û�û�����
				String sql = "insert into tb_likes(diary_id,from_user_id) select d.id,u.id from tb_user u,tb_diary d where u.username='"
						+ fromUserIDName + "' and d.id=" + diaryId;
				int result = conn.executeUpdate(sql);// ������û����޼�¼
				if (result > 0) {// ��������¼�ɹ�
					success = true;// ���³ɹ�
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return success;
	}

	public static void main(String[] args) {
		DiaryDao d = new DiaryDao();
		boolean a = d.updateLikesCount(15, "wgh");
		System.out.println(a);
	}
}

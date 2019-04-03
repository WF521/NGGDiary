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
	private ConnDB conn = null;// 创建数据库连接对象

	public DiaryDao() {
		conn = new ConnDB();// 实例化数据库连接对象
	}

	/**
	 * 查询日记
	 * 
	 * @param sql
	 * @return
	 */
	public List<Diary> queryDiary(String sql) {
		ResultSet rs = conn.executeQuery(sql);// 执行查询语句
		List<Diary> list = new ArrayList<Diary>();
		try {// 捕获异常
			while (rs.next()) {
				Diary diary = new Diary();
				diary.setId(rs.getInt(1));// 获取并设置ID
				diary.setTitle(rs.getString(2));// 获取并设置日记标题
				diary.setAddress(rs.getString(3));// 获取并设置图片地址
				Date date;
				try {
					date = DateFormat.getDateTimeInstance().parse(
							rs.getString(4));
					diary.setWriteTime(date);// 设置写日记的时间
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();// 输出异常信息到控制台
				}

				diary.setUserid(rs.getInt(5));// 获取并设置用户ID
				diary.setUsername(rs.getString(6));// 获取并设置用户名
				diary.setComments(getComments(diary.getId()));// 获取评论
				diary.setLikes(getLikesCount(diary.getId()));// 获取点赞数
				list.add(diary);// 将日记信息保存到list集合中

			}
		} catch (SQLException e) {
			e.printStackTrace();// 输出异常信息
		} finally {
			conn.close();// 关闭数据库连接
		}
		return list;
	}

	/**
	 * 功能：保存九宫格日记到数据库
	 * 
	 * @param diary
	 * @return
	 */
	public int saveDiary(Diary diary) {
		String sql = "INSERT INTO tb_diary (title,address,userid) VALUES('"
				+ diary.getTitle() + "','" + diary.getAddress() + "',"
				+ diary.getUserid() + ")"; // 保存数据的SQL语句
		int ret = conn.executeUpdate(sql);// 执行更新语句
		conn.close();// 关闭数据库连接
		return ret;
	}

	/**
	 * 删除指定日记
	 * 
	 * @param id
	 * @return
	 */
	public int delDiary(int id) {
		String sql = "DELETE FROM tb_diary WHERE id=" + id;
		int ret = 0;
		try {
			ret = conn.executeUpdate(sql);// 执行更新语句
		} catch (Exception e) {
			e.printStackTrace();// 输出异常信息
		} finally {
			conn.close();// 关闭数据连接
		}
		return ret;
	}

	/**
	 * 获取评论
	 * 
	 * @param diaryId
	 *            - 日志编号
	 * @return 评论集合
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
	 * 添加评论
	 * 
	 * @param diaryId
	 *            - 日志ID
	 * @param userId
	 *            - 评论人ID
	 * @param content
	 *            - 评论内容
	 * @return 是否插入成功
	 */
	public boolean addComments(int diaryId, String fromUserName, String content) {
		boolean success = false;// 更新是否成功
		String sql = "insert into tb_comments(diary_id,from_user_id,content,create_time,valid) select d.id,u.id,'"
				+ content
				+ "',current_timestamp,'Y'  from tb_user u ,tb_diary d where d.id="
				+ diaryId + " and u.username = '" + fromUserName + "' ";
		int result = conn.executeUpdate(sql);// 插入此用户评论记录
		if (result > 0) {
			success = true;
		}
		return success;
	}

	/**
	 * 获取点赞数
	 * 
	 * @param diaryId
	 *            日志ID
	 * @return 点赞数
	 */
	public int getLikesCount(int diaryId) {
		int count = 0;
		String sql = "select count(*) from tb_likes where diary_id =  "
				+ diaryId;
		ResultSet rs = conn.executeQuery(sql);// 执行查询语句
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
	 * 更新点赞数
	 * 
	 * @param diaryId
	 *            - 日志ID
	 * @param fromUserID
	 *            - 点赞用户ID
	 * @return 是否成功更新
	 */
	public boolean updateLikesCount(int diaryId, String fromUserIDName) {
		// insert into tb_likes(diary_id,from_user_id) select d.id,u.id from
		// tb_user u,tb_diary d where u.username='h' and d.id=16;
		boolean success = false;// 更新是否成功
		ResultSet rs = conn
				.executeQuery("select count(l.id) from tb_likes l ,tb_user u where u.id = l.from_user_id and u.username='"
						+ fromUserIDName + "' and l.diary_id = " + diaryId);// 查询用户是否已点过赞
		try {
			rs.next();// 结果集指针下移
			int count = rs.getInt(1);// 获取查询结果
			if (count == 0) {// 如果此用户没点过赞
				String sql = "insert into tb_likes(diary_id,from_user_id) select d.id,u.id from tb_user u,tb_diary d where u.username='"
						+ fromUserIDName + "' and d.id=" + diaryId;
				int result = conn.executeUpdate(sql);// 插入此用户点赞记录
				if (result > 0) {// 如果插入记录成功
					success = true;// 更新成功
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

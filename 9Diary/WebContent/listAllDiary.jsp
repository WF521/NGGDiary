<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String username = (String) session.getAttribute("userName");
%>
<jsp:useBean id="pagination" class="tools.MyPagination"
	scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="JS/excanvas-modified.js"></script>
<title>显示九宫格日记列表</title>
<script language="javascript">

//点击评论连接,传入展开的区域id,日志id，评论区id
function showComment(divID,diaryID,commentDivID){//展开评论区
	var hidDiv = document.getElementById(divID);//获取评论下的隐藏区域
	if(hidDiv.style.display == 'none'){//如果是隐藏的
		hidDiv.style.display = 'block';//取消隐藏
		//发送给ajax的参数
		var param="diaryID="+diaryID+"&commentDivID="+commentDivID;
		var loader=new net.AjaxRequest("DiaryServlet?action=getComment",deal_getComments,onerror,"POST",encodeURI(param));
	}else{
		hidDiv.style.display = 'none';//隐藏
	}
}

function onerror(){
	alert('Ajax无法与后台取得联系');
}

function deal_getComments(){//获取评论数据
	var h=this.req.responseText;//获取后台返回的结果
	h=h.replace(/\s/g,"");	//去除字符串中的Unicode空白
	var str =new Array();//创建数组对象
	str = h.split("<分隔符>");//使用分隔符分割后台返回的结果
	//获取评论记录区
	var commentDiv = document.getElementById(str[0]);
	commentDiv.innerHTML = str[1];//评论更新
}


function likeClick(diaryID,likeDivID){//点赞动作
	var userExist = ${empty sessionScope.userName};//判断session中用户名是否为空
	if(userExist){//如果是空用户名
		alert('请登录！');		
	}else{//如果不为空，则触发ajax向后台发送点赞请求
		//发送给ajax的参数
		var param="diaryID="+diaryID+"&username=<%=username%>&likeDivID="+likeDivID;
		var loader=new net.AjaxRequest("DiaryServlet?action=clickLike",deal_click,onerror,"POST",encodeURI(param));
	}
}

function deal_click(){//处理点赞请求
	var h=this.req.responseText;//获取后台返回的结果
	h=h.replace(/\s/g,"");	//去除字符串中的Unicode空白
	var str =new Array();//创建数组
	str = h.split("<分隔符>");//按照指定内容分割字符串
	if(str[1]=='OK'){//如果点赞成功
		var likeDiv = document.getElementById(str[0]);//获取点赞区
		likeDiv.innerHTML = str[2];	//点赞数量更新
	}else{
		alert("您已经点过赞了");
	}
}

//发送留言
function postComment(diaryID,commentDivID,textareaID,commentsCountID){
	var userExist = ${empty sessionScope.userName};//判断session中用户名是否为空
	if(userExist){//如果是空用户名
		alert('请登录！');		
	}else{//如果不为空，则触发ajax向后台发送点赞请求
		var content = document.getElementById(textareaID).value;
		var param="diaryID="+diaryID+"&username=<%=username%>&commentDivID="+commentDivID+"&content="+content+"&textareaID="+textareaID+"&commentsCountID="+commentsCountID;
		var loader=new net.AjaxRequest("DiaryServlet?action=postComment",deal_postComment,onerror,"POST",encodeURI(param));
	}
}

function deal_postComment(){//处理发送留言数据
	var h=this.req.responseText;//获取后台返回的结果
	h=h.replace(/\s/g,"");	//去除字符串中的Unicode空白
	var str =new Array();//创建数组
	str = h.split("<分隔符>");//按照指定内容分割字符串
	if(str[0] == "error"){
		alert("无法留言");
	}else{
		//获取留言记录区
		var commentDiv = document.getElementById(str[0]);
		commentDiv.innerHTML = str[1];//留言记录更新
		//获取留言文本域
		var textarea = document.getElementById(str[2]);
		textarea.value = "";//文本域清空
		//获取留言数量提示区
		var commentsCountDiv = document.getElementById(str[3]);
		commentsCountDiv.innerHTML = str[4];//留言数量更新
	}
}


//展开或收缩图片的方法
function zoom(id,url){
	document.getElementById('reDefault'+id).onclick();//执行reDefault的单击事件
	document.getElementById("diary"+id).style.display = "";//显示图片
	if(flag[id]){//用于展开图片
		document.getElementById("diary"+id).src="images/diary/"+url+".png";	//设置要显示的图片
		document.getElementById("diary"+id).style.cursor="url(images/ico02.ico)";//为图片添加自定义鼠标样式
		document.getElementById("control"+id).style.display="";	//显示控制工具栏
		document.getElementById("diaryImg"+id).style.width=401;	//设置日记图片的宽度
		document.getElementById("diaryImg"+id).style.height=436;	//设置日记图片的高度
		document.getElementById("canvas"+id).style.cursor="url(images/ico02.ico)";	//为画布添加自定义鼠标样式
		document.getElementById("diary"+id).width=400;	//设置图片的宽度
		document.getElementById("diary"+id).height=400;//设置图片的高度
		flag[id]=false;
	}else{//用于收缩图片
		document.getElementById("diary"+id).src="images/diary/"+url+"scale.jpg";//设置图片显示为缩略图
		document.getElementById("control"+id).style.display="none";	//设置控制工具栏不显示
		document.getElementById("diary"+id).style.cursor="url(images/ico01.ico)";//为图片添加自定义鼠标样式
		document.getElementById("diaryImg"+id).style.width=60;	//设置日记图片的宽度
		document.getElementById("diaryImg"+id).style.height=60;	//设置日记图片的高度		
		document.getElementById("canvas"+id).style.cursor="url(images/ico01.ico)";	//为画布添加自定义鼠标样式
		document.getElementById("diary"+id).width=60;	//设置图片的宽度
		document.getElementById("diary"+id).height=60;//设置图片的高度
		flag[id]=true;
		document.getElementById("canvas"+id).style.display="none";	//设置面板不显示
		
	}
}
var i=0;	//标记变量，用于记录当前页共几条日记



</script>
</head>
<body bgcolor="#F0F0F0">
	<div id="box">
		<%@ include file="top.jsp"%>
		<%@ include file="register.jsp"%>
		<c:if test="${!empty requestScope.diaryList}">
			<c:forEach items="${requestScope.diaryList}" var="diaryList"
				varStatus="id">
				<script type="text/javascript">
i++;	//标记变量，用于记录当前页共几条日记
function rotate${id.count }(){
		var param${id.count } = {
			right: document.getElementById("rotRight${id.count }"),
			left: document.getElementById("rotLeft${id.count }"),
			reDefault: document.getElementById("reDefault${id.count }"),			
			img: document.getElementById("diary${id.count }"),
			cv: document.getElementById("canvas${id.count }"),
			rot: 0
		};
		var rotate = function(canvas,img,rot){
			//获取图片的高宽
			var w = 400;						//设置图片的宽度
			var h = 400;						//设置图片的高度
			//角度转为弧度
			if(!rot){
				rot = 0;	
			}
			var rotation = Math.PI * rot / 180;
			var c = Math.round(Math.cos(rotation) * 1000) / 1000;
			var s = Math.round(Math.sin(rotation) * 1000) / 1000;
			//旋转后canvas面板的大小
			canvas.height = Math.abs(c*h) + Math.abs(s*w);
			canvas.width = Math.abs(c*w) + Math.abs(s*h);
			//绘图开始
			var context = canvas.getContext("2d");
			
			context.save();
			//改变中心点
			if (rotation <= Math.PI/2) {	//旋转角度小于等90度时
				context.translate(s*h,0);
			} else if (rotation <= Math.PI) {	//旋转角度小于等180度时
				context.translate(canvas.width,-c*h);
			} else if (rotation <= 1.5*Math.PI) {	//旋转角度小于等270度时
				context.translate(-c*w,canvas.height);
			} else {
				rot=0;
				context.translate(0,-s*w);
			}
			//旋转90°
			context.rotate(rotation);
			//绘制
			context.drawImage(img, 0, 0, w, h);
			context.restore();
			img.style.display = "none";	//设置图片不显示
		}
		var fun = {
			right: function(){//向右转的方法
				param${id.count }.rot += 90;
				rotate(param${id.count }.cv, param${id.count }.img, param${id.count }.rot);
				if(param${id.count }.rot === 270){
					param${id.count }.rot = -90;	
				}else if(param${id.count }.rot > 270){
					param${id.count }.rot = -90;	
					fun.right();//调用向右转的方法
				}	
			},

			reDefault: function(){//恢复默认的方法
				param${id.count }.rot = 0;	
				rotate(param${id.count }.cv, param${id.count }.img, param${id.count }.rot);	
			},

			left: function(){//向左转的方法
				param${id.count }.rot -= 90;
				if(param${id.count }.rot <= -90){
					param${id.count }.rot = 270;
				}
				rotate(param${id.count }.cv, param${id.count }.img, param${id.count }.rot);	//旋转指定角度		
			}
		};
		param${id.count }.right.onclick = function(){	//向右转
			param${id.count }.cv.style.display="";//显示画图面板
			fun.right();
			return false;
		};
		param${id.count }.left.onclick = function(){		//向左转
			param${id.count }.cv.style.display="";//显示画图面板
			fun.left();
			return false;
		};
		param${id.count }.reDefault.onclick = function(){//恢复默认
			fun.reDefault();	//恢复默认
			return false;
		};		
}
</script>
				<div
					style="border-bottom-color: #CBCBCB; padding: 5px; border-bottom-style: dashed; border-bottom-width: 1px; margin: 10px 20px; color: #0F6548">
					<font color="#CE6A1F" style="font-weight: bold; font-size: 14px;">${diaryList.username}</font>&nbsp;&nbsp;发表九宫格日记：<b>${diaryList.title}</b>
				</div>
				<div
					style="margin: 10px 10px 0px 10px; background-color: #FFFFFF; border-bottom-color: #CBCBCB; border-bottom-style: dashed; border-bottom-width: 1px;">
					<div id="diaryImg${id.count }"
						style="border: 1px #DDDDDD solid; width: 60px; background-color: #EEEEEE;">

						<div id="control${id.count }"
							style="display: none; padding: 10px;">
							<%
								String url = request.getRequestURL().toString();
										url = url.substring(0, url.lastIndexOf("/"));
							%>
							<a href="#"
								onClick="zoom('${id.count }','${diaryList.address }')">收缩</a>
							&nbsp;&nbsp; <a
								href="<%=url %>/images/diary/${diaryList.address }.png"
								target="_blank">原图</a> &nbsp;&nbsp;<a id="rotLeft${id.count }"
								href="#">左转</a> <a id="rotRight${id.count }"
								href="#">右转</a> <a id="reDefault${id.count }" href="#"
								style="display: none">恢复默认</a>
						</div>
						<img id="diary${id.count }"
							src="images/diary/${diaryList.address }scale.jpg"
							style="cursor: url(images/ico01.ico);"
							onClick="zoom('${id.count }','${diaryList.address }')">
						<canvas id="canvas${id.count }" style="display:none;"
							onClick="zoom('${id.count }','${diaryList.address }')"></canvas>
					</div>
					<div name="location${id.count }">
						<a href="#location${id.count }"
							onclick="likeClick('${diaryList.id }','like${diaryList.id }')"><font
							id="like${diaryList.id }" color="#8E8E8E">赞(${diaryList.likes})</font></a>
						<a href="#location${id.count }"
							onclick="showComment('hidden${id.count }','${diaryList.id }','comments${id.count }')">
							<font id="commentsCount${diaryList.id }" color='#8E8E8E'>评论(${fn:length(diaryList.comments)})</font>
						</a>
					</div>
					<div id="hidden${id.count }"
						style="display: none; border-top-color: #CBCBCB; border-top-style: dashed; border-top-width: 1px;">
						<div id="comments${id.count }"></div>
						<textarea id="textarea${diaryList.id }" rows="2" cols="40"></textarea>
						<br> <input type="button" value="留言"
							onclick="postComment('${diaryList.id }','comments${id.count }','textarea${diaryList.id }','commentsCount${diaryList.id }')" />
					</div>
					<div
						style="padding: 10px; background-color: #FFFFFF; text-align: right; color: #999999;">
						发表时间：
						<fmt:formatDate value="${diaryList.writeTime}" type="both"
							pattern="yyyy-MM-dd HH:mm:ss" />
						<c:if test="${sessionScope.userName==diaryList.username}">
							<a
								href="DiaryServlet?action=delDiary&id=${diaryList.id }&url=${requestScope.url}&imgName=${diaryList.address }">[删除]</a>
						</c:if>
					</div>
				</div>
			</c:forEach>
		</c:if>
		<c:if test="${empty requestScope.diaryList}">
暂无九宫格日记！
</c:if>
		<script type="text/javascript">
var flag=new Array(i);//定义一个标记数组
window.onload = function(){
	while(i>0){
		eval("rotate"+i)();//执行旋转图片的方法
		flag[i]=true;//初始化一维数组的各个元素
		i--;
	}
}
</script>
		<div style="background-color: #FFFFFF;">
			<%=pagination.printCtrl(
					Integer.parseInt(request.getAttribute("Page").toString()),
					"DiaryServlet?action=" + request.getAttribute("url"), "")%>
		</div>
		<%@ include file="bottom.jsp"%>
	</div>
</body>
</html>
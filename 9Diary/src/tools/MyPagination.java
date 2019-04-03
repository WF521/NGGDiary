package tools;

import java.util.ArrayList;
import java.util.List;

import model.Diary;



public class MyPagination {
	public List<Diary> list=null;
	private int recordCount=0;	//��¼��
	private int pagesize=0;		//ÿҳ��ʾ�ļ�¼��
	private int maxPage=0;		//���ҳ��

	//��ʼ����ҳ��Ϣ
	public List<Diary> getInitPage(List<Diary> list,int Page,int pagesize){
		List<Diary> newList=new ArrayList<Diary>();
		this.list=list;
		recordCount=list.size();	//��ȡlist���ϵ�Ԫ�ظ���
		this.pagesize=pagesize;
		this.maxPage=getMaxPage();	//��ȡ���ҳ��
		try{
		for(int i=(Page-1)*pagesize;i<=Page*pagesize-1;i++){
			try{
				if(i>=recordCount){break;}
			}catch(Exception e){}
				newList.add((Diary)list.get(i));
		}
		}catch(Exception e){
			e.printStackTrace();//����쳣��Ϣ
		}
		return newList;
	}
	//��ȡָ��ҳ������
	public List<Diary> getAppointPage(int Page){
		List<Diary> newList=new ArrayList<Diary>();
		try{
			for(int i=(Page-1)*pagesize;i<=Page*pagesize-1;i++){
				try{
					if(i>=recordCount){break;}
				}catch(Exception e){}
					newList.add((Diary)list.get(i));
			}
			}catch(Exception e){
				e.printStackTrace();//����쳣��Ϣ
			}
			return newList;
	}
	//��ȡ����¼��
	public int getMaxPage(){
		int maxPage=(recordCount%pagesize==0)?(recordCount/pagesize):(recordCount/pagesize+1);
		return maxPage;
	}
	//��ȡ�ܼ�¼��
	public int getRecordSize(){
		return recordCount;
	}
	
	//��ȡ��ǰҳ��
	public int getPage(String str){
		if(str==null){//��ҳ������nullʱ���������0
			str="0";
		}
		int Page=Integer.parseInt(str);
		if(Page<1){//��ҳ��С��1ʱ���������1
			Page=1;
		}else{
			if(((Page-1)*pagesize+1)>recordCount){//��ҳ���������ҳ��ʱ������������ҳ��
				Page=maxPage;
			}
		}
		return Page;
	}

	/**
	 * ��ҳ���������ҳ����
	 * @param Page
	 * @param url
	 * @param para
	 * @return
	 */
	public String printCtrl(int Page,String url,String para){
		String strHtml="<table width='100%'  border='0' cellspacing='0' cellpadding='0'><tr> <td height='24' align='right'>��ǰҳ������"+Page+"/"+maxPage+"��&nbsp;";
		try{
		if(Page>1){
			strHtml=strHtml+"<a href='"+url+"&Page=1"+para+"'>��һҳ</a>��";
			strHtml=strHtml+"<a href='"+url+"&Page="+(Page-1)+para+"'>��һҳ</a>";
		}
		if(Page<maxPage){
			strHtml=strHtml+"<a href='"+url+"&Page="+(Page+1)+para+"'>��һҳ</a>��<a href='"+url+"&Page="+maxPage+para+"'>���һҳ&nbsp;</a>";
		}
		strHtml=strHtml+"</td> </tr>	</table>";
		}catch(Exception e){
			e.printStackTrace();
		}
		return strHtml;
	}	
}


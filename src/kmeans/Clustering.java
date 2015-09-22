package kmeans;

import java.util.Hashtable;
import java.sql.*;


public class Clustering 
{	
	Connection con=null;
	Statement st=null;
	ResultSet rs=null;
	
	int i,j,index1=0,index2=0,index3=0,flag=0,status=0,jobIndex=0,jobId;
	double x1,y1,c1Result,c2Result,c3Result,temp1=0,temp2=0;
	String requirement;
	String[] skills=new String[100];
	String[] cluster1=new String[50]; //Procedural
	String[] cluster2=new String[50]; //Web Technology
	String[] cluster3=new String[50]; //OOPS
	String[] jobs=new String[20];
	
	class HashStructure  //define the hash table with columns x and y
	{
		double x;
		double y;
		HashStructure(double x1,double y1)
		{
			this.x=x1;
			this.y=y1;
		}
	}
	HashStructure hs=new HashStructure(0,0);
	
	Hashtable<Object,HashStructure> h=new Hashtable<Object,HashStructure>(); 


public String[] kmeans(String skillSet)
{
	try
	{
		skills=skillSet.split(",");
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + "C://Users/nandhu/Documents/SkillMatchingDB.accdb";
		con = DriverManager.getConnection(url);
		st=con.createStatement();
		
		for(i=0;i<skills.length;i++)
		{
			String sql = "SELECT * FROM Vector WHERE Skill='" + skills[i] + "'";
			rs = st.executeQuery(sql);
			while(rs.next())
			{   
				x1  = rs.getFloat("VectorX");
				y1  = rs.getFloat("VectorY");    
				hs=new HashStructure(x1,y1);
				h.put(skills[i], hs);
			}					
		}
		
		hs=new HashStructure(2,10);
		h.put("C1", hs);
		hs=new HashStructure(5,8);
		h.put("C2", hs);
		hs=new HashStructure(1,2);
		h.put("C3", hs);
		
	while(flag==0)
	{
		status=0;
		for(int index=0;index<index1;index++)
			cluster1[index]=null;
		for(int index=0;index<index2;index++)
			cluster2[index]=null;
		for(int index=0;index<index3;index++)
			cluster3[index]=null;
		
		index1=0;
		index2=0;
		index3=0;
		for(i=0;i<skills.length;i++)
		{
				hs=h.get(skills[i]);
				HashStructure hash1=h.get("C1");
				c1Result=distance(hs.x,hs.y,hash1.x,hash1.y);
				HashStructure hash2=h.get("C2");
				c2Result=distance(hs.x,hs.y,hash2.x,hash2.y);
				HashStructure hash3=h.get("C3");
				c3Result=distance(hs.x,hs.y,hash3.x,hash3.y);
	
				if(c1Result<c2Result&& c1Result<c3Result) //Cluster 1
				{
					cluster1[index1]=skills[i];
					index1++;
				}	
				else if(c2Result<c1Result&& c2Result<c3Result) //Cluster 2
				{
					cluster2[index2]=skills[i];
					index2++;
				}	
				else //Cluster 3
				{
					cluster3[index3]=skills[i];
					index3++;
				}
		}
		System.out.println("................................................");
		displayCluster();
		
		if(cluster1[0]!=null)
		computeMean(index1,"C1",cluster1); //recompute mean for cluster1
		if(cluster2[0]!=null)
		computeMean(index2,"C2",cluster2); //recompute mean for cluster2
		if(cluster3[0]!=null)		
		computeMean(index3,"C3",cluster3); //recompute mean for cluster3
		if(status>0)
			flag=0;
		else if(status==0)
			flag=1;
	}
		//compare the user skills with job requirements
		
		if(cluster1[0]!=null) //user knows Procedural languages
		{
			String sql = "SELECT * FROM Job WHERE Requirement='Procedural'";
			rs = st.executeQuery(sql);
			while(rs.next())
			{   
				jobId  = rs.getInt("Job_Id");
				requirement=rs.getString("Requirement");
				jobs[jobIndex]="Requirement="+requirement+" Job id="+jobId;
				jobIndex++;
			}
		}
		if(cluster2[0]!=null) ////user knows Web Technology
		{
			String sql = "SELECT * FROM Job WHERE Requirement='Web'";
			rs = st.executeQuery(sql);
			while(rs.next())
			{   
				jobId  = rs.getInt("Job_Id");    
				requirement=rs.getString("Requirement");
				jobs[jobIndex]="Requirement="+requirement+" Job id="+jobId;
				jobIndex++;
			}
		}
		if(cluster3[0]!=null) ////user knows OOPS
		{
			String sql = "SELECT * FROM Job WHERE Requirement='OOPS'";
			rs = st.executeQuery(sql);
			while(rs.next())
			{   
				jobId  = rs.getInt("Job_Id");    
				requirement=rs.getString("Requirement");
				jobs[jobIndex]="Requirement="+requirement+" Job id="+jobId;
				jobIndex++;
			}	
		}
		rs.close();
		con.close();
	}
	catch(Exception e)
	{
	
	}
	return jobs; 
}

public double distance(double x1,double y1,double x2,double y2)
{
	double result=Math.abs(x2-x1)+Math.abs(y2-y1);
	return result;
}
public void computeMean(int index,String clusterName,String[] cluster)
{
		for(int k=0;k<index;k++)
		{
			hs=h.get(cluster[k]);
			temp1=hs.x+temp1;
			temp2=hs.y+temp2;
		}
		temp1=temp1/index;
		temp2=temp2/index;
		//check if cluster mean value has changed 
		HashStructure hs=h.get(clusterName);
		if(hs.x!=temp1 || hs.y!=temp2) //update mean value
		{
			HashStructure hash=new HashStructure(temp1,temp2);
			h.put(clusterName, hash);	
			status++; //changed
		}
		temp1=0;
		temp2=0;
}
public void displayCluster()
{
	System.out.println("Procedural");
	for(i=0;i<skills.length;i++)
	{
		if(cluster1[i]!=null)
		System.out.println("cluster1["+i+"]="+cluster1[i]);
	}
	System.out.println("Web Technology");
	for(i=0;i<skills.length;i++)
	{
		if(cluster2[i]!=null)
		System.out.println("cluster2["+i+"]="+cluster2[i]);
	}
	System.out.println("OOPS");
	for(i=0;i<skills.length;i++)
	{
		if(cluster3[i]!=null)
		System.out.println("cluster3["+i+"]="+cluster3[i]);
	}
}
public static void main(String args[])
{
	String[] jobs=new String[20];
	Clustering c=new Clustering();
	jobs=c.kmeans("HTML,css,java");
	for(int i=0;i<c.jobIndex;i++)
		System.out.println(jobs[i]);
}
}


package com.lucene.db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Qrels {
	public int qid;
	public int docno;
	public String relevance;
	public Qrels(int qid, int docno, String relevance) {
		super();
		this.qid = qid;
		this.docno = docno;
		this.relevance = relevance;
	}
	@Override
	public String toString() {
		String qid=String.valueOf(this.qid);
		String docno=String.valueOf(this.docno);
		return qid+"\t"+"0\t"+this.docno+"\t"+this.relevance;
	}
	

	public static void main(String[] args)
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			/** Assuming database solr_test exists */
			String url="jdbc:mysql://localhost:3306/"+"lucene";
			Connection conn = DriverManager.getConnection(url, "root","");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM qrels");
			BufferedWriter bw=null;
			 try {
				 bw=new BufferedWriter(new FileWriter("qrel.qrel",true));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 while(rs.next())
			 {
				 Qrels qr=new Qrels(rs.getInt("QueryID"),rs.getInt("docno"),rs.getString("rel"));
				
				 
				 try {
				    	
				    	String val=qr.toString()+"\n";
				    	System.out.println(val);
				    	bw.write(val);
				    	}
				    	catch(IOException ex)
				    	{
				    		System.out.println("Dosya Açýlýrken hata oluþtu");
				    	}
				    	
			 }
			 if(bw!=null)
	    		{
	    			
				 try {
					 bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		}
			 
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package com.lucene.db;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tr.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.analysis.standard.*;
public class Index {
	
	public static void main(String[] args) throws SQLException
	{
		
		Index index=new Index();
		Statement stmt=null;
		ResultSet rs=null;
		String sql="SELECT docno,headline,text from documents";
		
		try {
			 stmt=index.Db_Connect("lucene","root","");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			rs=index.Db_Read(stmt, sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//Burada indexleme
		Directory dir=null;
		 Analyzer analyzer=null;
		   IndexWriterConfig iwc=null;
		   IndexWriter writer=null;
		 String indexPath = "E:\\lucene_db_index";
		 try {
			  Date start=new Date();
			 dir = FSDirectory.open(Paths.get(indexPath));
			  analyzer =  new TurkishAnalyzer();
		     iwc = new IndexWriterConfig(analyzer);
		     iwc.setOpenMode(OpenMode.CREATE);
		     writer = new IndexWriter(dir, iwc);
		    index.Index_db_Docs(rs,writer);
		    writer.close();
		  
		    Date end = new Date();
		      System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		     
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		
		
		
	}
	
	
	
	
	public ResultSet Db_Read(Statement stmt,String sql) throws SQLException
	{   
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
		
	}
	
	public Statement Db_Connect(String dbname,String username,String pass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		/** Assuming database solr_test exists */
		String url="jdbc:mysql://localhost:3306/"+dbname;
		Connection conn = DriverManager.getConnection(url, username, pass);
		Statement stmt = conn.createStatement();
		return stmt;
	}
	
	public static void Index_db_Docs(ResultSet rs,IndexWriter writer) throws SQLException, IOException
	{
		Document doc=null;
		FieldType stringType = new FieldType();
		stringType.setTokenized(true);
		
		stringType.setIndexOptions(IndexOptions.NONE);
		while(rs.next())
		{
			 doc = new Document();
			 String docno_val=String.valueOf(rs.getInt("docno"));
		
			 doc.add(new TextField("docno", docno_val, Store.YES));
			 doc.add(new TextField("headline", rs.getString("headline"), Store.YES));
			 doc.add(new TextField("text", rs.getString("text"), Store.YES));
			
			 writer.addDocument(doc);
			 
			 
		}
		

	
		 
	}

}

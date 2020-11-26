package com.lucene.db;
import java.io.BufferedReader;
import java.io.FileWriter;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
public class Search {
	
	public static void main(String[] args) throws Exception
	{
		String indexPath = "E:\\lucene_db_index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
	
		
		IndexSearcher searcher = new IndexSearcher(reader);
		
	    Analyzer analyzer = new TurkishAnalyzer();
	  
	    String field = "text";
	    QueryParser parser = new QueryParser(field, analyzer);
	    Statement stmt=Db_Connect("lucene","root","");
	    ResultSet rs=Db_Read(stmt,"SELECT QueryID,Topic FROM queries");
	    String queryString = "";
	    String q_id=null;
	    List<Result> resultlist =new ArrayList<Result>();
	    while(rs.next())
		{
		
	    	queryString=rs.getString("Topic");
	    	Query query = parser.parse(queryString);
		  
		      Search search_op=new Search();
		      search_op.search(searcher,query,rs.getInt("QueryId"),resultlist);
			 
		}
	    Collections.sort(resultlist);
	    for(Result t:resultlist)
	    {
	    	FileWriter fwrite=null;
	    	try {
	    	fwrite=new FileWriter("resultfirst.result",true);
	    	fwrite.write(t.toString()+"\n");
	    	}
	    	catch(IOException ex)
	    	{
	    		System.out.println("Dosya Açýlýrken hata oluþtu");
	    	}
	    	finally {
	    		if(fwrite!=null)
	    		{
	    			try {
	    				fwrite.close();
	    		    	}
	    		    	catch(IOException ex)
	    		    	{
	    		    		System.out.println("Dosya Kapatýlýrken hata oluþtu");
	    		    	}
	    		
	    		}
	    		
	    	}
	    }
	    
	    System.out.println("Bitti");
	    
	    
	    
	    
	      
	}
	public static ResultSet Db_Read(Statement stmt,String sql) throws SQLException
	{   
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
		
	}
	
	public static Statement Db_Connect(String dbname,String username,String pass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		/** Assuming database solr_test exists */
		String url="jdbc:mysql://localhost:3306/"+dbname;
		Connection conn = DriverManager.getConnection(url, username, pass);
		Statement stmt = conn.createStatement();
		return stmt;
	}
	
	
	public void search(IndexSearcher searcher, Query query,int query_id,List<Result> resultlist) throws Exception
	{
		TopDocs results=searcher.search(query, 1000);
		
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = Math.toIntExact(results.totalHits.value);
		 //System.out.println(numTotalHits + " total matching documents");
		int r=1;
		 for (ScoreDoc sd :hits) {
				Document d = searcher.doc(sd.doc);
				Result rslt=new Result(query_id,d.get("docno"),r++, sd.score,"QO","Standart");
				resultlist.add(rslt);
				
			}
		
		
	
		 
	}

}

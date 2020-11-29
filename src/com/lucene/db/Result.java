package com.lucene.db;

public class Result implements Comparable<Result>{
	public int queryId;
	public String documentId;
	public int rank;
	public float score;
	public String qo;
	public String standart;
	public Result(int queryId, String documentId, int rank, float score, String qo, String standart) {
		super();
		this.queryId = queryId;
		this.documentId = documentId;
		this.rank = rank;
		this.score = score;
		this.qo = qo;
		this.standart = standart;
	}
	@Override
	public int compareTo(Result result) {
	if(this.queryId >result.queryId)
	{
		return 1;
	}
	else if(this.queryId < result.queryId)
	{
		return -1;
	}
	
		return 0;
	}
	@Override
	public String toString() {
		String qid=String.valueOf(this.queryId);
		String rank=String.valueOf(this.rank);
		String score=String.valueOf(this.score);
		
		return qid+" "+this.qo+" "+this.documentId+" "+rank+" "+score+" "+this.standart;
	
	}
	
	

}

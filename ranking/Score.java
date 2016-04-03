package ranking;

public class Score {
	
	private int rank_bm25, rank_mmr, rank_pd;
	private float score_bm25, score_mmr, score_pd;
	
	public Score() {
		
	}
	
	public void setRankBM25(int rank) {
		this.rank_bm25 = rank;
	}
	
	public void setRankMMR(int rank) {
		this.rank_mmr = rank;
	}

	public void setRankPD(int rank) {
		this.rank_pd = rank;
	}
	
	public void setScoreBM25(float score) {
		this.score_bm25 = score;
	}
	
	public void setScoreMMR(float score) {
		this.score_mmr = score;
	}

	public void setScorePD(float score) {
		this.score_pd = score;
	}
	
	public int getRankBM25() {
		return rank_bm25;
	}

	public int getRankMMR() {
		return rank_mmr;
	}
	
	public int getRankPD() {
		return rank_pd;
	}
	
	public float getScoreBM25() {
		return score_bm25;
	}
	
	public float getScoreMMR() {
		return score_mmr;
	}
	
	public float getScorePD() {
		return score_pd;
	}

}

package com.shopme.reviewVote;

public class VoteResult {

	private boolean success;
	private String message;
	private int voteCount;
	
	public static VoteResult fail(String message) {
		return new VoteResult(false, message, 0);
	}
	
	public static VoteResult success(String message, int voteCount) {
		return new VoteResult(true, message , voteCount);
	}
	
	public VoteResult(boolean success, String message, int voteCount) {
		this.success = success;
		this.message = message;
		this.voteCount = voteCount;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
	
	
}

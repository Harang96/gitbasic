package com.webljy.dto;

public class SearchCriteria {
	private String searchWords;
	private String searchType;
	
	public SearchCriteria(String searchWords, String searchType) {
		super();
		this.searchWords = searchWords;
		this.searchType = searchType;
	}

	public String getSearchWords() {
		return searchWords;
	}

	public void setSearchWords(String searchWords) {
		this.searchWords = searchWords;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public String toString() {
		return "SearchCriteria [searchWords=" + searchWords + ", searchType=" + searchType + "]";
	}
}

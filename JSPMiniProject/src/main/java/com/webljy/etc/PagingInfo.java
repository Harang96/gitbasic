package com.webljy.etc;

public class PagingInfo {
	private int totalPostCnt; // 전체 게시글의 개수
	private int viewPostCntPerPage = 5; // 한 페이지 당 글의 개수
	private int totalPageCnt; // 총 페이지 수
	private int startRowIndex; // 보여주기 시작할 글의 row index 번호
	private int pageNo; // 유저가 보고 있는 페이지
	
	private int pageCntPerBlock = 5; // 한 개 블럭에 보여줄 페이지 개수
	private int totalPagingBlockCnt; // 전체 페이징 블럭 개수
	private int pageBlockOfCurrentPage; // 현재 페이지가 속한 페이징 블럭 번호
	private int startNumOfCurrentPagingBlock; // 현재 페이징 블럭에서의 출력 시작 페이지 번호
	private int endNumOfCurrentPagingBlock; // 현재 페이징 블럭에서의 출력 끝 페이지 번호
	
	public PagingInfo() {}

	public int getTotalPostCnt() {
		return totalPostCnt;
	}

	public int getViewPostCntPerPage() {
		return viewPostCntPerPage;
	}

	public int getTotalPageCnt() {
		return totalPageCnt;
	}

	public int getStartRowIndex() {
		return startRowIndex;
	}

	public int getPageNo() {
		return pageNo;
	}
	
	// 페이징 블럭 관련한 getter
	public int getPageCntPerBlock() {
		return pageCntPerBlock;
	}

	public int getTotalPagingBlockCnt() {
		return totalPagingBlockCnt;
	}

	public int getPageBlockOfCurrentPage() {
		return pageBlockOfCurrentPage;
	}

	public int getStartNumOfCurrentPagingBlock() {
		return startNumOfCurrentPagingBlock;
	}

	public int getEndNumOfCurrentPagingBlock() {
		return endNumOfCurrentPagingBlock;
	}
	
	private void setTotalPostCnt(int totalPostCnt) {
		this.totalPostCnt = totalPostCnt;
	}
	
	public void setViewPostCntPerPage(int viewPostCntPerPage) {
		this.viewPostCntPerPage = viewPostCntPerPage;
	}
	
	private void setTotalPageCnt(int totalPostCnt, int viewPostCntPerPage) {
		this.totalPageCnt = (int) Math.ceil((double)totalPostCnt / (double)viewPostCntPerPage);
	}
	
	private void setStartRowIndex(int pageNo, int viewPostCntPerPage) {
		this.startRowIndex = (pageNo - 1) * viewPostCntPerPage;
	}
	
	private void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	// 페이징 블럭 관련 변수 setter
	private void setTotalPagingBlockCnt() {
		// 전체 페이지 수 / pageCntPerBlock --> 나누어 떨어지지 않으면 + 1
		if ((this.totalPageCnt % this.pageCntPerBlock) == 0) {
			this.totalPagingBlockCnt =  this.totalPageCnt / this.pageCntPerBlock;
		} else {
			this.totalPagingBlockCnt =  this.totalPageCnt / this.pageCntPerBlock + 1;
		}
	}	
	
	private void setPageBlockOfCurrentPage() {
		// pageNo / pageCntPerBlock => 나누어 떨어지면 + 1
		this.pageBlockOfCurrentPage = (int) Math.ceil((double)this.pageNo / (double)this.pageCntPerBlock);
	}
	
	private void setStartNumOfCurrentPagingBlock() {
		// (블럭 번호 - 1) * pageCntPerBlock + 1
		this.startNumOfCurrentPagingBlock = (this.pageBlockOfCurrentPage - 1) * this.pageCntPerBlock + 1;
	}
	
	private void setEndNumOfCurrentPagingBlock() {
		// 블럭 번호 * pageCntPerBlock
		this.endNumOfCurrentPagingBlock = this.pageBlockOfCurrentPage * this.pageCntPerBlock; 
		if (endNumOfCurrentPagingBlock >= this.totalPageCnt) {
			this.endNumOfCurrentPagingBlock = this.totalPageCnt;
		}
	}
	
	// 한 번에 세팅하자
	public void setAttribute(int totalPostCnt, int pageNo) {
		setTotalPostCnt(totalPostCnt);
		setPageNo(pageNo);
		setTotalPageCnt(totalPostCnt, this.viewPostCntPerPage);
		setStartRowIndex(pageNo, this.viewPostCntPerPage);
		setTotalPagingBlockCnt();
		setPageBlockOfCurrentPage();
		setStartNumOfCurrentPagingBlock();
		setEndNumOfCurrentPagingBlock();
	}
	
	public void setAttribute(int value, String kind) {
		if (kind == "pageNo") {
			setTotalPostCnt(this.totalPostCnt);
			setPageNo(value);
			setTotalPageCnt(this.totalPostCnt, this.viewPostCntPerPage);
			setStartRowIndex(value, this.viewPostCntPerPage);
		} else if (kind == "totalPostCnt") {
			setTotalPostCnt(value);
			setPageNo(this.pageNo);
			setTotalPageCnt(value, this.viewPostCntPerPage);
			setStartRowIndex(this.pageNo, this.viewPostCntPerPage);
		}
	}

	@Override
	public String toString() {
		return "PagingInfo [totalPostCnt=" + totalPostCnt + ", viewPostCntPerPage=" + viewPostCntPerPage
				+ ", totalPageCnt=" + totalPageCnt + ", startRowIndex=" + startRowIndex + ", pageNo=" + pageNo
				+ ", pageCntPerBlock=" + pageCntPerBlock + ", totalPagingBlockCnt=" + totalPagingBlockCnt
				+ ", pageBlockOfCurrentPage=" + pageBlockOfCurrentPage + ", startNumOfCurrentPagingBlock="
				+ startNumOfCurrentPagingBlock + ", endNumOfCurrentPagingBlock=" + endNumOfCurrentPagingBlock + "]";
	}
	
}

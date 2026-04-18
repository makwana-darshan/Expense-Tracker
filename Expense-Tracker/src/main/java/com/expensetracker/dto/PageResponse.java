package com.expensetracker.dto;

import java.util.List;

public class PageResponse<T> {

	private List<T> content;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private boolean isFirst;
	private boolean isLast;

	
	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean first) {
		isFirst = first;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean last) {
		isLast = last;
	}
}
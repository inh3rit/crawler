package com.zxsoft.crawler.p.ctrl;

public class HttpRequestEntity {

	private String url;
	private String listdom;
	private String linedom;
	private String urldom;
	private String datedom;
	private String synopsisdom;
	private String udpatedom;
	private String authordom;
	private String sourceName;
	private String type;
	private Integer sourceId;
	private Integer sectionId;
	private String keywordEncoding;
	private String keyword;

	public HttpRequestEntity(String url, String listdom, String linedom, String urldom, String datedom,
			String synopsisdom, String udpatedom, String authordom, String sourceName, String type, Integer sourceId,
			Integer sectionId, String keywordEncoding, String keyword) {
		super();
		this.url = url;
		this.listdom = listdom;
		this.linedom = linedom;
		this.urldom = urldom;
		this.datedom = datedom;
		this.synopsisdom = synopsisdom;
		this.udpatedom = udpatedom;
		this.authordom = authordom;
		this.sourceName = sourceName;
		this.type = type;
		this.sourceId = sourceId;
		this.sectionId = sectionId;
		this.keywordEncoding = keywordEncoding;
		this.keyword = keyword;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getListdom() {
		return listdom;
	}

	public void setListdom(String listdom) {
		this.listdom = listdom;
	}

	public String getLinedom() {
		return linedom;
	}

	public void setLinedom(String linedom) {
		this.linedom = linedom;
	}

	public String getUrldom() {
		return urldom;
	}

	public void setUrldom(String urldom) {
		this.urldom = urldom;
	}

	public String getDatedom() {
		return datedom;
	}

	public void setDatedom(String datedom) {
		this.datedom = datedom;
	}

	public String getSynopsisdom() {
		return synopsisdom;
	}

	public void setSynopsisdom(String synopsisdom) {
		this.synopsisdom = synopsisdom;
	}

	public String getUdpatedom() {
		return udpatedom;
	}

	public void setUdpatedom(String udpatedom) {
		this.udpatedom = udpatedom;
	}

	public String getAuthordom() {
		return authordom;
	}

	public void setAuthordom(String authordom) {
		this.authordom = authordom;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getKeywordEncoding() {
		return keywordEncoding;
	}

	public void setKeywordEncoding(String keywordEncoding) {
		this.keywordEncoding = keywordEncoding;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}

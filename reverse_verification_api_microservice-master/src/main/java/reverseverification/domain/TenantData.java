package reverseverification.domain;

import java.util.List;

public class TenantData {
	private String errmsg;
	private List<TenantInfo> result;
	private long totalCount;

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public List<TenantInfo> getResult() {
		return result;
	}

	public void setResult(List<TenantInfo> result) {
		this.result = result;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}

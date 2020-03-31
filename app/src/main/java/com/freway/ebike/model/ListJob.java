/**
 * 
 */
package com.freway.ebike.model;

import java.util.List;

/**
 * 资源集合
 * 
 * @author mengjk
 *
 *         2015年5月21日
 */
public class ListJob extends EBResponse {
	private static final long serialVersionUID = -7254402164173434218L;
	private List<Job> resultData;
	public List<Job> getResultData() {
		return resultData;
	}
	public void setResultData(List<Job> resultData) {
		this.resultData = resultData;
	}

}

/**
 * 
 */
package com.freway.ebike.model;

import java.io.Serializable;

/**
 * 用户User
 * 
 * @author mengjk
 *
 *         2015年5月25日
 */
public class Job implements Serializable{
	private static final long serialVersionUID = 1L;
	private String progress;
	private String jobID;
	private String jobName;
	private String jobManagerID;
	private String jobManagerName;
	private String jobOwner;
	private String jobStatus;
	private String jobStartTime;
	private String jobWallTime;
	private String jobCPUTime;
	private String jobMemUsed;
	private String jobVMemUsed;
	private String jobVncSid;
	private String initWorkDir;
	private String exitStatus;
	public String getJobID() {
		return jobID;
	}
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobManagerID() {
		return jobManagerID;
	}
	public void setJobManagerID(String jobManagerID) {
		this.jobManagerID = jobManagerID;
	}
	public String getJobManagerName() {
		return jobManagerName;
	}
	public void setJobManagerName(String jobManagerName) {
		this.jobManagerName = jobManagerName;
	}
	public String getJobOwner() {
		return jobOwner;
	}
	public void setJobOwner(String jobOwner) {
		this.jobOwner = jobOwner;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getJobWallTime() {
		return jobWallTime;
	}
	public void setJobWallTime(String jobWallTime) {
		this.jobWallTime = jobWallTime;
	}
	public String getJobCPUTime() {
		return jobCPUTime;
	}
	public void setJobCPUTime(String jobCPUTime) {
		this.jobCPUTime = jobCPUTime;
	}
	public String getJobMemUsed() {
		return jobMemUsed;
	}
	public void setJobMemUsed(String jobMemUsed) {
		this.jobMemUsed = jobMemUsed;
	}
	public String getJobVMemUsed() {
		return jobVMemUsed;
	}
	public void setJobVMemUsed(String jobVMemUsed) {
		this.jobVMemUsed = jobVMemUsed;
	}
	public String getJobVncSid() {
		return jobVncSid;
	}
	public void setJobVncSid(String jobVncSid) {
		this.jobVncSid = jobVncSid;
	}
	public String getInitWorkDir() {
		return initWorkDir;
	}
	public void setInitWorkDir(String initWorkDir) {
		this.initWorkDir = initWorkDir;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getExitStatus() {
		return exitStatus;
	}
	public void setExitStatus(String exitStatus) {
		this.exitStatus = exitStatus;
	}
	public String getJobStartTime() {
		return jobStartTime;
	}
	public void setJobStartTime(String jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

}

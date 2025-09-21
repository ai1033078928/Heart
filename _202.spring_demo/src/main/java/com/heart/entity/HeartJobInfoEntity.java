package com.heart.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "heart_job_info")
public class HeartJobInfoEntity {

    @Id
    @Basic
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "file_head")
    private String fileHead;
    @Basic
    @Column(name = "file_path")
    private String filePath;
    @Basic
    @Column(name = "archive_path")
    private String archivePath;
    @Basic
    @Column(name = "load_server_name")
    private String loadServerName;
    @Basic
    @Column(name = "target_table_name")
    private String targetTableName;
    @Basic
    @Column(name = "time_interval")
    private Integer timeInterval;
    @Basic
    @Column(name = "time_unit")
    private String timeUnit;
    @Basic
    @Column(name = "job_status")
    private String jobStatus;
    @Basic
    @Column(name = "job_enable")
    private String jobEnable;
    @Basic
    @Column(name = "start_load_time")
    private String startLoadTime;
    @Basic
    @Column(name = "last_load_time")
    private String lastLoadTime;
    @Basic
    @Column(name = "last_file_name")
    private String lastFileName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileHead() {
        return fileHead;
    }

    public void setFileHead(String fileHead) {
        this.fileHead = fileHead;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public String getLoadServerName() {
        return loadServerName;
    }

    public void setLoadServerName(String loadServerName) {
        this.loadServerName = loadServerName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobEnable() {
        return jobEnable;
    }

    public void setJobEnable(String jobEnable) {
        this.jobEnable = jobEnable;
    }

    public String getStartLoadTime() {
        return startLoadTime;
    }

    public void setStartLoadTime(String startLoadTime) {
        this.startLoadTime = startLoadTime;
    }

    public String getLastLoadTime() {
        return lastLoadTime;
    }

    public void setLastLoadTime(String lastLoadTime) {
        this.lastLoadTime = lastLoadTime;
    }

    public String getLastFileName() {
        return lastFileName;
    }

    public void setLastFileName(String lastFileName) {
        this.lastFileName = lastFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeartJobInfoEntity that = (HeartJobInfoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(fileHead, that.fileHead) && Objects.equals(filePath, that.filePath) && Objects.equals(archivePath, that.archivePath) && Objects.equals(loadServerName, that.loadServerName) && Objects.equals(targetTableName, that.targetTableName) && Objects.equals(timeInterval, that.timeInterval) && Objects.equals(timeUnit, that.timeUnit) && Objects.equals(jobStatus, that.jobStatus) && Objects.equals(jobEnable, that.jobEnable) && Objects.equals(startLoadTime, that.startLoadTime) && Objects.equals(lastLoadTime, that.lastLoadTime) && Objects.equals(lastFileName, that.lastFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileHead, filePath, archivePath, loadServerName, targetTableName, timeInterval, timeUnit, jobStatus, jobEnable, startLoadTime, lastLoadTime, lastFileName);
    }
}

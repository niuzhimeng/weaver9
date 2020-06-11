package com.weavernorth.createFlow.vo;

import java.io.Serializable;
import java.util.List;

/**
 * description :
 * author ：JHY
 * date : 2020/5/13
 * version : 1.0
 */
public class WorkflowDetailTableInfoEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1328729781044551608L;

    /**
     * 明细表标题
     */
    private String tableTitle;

    /**
     * 数据库明细表名
     */
    private String tableDBName;

    /**
     * 明细表字段名称
     */
    private String[] tableFieldName;

    /**
     * 删除数据ID
     */
    private String deleteKeys;

    /**
     * 明细表列表
     */
    private List<WorkflowRequestTableRecord> workflowRequestTableRecords;

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public String getTableDBName() {
        return tableDBName;
    }

    public void setTableDBName(String tableDBName) {
        this.tableDBName = tableDBName;
    }

    public String[] getTableFieldName() {
        return tableFieldName;
    }

    public void setTableFieldName(String[] tableFieldName) {
        this.tableFieldName = tableFieldName;
    }

    public List<WorkflowRequestTableRecord> getWorkflowRequestTableRecords() {
        return workflowRequestTableRecords;
    }

    public void setWorkflowRequestTableRecords(List<WorkflowRequestTableRecord> workflowRequestTableRecords) {
        this.workflowRequestTableRecords = workflowRequestTableRecords;
    }

    public String getDeleteKeys() {
        return deleteKeys;
    }

    public void setDeleteKeys(String deleteKeys) {
        this.deleteKeys = deleteKeys;
    }
}

package com.weavernorth.createFlow.vo;

import java.io.Serializable;
import java.util.List;

public class WorkflowRequestTableRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -202638298244870735L;

	//修改时明细行ID，新增时赋0
	private int recordOrder;
	
	private List<WorkflowRequestTableField> workflowRequestTableFields;

	public int getRecordOrder() {
		return recordOrder;
	}

	public void setRecordOrder(int recordOrder) {
		this.recordOrder = recordOrder;
	}

	public List<WorkflowRequestTableField> getWorkflowRequestTableFields() {
		return workflowRequestTableFields;
	}

	public void setWorkflowRequestTableFields(List<WorkflowRequestTableField> workflowRequestTableFields) {
		this.workflowRequestTableFields = workflowRequestTableFields;
	}
}

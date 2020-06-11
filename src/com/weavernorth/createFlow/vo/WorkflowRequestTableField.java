package com.weavernorth.createFlow.vo;

import java.io.Serializable;

/**
 * 工作流字段信息
 */
public class WorkflowRequestTableField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2865596549306206630L;

	/**
	 * 字段ID
	 */
	private String fieldId;
	
	/**
	 * 字段名称
	 */
	private String fieldName;
	
	/**
	 * 字段值
	 */
	private String fieldValue;
	
	/**
	 * 界面字段表现形式
	 */
	private String fieldHtmlType;
	
	/**
	 * 界面字段类型
	 */
	private String fieldType;
	
	/**
	 * 数据库字段类型
	 */
	private String fieldDBType;
	
	/**
	 * 表单中提交的字段名
	 */
	private String fieldFormName;
	
	/**
	 * 显示顺序
	 */
	private int fieldOrder;
	
	/**
	 * 是否显示
	 */
	private boolean isView;
	
	/**
	 * 是否可编辑
	 */
	private boolean isEdit;
	
	/**
	 * 是否必填
	 */
	private boolean isMand;
	
	/**
	 * 显示字段名称
	 */
	private String fieldShowName;
	
	/**
	 * 浏览框字段URL
	 */
	private String browserurl;
	
	/**
	 * 下拉框字段信息
	 */
	private String[] selectnames;
	private String[] selectvalues;
	
	/**
	 * 显示字段值
	 */
	private String fieldShowValue;
	
	/**
	 * 字段html显示
	 */
	private String filedHtmlShow;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getFieldHtmlType() {
		return fieldHtmlType;
	}

	public void setFieldHtmlType(String fieldHtmlType) {
		this.fieldHtmlType = fieldHtmlType;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldDBType() {
		return fieldDBType;
	}

	public void setFieldDBType(String fieldDBType) {
		this.fieldDBType = fieldDBType;
	}

	public String getFieldFormName() {
		return fieldFormName;
	}

	public void setFieldFormName(String fieldFormName) {
		this.fieldFormName = fieldFormName;
	}

	public int getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(int fieldOrder) {
		this.fieldOrder = fieldOrder;
	}

	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public boolean isMand() {
		return isMand;
	}

	public void setMand(boolean isMand) {
		this.isMand = isMand;
	}

	public String getFieldShowName() {
		return fieldShowName;
	}

	public void setFieldShowName(String fieldShowName) {
		this.fieldShowName = fieldShowName;
	}

	public String getFieldShowValue() {
		return fieldShowValue;
	}

	public void setFieldShowValue(String fieldShowValue) {
		this.fieldShowValue = fieldShowValue;
	}

	public String getBrowserurl() {
		return browserurl;
	}

	public void setBrowserurl(String browserurl) {
		this.browserurl = browserurl;
	}

	public String[] getSelectnames() {
		return selectnames;
	}

	public void setSelectnames(String[] selectnames) {
		this.selectnames = selectnames;
	}

	public String[] getSelectvalues() {
		return selectvalues;
	}

	public void setSelectvalues(String[] selectvalues) {
		this.selectvalues = selectvalues;
	}

	public String getFiledHtmlShow() {
		return filedHtmlShow;
	}

	public void setFiledHtmlShow(String filedHtmlShow) {
		this.filedHtmlShow = filedHtmlShow;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}


}

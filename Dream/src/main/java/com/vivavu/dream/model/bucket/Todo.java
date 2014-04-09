package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 9.
 */
public class Todo implements Serializable{
    @SerializedName("todoDeadline")
    private Date todoDeadLine;
    @SerializedName("todoID")
    private Integer todoId;
    @SerializedName("todoIsLive")
    private Integer todoIsLive;
    @SerializedName("todoIsPrivate")
    private Integer todoIsPrivate;
    @SerializedName("todoParent_id")
    private Integer todoParentId;
    @SerializedName("todoRange")
    private String todoRange;

    @SerializedName("todoRegDate")
    private Date todoRegDate;
    @SerializedName("todoScope")
    private String todoScope;
    @SerializedName("todoTitle")
    private String todoTitle;

    @SerializedName("todoRptCndt")
    private String todoRptCndt;

    @SerializedName("todoRptType")
    private String todoRptType;

    public Date getTodoDeadLine() {
        return todoDeadLine;
    }

    public void setTodoDeadLine(Date todoDeadLine) {
        this.todoDeadLine = todoDeadLine;
    }

    public Integer getTodoId() {
        return todoId;
    }

    public void setTodoId(Integer todoId) {
        this.todoId = todoId;
    }

    public Integer getTodoIsLive() {
        return todoIsLive;
    }

    public void setTodoIsLive(Integer todoIsLive) {
        this.todoIsLive = todoIsLive;
    }

    public Integer getTodoIsPrivate() {
        return todoIsPrivate;
    }

    public void setTodoIsPrivate(Integer todoIsPrivate) {
        this.todoIsPrivate = todoIsPrivate;
    }

    public Integer getTodoParentId() {
        return todoParentId;
    }

    public void setTodoParentId(Integer todoParentId) {
        this.todoParentId = todoParentId;
    }

    public String getTodoRange() {
        return todoRange;
    }

    public void setTodoRange(String todoRange) {
        this.todoRange = todoRange;
    }

    public Date getTodoRegDate() {
        return todoRegDate;
    }

    public void setTodoRegDate(Date todoRegDate) {
        this.todoRegDate = todoRegDate;
    }

    public String getTodoScope() {
        return todoScope;
    }

    public void setTodoScope(String todoScope) {
        this.todoScope = todoScope;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getTodoRptCndt() {
        return todoRptCndt;
    }

    public void setTodoRptCndt(String todoRptCndt) {
        this.todoRptCndt = todoRptCndt;
    }

    public String getTodoRptType() {
        return todoRptType;
    }

    public void setTodoRptType(String todoRptType) {
        this.todoRptType = todoRptType;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "todoDeadLine=" + todoDeadLine +
                ", todoId=" + todoId +
                ", todoIsLive=" + todoIsLive +
                ", todoIsPrivate=" + todoIsPrivate +
                ", todoParentId=" + todoParentId +
                ", todoRange='" + todoRange + '\'' +
                ", todoRegDate=" + todoRegDate +
                ", todoScope='" + todoScope + '\'' +
                ", todoTitle='" + todoTitle + '\'' +
                '}';
    }
}

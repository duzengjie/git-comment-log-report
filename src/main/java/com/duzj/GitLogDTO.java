package com.duzj;

/**
 * @Description
 * @Date 2024/1/28 14:14
 * @Created by duzengjie
 */
public class GitLogDTO {
    private String name;

    private String createDate;

    private String comment;

    public GitLogDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

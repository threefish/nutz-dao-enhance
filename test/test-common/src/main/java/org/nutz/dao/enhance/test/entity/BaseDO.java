package org.nutz.dao.enhance.test.entity;

import lombok.Data;
import org.nutz.dao.enhance.annotation.CreatedBy;
import org.nutz.dao.enhance.annotation.LastModifiedDate;
import org.nutz.dao.entity.annotation.Column;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/17
 */
@Data
public class BaseDO implements java.io.Serializable {

    @Column
    @LastModifiedDate
    protected Date gmtCreate;

    @Column
    @CreatedBy
    protected String createBy;
}

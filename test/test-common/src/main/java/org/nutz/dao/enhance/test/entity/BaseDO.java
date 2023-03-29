package org.nutz.dao.enhance.test.entity;

import lombok.Data;
import org.nutz.dao.enhance.annotation.CreatedBy;
import org.nutz.dao.enhance.annotation.CreatedDate;
import org.nutz.dao.entity.annotation.Column;

import java.time.LocalDateTime;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/17
 */
@Data
public class BaseDO implements java.io.Serializable {

    @Column
    @CreatedDate
    protected LocalDateTime gmtCreate;

    @Column
    @CreatedBy
    protected String createBy;
}

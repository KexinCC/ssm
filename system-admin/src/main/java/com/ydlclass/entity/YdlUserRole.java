package com.ydlclass.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户和角色关联表(YdlUserRole)实体类
 *
 * @author makejava
 * @since 2023-01-15 18:10:54
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YdlUserRole implements Serializable {
    private static final long serialVersionUID = -68433771304019465L;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long roleId;

}


package com.ydlclass.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色和菜单关联表(YdlRoleMenu)实体类
 *
 * @author makejava
 * @since 2023-01-15 18:10:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YdlRoleMenu implements Serializable {
    private static final long serialVersionUID = 618603034985698302L;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 菜单ID
     */
    private Long menuId;

}


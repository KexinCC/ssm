package com.ydlclass.dao;

import com.ydlclass.entity.YdlRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 角色信息表(YdlRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-15 18:10:54
 */
public interface YdlRoleDao {

    /**
     * 通过ID查询单条数据
     *
     * @param roleId 主键
     * @return 实例对象
     */
    YdlRole queryById(Long roleId);

    /**
     * 查询指定行数据
     *
     * @param ydlRole 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<YdlRole> queryAllByLimit(YdlRole ydlRole, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param ydlRole 查询条件
     * @return 总行数
     */
    long count(YdlRole ydlRole);

    /**
     * 新增数据
     *
     * @param ydlRole 实例对象
     * @return 影响行数
     */
    int insert(YdlRole ydlRole);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<YdlRole> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<YdlRole> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<YdlRole> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<YdlRole> entities);

    /**
     * 修改数据
     *
     * @param ydlRole 实例对象
     * @return 影响行数
     */
    int update(YdlRole ydlRole);

    /**
     * 通过主键删除数据
     *
     * @param roleId 主键
     * @return 影响行数
     */
    int deleteById(Long roleId);

}


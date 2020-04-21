package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.TbObjectAtt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public interface TbObjectAttMapper {
    /**
    * 查询集合
    * @return 集合
    */
    List<TbObjectAtt> getTbObjectAttList(Map<String, Object> params);

    /**
    * 新增
    */
    void addTbObjectAtt(TbObjectAtt tbObjectAtt);

    /**
    * 修改
    */
    void updateTbObjectAttById(TbObjectAtt tbObjectAtt);

    /**
    * 删除
    */
    void deleteTbObjectAttById(int id);

    /**
    * 查询
    * @return 
    */
    TbObjectAtt getTbObjectAttById(int id);
}

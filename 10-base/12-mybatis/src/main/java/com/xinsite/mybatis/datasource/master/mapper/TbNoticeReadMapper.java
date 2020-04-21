package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.TbNoticeRead;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public interface TbNoticeReadMapper {
    /**
    * 查询集合
    * @return 集合
    */
    List<TbNoticeRead> getTbNoticeReadList(Map<String, Object> params);

    /**
    * 新增
    */
    void addTbNoticeRead(TbNoticeRead tbNoticeRead);

    /**
    * 修改
    */
    void updateTbNoticeReadById(TbNoticeRead tbNoticeRead);

    /**
    * 删除
    */
    void deleteTbNoticeReadById(int id);

    /**
    * 查询
    * @return 
    */
    TbNoticeRead getTbNoticeReadById(int id);
}

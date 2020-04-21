package com.xinsite.mybatis.datasource.master.mapper;

import com.xinsite.mybatis.datasource.master.entity.TbNotice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public interface TbNoticeMapper {
    /**
    * 查询集合
    * @return 集合
    */
    List<TbNotice> getTbNoticeList(Map<String, Object> params);

    /**
    * 新增
    */
    void addTbNotice(TbNotice tbNotice);

    /**
    * 修改
    */
    void updateTbNoticeById(TbNotice tbNotice);

    /**
    * 删除
    */
    void deleteTbNoticeById(int id);

    /**
    * 查询
    * @return 
    */
    TbNotice getTbNoticeById(int id);
}

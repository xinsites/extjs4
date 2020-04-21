package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.TbNoticeRead;
import com.xinsite.mybatis.datasource.master.mapper.TbNoticeReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public class TbNoticeReadService {

    @Autowired
    private TbNoticeReadMapper tbNoticeReadMapper;

    /**
    * 查询集合
    * @return 集合
    */
    public List<TbNoticeRead> getTbNoticeReadList(Map<String, Object> params) {
        return tbNoticeReadMapper.getTbNoticeReadList(params);
    }

    /**
    * 新增
    */
    public void addTbNoticeRead(TbNoticeRead tbNoticeRead) {
        tbNoticeReadMapper.addTbNoticeRead(tbNoticeRead);
    }

    /**
    * 修改
    */
    public void updateTbNoticeReadById(TbNoticeRead tbNoticeRead) {
        tbNoticeReadMapper.updateTbNoticeReadById(tbNoticeRead);
    }

    /**
    * 删除
    */
    public void deleteTbNoticeReadById(int Primarykey) {
        try {
            tbNoticeReadMapper.deleteTbNoticeReadById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TbNoticeRead dealUpdateBean(TbNoticeRead tbNoticeRead, Map<String, Object> params) {
        tbNoticeRead = new TbNoticeRead();
        return tbNoticeRead;
    }

    public TbNoticeRead dealCreateBean(Map<String, Object> params) {
        TbNoticeRead tbNoticeRead = new TbNoticeRead();
        return tbNoticeRead;
    }

    /**
    * 查询
    * @return 
    */
    public TbNoticeRead getTbNoticeReadById(int Primarykey) {
        return tbNoticeReadMapper.getTbNoticeReadById(Primarykey);
    }
}

package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.TbNotice;
import com.xinsite.mybatis.datasource.master.mapper.TbNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public class TbNoticeService {

    @Autowired
    private TbNoticeMapper tbNoticeMapper;

    /**
    * 查询集合
    * @return 集合
    */
    public List<TbNotice> getTbNoticeList(Map<String, Object> params) {
        return tbNoticeMapper.getTbNoticeList(params);
    }

    /**
    * 新增
    */
    public void addTbNotice(TbNotice tbNotice) {
        tbNoticeMapper.addTbNotice(tbNotice);
    }

    /**
    * 修改
    */
    public void updateTbNoticeById(TbNotice tbNotice) {
        tbNoticeMapper.updateTbNoticeById(tbNotice);
    }

    /**
    * 删除
    */
    public void deleteTbNoticeById(int Primarykey) {
        try {
            tbNoticeMapper.deleteTbNoticeById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TbNotice dealUpdateBean(TbNotice tbNotice, Map<String, Object> params) {
        tbNotice = new TbNotice();
        return tbNotice;
    }

    public TbNotice dealCreateBean(Map<String, Object> params) {
        TbNotice tbNotice = new TbNotice();
        return tbNotice;
    }

    /**
    * 查询
    * @return 
    */
    public TbNotice getTbNoticeById(int Primarykey) {
        return tbNoticeMapper.getTbNoticeById(Primarykey);
    }
}

package com.xinsite.mybatis.datasource.master.service;

import com.xinsite.mybatis.datasource.master.entity.TbObjectAtt;
import com.xinsite.mybatis.datasource.master.mapper.TbObjectAttMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by zhangxiaxin
 * create time: 2019-10-29
 */
@Service
public class TbObjectAttService {

    @Autowired
    private TbObjectAttMapper tbObjectAttMapper;

    /**
    * 查询集合
    * @return 集合
    */
    public List<TbObjectAtt> getTbObjectAttList(Map<String, Object> params) {
        return tbObjectAttMapper.getTbObjectAttList(params);
    }

    /**
    * 新增
    */
    public void addTbObjectAtt(TbObjectAtt tbObjectAtt) {
        tbObjectAttMapper.addTbObjectAtt(tbObjectAtt);
    }

    /**
    * 修改
    */
    public void updateTbObjectAttById(TbObjectAtt tbObjectAtt) {
        tbObjectAttMapper.updateTbObjectAttById(tbObjectAtt);
    }

    /**
    * 删除
    */
    public void deleteTbObjectAttById(int Primarykey) {
        try {
            tbObjectAttMapper.deleteTbObjectAttById(Primarykey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TbObjectAtt dealUpdateBean(TbObjectAtt tbObjectAtt, Map<String, Object> params) {
        tbObjectAtt = new TbObjectAtt();
        return tbObjectAtt;
    }

    public TbObjectAtt dealCreateBean(Map<String, Object> params) {
        TbObjectAtt tbObjectAtt = new TbObjectAtt();
        return tbObjectAtt;
    }

    /**
    * 查询
    * @return 
    */
    public TbObjectAtt getTbObjectAttById(int Primarykey) {
        return tbObjectAttMapper.getTbObjectAttById(Primarykey);
    }
}

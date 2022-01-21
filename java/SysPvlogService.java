package com.blueland.sys.log.service;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blueland.framework.service.CrudService;
import com.blueland.model.SysPvlogEntity;
import com.blueland.sys.log.dao.SysPvlogDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysPvlogService extends CrudService<SysPvlogDao, SysPvlogEntity> {
	@Autowired
	//SysPvlog对应的DAO类,主要用于数据库的增删改查等操作
	private SysPvlogDao sysPvlogDao;
	
	/**
	 * 根据条件查询列表(分页)
	 */
	public PageInfo<SysPvlogEntity> findSysPvlogPageListByParams(Map<String,Object> tagSysPvlog,int curPage,int pageSize){
	   PageHelper.startPage(curPage,pageSize);
	   List<SysPvlogEntity> list = sysPvlogDao.findSysPvlogListByParams(tagSysPvlog);
	   
	   return new PageInfo<SysPvlogEntity>(list);
	}
}

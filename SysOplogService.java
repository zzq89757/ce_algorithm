package com.blueland.sys.log.service;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blueland.framework.service.CrudService;
import com.blueland.model.SysOplogEntity;
import com.blueland.sys.log.dao.SysOplogDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysOplogService extends CrudService<SysOplogDao, SysOplogEntity> {
	@Autowired
	//SysOplog对应的DAO类,主要用于数据库的增删改查等操作
	private SysOplogDao sysOplogDao;
	
	/**
	 * 根据条件查询列表(分页)
	 */
	public PageInfo<SysOplogEntity> findSysOplogPageListByParams(Map<String,Object> tagSysOplog,int curPage,int pageSize){
	   PageHelper.startPage(curPage,pageSize);
	   List<SysOplogEntity> list = sysOplogDao.findSysOplogListByParams(tagSysOplog);
	   
	   return new PageInfo<SysOplogEntity>(list);
	}
}

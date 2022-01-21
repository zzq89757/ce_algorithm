package com.blueland.sys.log.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DNA序列处理
 * sequence
 */
@Slf4j
@Service
public class DNAsequenceService  {
	@SuppressWarnings("unused")
	@Autowired
	private SysOplogDao sysOplogDao;
	@SuppressWarnings("unused")
	private int numA;
	@SuppressWarnings("unused")
	private int numT;
	@SuppressWarnings("unused")
	private int numG;
	@SuppressWarnings("unused")
	private int numC;
	private String seqR = "";
	private String seqC = "";
	private String seqRC = "";
	
	/**
	 * DNA序列-反向
	 * @param params
	 * sequence（text1序列字段）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * resultvalue：返回第一行text的值
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> DNAReverse(String sequence){
		@SuppressWarnings("unchecked")
		Map<String,Object> result = new HashMap();
		result.put("success", false);
		
		if(sequence==null||StringUtils.isEmpty(sequence)){
			result.put("message", "sequence不能为空");
			return result;
		}
		RC(sequence);
		result.put("resultvalue", seqR);
		result.put("success", true);
		return result;
	}
	
	/**
	 * DNA序列-互补
	 * @param params
	 * sequence（text1序列字段）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * PrimerFvalue：返回第一行text的值
	 */
	public Map<String,Object> DNAComplementary(String sequence){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		result.put("success", false);
		
		if(sequence==null||StringUtils.isEmpty(sequence)){
			result.put("message", "sequence不能为空");
			return result;
		}
		RC(sequence);
		result.put("resultvalue", seqC);
		result.put("success", true);
		return result;
	}
	
	/**
	 * DNA序列-反向互补
	 * @param params
	 * sequence（text1序列字段）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * PrimerFvalue：返回第一行text的值
	 */
	public Map<String,Object> DNAReverseComplementary(String sequence){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		result.put("success", false);
		
		if(sequence ==null||StringUtils.isEmpty(sequence)){
			result.put("message", "sequence不能为空");
			return result;
		}
		RC(sequence);
		result.put("resultvalue", seqRC);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 替换并计数
	 * @param beforeRC
	 * @return
	 */
	private void RC(String beforeRC){
		seqC = "";
		char[] beforeRCs = beforeRC.toCharArray();
		for(int i=0;i<beforeRCs.length;i++) {
			char beforeRCc = beforeRCs[i];
			switch(beforeRCc){
			case 'A':
				seqC = seqC + "T";
				break;
			case 'T':
				seqC = seqC + "A";
				break;
			case 'C':
				seqC = seqC + "G";
				break;
			case 'G':
				seqC = seqC + "C";
				break;
			}
		}
		
		seqR = "";
		int a = beforeRC.length();
		for(int i=a-1;i>=0;i--){
			seqR = seqR + beforeRC.substring(i, i+1);
		}
		
		seqRC = "";		
		char[] seqRs = seqR.toCharArray();
		for(int i=0;i<seqRs.length;i++){
			char seq = seqRs[i];
			switch(seq){
			case 'A':
				seqRC = seqRC + "T";
				break;
			case 'T':
				seqRC = seqRC + "A";
				break;
			case 'C':
				seqRC = seqRC + "G";
				break;
			case 'G':
				seqRC = seqRC + "C";
				break;
			}
		}
		return;
	}
}

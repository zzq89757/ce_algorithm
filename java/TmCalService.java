package com.blueland.sys.log.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 引物Tm计算
 * sequence
 */
@Slf4j
@Service
public class TmCalService  {
	@SuppressWarnings("unused")
	@Autowired
	private SysOplogDao sysOplogDao;
	private int numA;
	private int numT;
	private int numG;
	private int numC;
	private int Primer_Length;
	private String trimseq;
	
	/**
	 * DNA序列-反向
	 * @param params
	 * sequence（text1序列字段）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * resultvalue1：返回第一行text的值
	 * resultvalue2：返回第二行text的值
	 * resultvalue3：返回第三行text的值
	 * resultvalue4：返回第四行text的值
	 * resultvalue5：返回第五行text的值
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> primerTmCal(String sequence){
		@SuppressWarnings("unchecked")
		Map<String,Object> result = new HashMap();
		result.put("success", false);
		
		if(sequence==null||StringUtils.isEmpty(sequence)){
			result.put("message", "sequence不能为空");
			return result;
		}
				
		Double GC = 0d;
		sequence(sequence);
		GC = ((double)numG + (double)numC) / ((double)numA + (double)numT + (double)numC + (double)numG) * 100d;
		BigDecimal b = new BigDecimal(GC);
		//GC含量四舍五入 保留一位小数
		GC = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		String Tuihuo = Tm_Cal(trimseq);
		
		result.put("resultvalue1", "引物基本信息：");
		result.put("resultvalue2", "引物长度（Length）："+Primer_Length+" bp");
		result.put("resultvalue3", "引物GC含量（GC Content）："+GC.toString()+" %");
		result.put("resultvalue4", "引物Tm值："+Tuihuo+" ℃");
		result.put("resultvalue1_en", "Primer basic information:");
		result.put("resultvalue2_en", "Length:  "+Primer_Length+" bp");
		result.put("resultvalue3_en", "GC Content:  "+GC.toString()+" %");
		result.put("resultvalue4_en", "Tm Value:  "+Tuihuo+" ℃");
		// 设置最高退火温度为72度
		if(Double.parseDouble(Tuihuo) >= 72d) {
			Tuihuo = "72.0";
		}
		result.put("resultvalue5", "推荐PCR退火温度："+Tuihuo+" ℃");
		result.put("resultvalue5_en", "Recommended PCR annealing temperature:  "+Tuihuo+" ℃");
		result.put("success", true);
		return result;
	}
	
	/***
	 * 计数
	 * @param seq
	 */
	private void sequence(String seq){
		String s="";
		String Tmp="";
		seq = seq.toUpperCase();
		numA = 0;
		numT = 0;
		numC = 0;
		numG = 0;
		int seqLength = seq.length();
		for(int i=0;i<seqLength;i++){
			Tmp = seq.substring(i,i+1);
			s = s + Tmp;
			switch(Tmp){
			case "A":
				numA++;
				break;
			case "T":
				numT++;
				break;
			case "C":
				numC++;
				break;
			case "G":
				numG++;
				break;
			}
		}
		trimseq = s;
		Primer_Length = trimseq.length();
	}
	
	/**
	 * 计算输入的碱基对串并返回退火温度（不设限制的Tm值）
	 * @param Oligo
	 * @return
	 */
	private String Tm_Cal(String Oligo){
		String OligoTemp;
		int OligoLength;
		sequence(Oligo);
		OligoTemp = trimseq;
		OligoLength = Primer_Length;
		Double dH = 0d;
		Double dS = 15.1d;
		
		String j = "";
		for(int i=0;i<OligoLength-1;i++){
			j = OligoTemp.substring(i, i+2);		
			switch(j) {
			case "AA":
				dH = dH + 9.1d;
				dS = dS + 24d;
				break;
			case "TT":
				dH = dH + 9.1d;
				dS = dS + 24d;
				break;
			case "AC":
				dH = dH + 6.5d;
				dS = dS + 17.3d;
				break;
			case "GT":
				dH = dH + 6.5d;
				dS = dS + 17.3d;
				break;
			case "AG":
				dH = dH + 7.8;
				dS = dS + 20.8;
				break;
			case "CT":
				dH = dH + 7.8d;
				dS = dS + 20.8d;
				break;
			case "AT":
				dH = dH + 8.6d;
				dS = dS + 23.9d;
				break;
			case "TA":
				dH = dH + 6d;
				dS = dS + 16.9d;
				break;
			case "TC":
				dH = dH + 5.6d;
				dS = dS + 13.5d;
				break;
			case "GA":
				dH = dH + 5.6d;
				dS = dS + 13.5d;
				break;
			case "TG":
				dH = dH + 5.8d;
				dS = dS + 12.9d;
				break;
			case "CA":
				dH = dH + 5.8d;
				dS = dS + 12.9d;
				break;
			case "CC":
				dH = dH + 11d;
				dS = dS + 26.6d;
				break;
			case "GG":
				dH = dH + 11d;
				dS = dS + 26.6d;
				break;
			case "CG":
				dH = dH + 11.9d;
				dS = dS + 27.8d;
				break;
			case "GC":
				dH = dH + 11.1d;
				dS = dS + 26.7d;
				break;
			}
		}
		
		BigDecimal b = new BigDecimal(dH);
		dH = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		b = new BigDecimal(dS);
		dS = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		dH = -dH * 1000;
		dS = -dS;
		
		Double TmTemp = dH / (dS + 1.987 * Math.log(250d / 4d * Math.pow(10d,-12))) + 16.6 / Math.log(10d) * Math.log(204.92 * Math.pow(10d,-3) / (1d + 0.7 * 204.92 * Math.pow(10d,-3))) - 273.15;
		String reStr = String.format("%.1f",TmTemp);
		return reStr;
	}
}

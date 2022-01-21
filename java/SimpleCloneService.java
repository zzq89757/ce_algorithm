package com.blueland.sys.log.service;


import java.math.BigDecimal;
import java.util.HashMap;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.model.SimpleCloneEntity;
import com.blueland.model.SingleFragmentCloneEntity;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 入门克隆
 * sequence
 */
@Slf4j
@Service
public class SimpleCloneService  {
	@SuppressWarnings("unused")
	@Autowired
	private SysOplogDao sysOplogDao;
	private String trimseq;
	private int Primer_Length;
	@SuppressWarnings("unused")
	private int numA;
	@SuppressWarnings("unused")
	private int numT;
	@SuppressWarnings("unused")
	private int numG;
	@SuppressWarnings("unused")
	private int numC;
	
	/**
	 * 入门克隆
	 * @param params
	 * sequence（text1序列字段）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * enzymeLociFlag（是否添加酶切位点标志）：不能为空，必须是：Y（是）、N（否）
	 * fragment5RC（片段5）：enzymeLociFlag = true 时，下拉框必选,不能为空
	 * fragment5Sites:enzymeLociFlag = true 时，不能为空
	 * fragment3RC（片段3）：enzymeLociFlag = true 时，下拉框必选，必须为整型数据
	 * fragment3Sites:enzymeLociFlag = true 时，不能为空
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * promptMessage：提示消息，弹出提示框，只有确认按钮，不影响程序后续操作
	 * PrimerFvalue：返回第一行text的值
	 * PrimerFbefore：返回第一行text前端的值
	 * PrimerFafter：返回第一行text后端的值
	 * PrimerFabove1：返回第一行text上面第1段的内容
	 * PrimerFabove2：返回第一行text上面第2段的内容
	 * 
	 * PrimerRvalue：返回第二行text的值
	 * PrimerRbefore：返回第二行text前端的值
	 * PrimerRafter：返回第二行text后端的值
	 * PrimerRabove1：返回第二行text上面第1段的内容
	 * PrimerRabove2：返回第二行text上面第2段的内容
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> EntryClone(SimpleCloneEntity simpleClone){
		@SuppressWarnings("unchecked")
		Map<String,Object> result = new HashMap(); 
		result.put("success", "false");
		
		//检查sequence不能为空
		if(StringUtils.isEmpty(simpleClone.getSequence())){
			result.put("message", "sequence不能为空");
			return result;
		}
		String enzymeLociFlag = simpleClone.getEnzymeLociFlag();
		//检查enzymeLociFlag不能为空，和不能为其他数据
		if(StringUtils.isEmpty(enzymeLociFlag)){
			result.put("message", "enzymeLociFlag不能为空");
			return result;
		}
		
		if(enzymeLociFlag.equals("Y")){
			//检查fragment5不能为空
			if(StringUtils.isEmpty(simpleClone.getFragment5RC())){
				result.put("message", "fragment5RC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(simpleClone.getFragment5Sites())){
				result.put("message", "fragment5Sites不能为空");
				return result;
			}
			//检查fragment3不能为空
			if(StringUtils.isEmpty(simpleClone.getFragment3RC())){
				result.put("message", "fragment3Value不能为空");
				return result;
			}
			if(StringUtils.isEmpty(simpleClone.getFragment3Sites())){
				result.put("message", "fragment3Sites不能为空");
				return result;
			}
		} else if(!enzymeLociFlag.equals("N")){
			result.put("message", "enzymeLociFlag不能为N或Y以外的值");
			return result;
		}
		String sequenceStr = simpleClone.getSequence();
		int sequenceLength = sequenceStr.length();
		
		if(enzymeLociFlag.equals("N")){
			String tmp = "";
			Double Tm = 0d;
			for(int i=18; i <= sequenceLength; i++){
				tmp = sequenceStr.substring(0,i);
				Tm = Double.parseDouble(Tm_Cal(tmp));
				if(Tm >= 60 ){
					break;
				}
			}
			String PrimerF = "ggatcttccagagat"+tmp;
			result.put("PrimerFvalue", PrimerF);
			result.put("PrimerFbefore", "5'-");
			result.put("PrimerFafter", "-3'");
			result.put("PrimerFabove1", "CE Entry正向扩增引物序列为（小写序列为5'端添加的15 bp URS序列）：");
			result.put("PrimerFabove1_en", "The CE Entry forward amplification primer sequence (lowercase sequence is 15 bp URS sequence):");
			result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+Tm.toString()+"℃");
			result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+Tm.toString()+"℃");
			
			String seqRC = RC(sequenceStr);
			int seqRCLength = seqRC.length();
			tmp = "";
			Tm = 0d;
			for(int i=18;i<=seqRCLength;i++){
				tmp = seqRC.substring(0, i);
				Tm = Double.parseDouble(Tm_Cal(tmp));
				if(Tm >= 60 ){
					break;
				}
			}
			String PrimerR = "ctgccgttcgacgat"+tmp;
			result.put("PrimerRvalue", PrimerR);
			result.put("PrimerRbefore", "5'-");
			result.put("PrimerRafter", "-3'");
			result.put("PrimerRabove1", "CE Entry反向扩增引物序列为（小写序列为5'端添加的15 bp DRS序列）：");
			result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+Tm.toString()+"℃");
			result.put("PrimerRabove1_en", "The CE Entry reverse amplification primer sequence (lowercase sequence is 15 bp DRS sequence): ");
			result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+Tm.toString()+"℃");
		}else{
			String m1 = simpleClone.getFragment5RC();
			String n1 = simpleClone.getFragment5Sites();
			
			String m2 = simpleClone.getFragment3RC();
			String n2 = simpleClone.getFragment3Sites();
			String tmp = "";
			Double Tm = 0d;
			for(int i=18; i <=sequenceLength; i++){
				tmp = sequenceStr.substring(0, i);
				Tm = Double.parseDouble(Tm_Cal(tmp));
				if(Tm >= 60 ){
					break;
				}
			}
			String PrimerF = "ggatcttccagagat"+n1.toLowerCase()+tmp;
			result.put("PrimerFvalue", PrimerF);
			result.put("PrimerFbefore", "5'-");
			result.put("PrimerFafter", "-3'");
			result.put("PrimerFabove1", "CE Entry正向扩增引物序列为（小写序列为15 bp URS序列+"+m1+"序列）：");
			result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+Tm.toString()+"℃");
			result.put("PrimerFabove1_en", "The CE Entry forward amplification primer sequence (lowercase sequence is 15 bp URS sequence + "+m1+" sequence):");
			result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+Tm.toString()+"℃");
			
			String seqRC = RC(sequenceStr);
			int seqRCLength = seqRC.length();
			tmp = "";
			Tm = 0d;
			for(int i=18;i<=seqRCLength;i++){
				tmp = seqRC.substring(0, i);
				Tm = Double.parseDouble(Tm_Cal(tmp));
				if(Tm >= 60 ){
					break;
				}
			}
			String PrimerR = "ctgccgttcgacgat"+n2.toLowerCase()+tmp;
			result.put("PrimerRvalue", PrimerR);
			result.put("PrimerRbefore", "5'-");
			result.put("PrimerRafter", "-3'");
			result.put("PrimerRabove1", "CE Entry反向扩增引物序列为（小写序列为15 bp DRS序列+"+m2+"序列）：");
			result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+Tm.toString()+"℃");
			result.put("PrimerRabove1_en", "The CE Entry reverse amplification primer sequence (lowercase sequence is 15 bp DRS sequence + "+m2+" sequence):");
			result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+Tm.toString()+"℃");
		}
		result.put("success", "true");
		return result;
	}
	
	/**
	 * 替换并计数
	 * @param beforeRC
	 * @return
	 */
	private String RC(String beforeRC){
		String seqR = "";
		String seqRC = "";
		int a = beforeRC.length();
		for(int i=a-1;i>=0;i--){
			seqR = seqR + beforeRC.substring(i, i+1);
		}
		a = seqR.length();
		
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
		return seqRC;
	}
	
	/**
	 * 计算输入的碱基对串并返回温度
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
}

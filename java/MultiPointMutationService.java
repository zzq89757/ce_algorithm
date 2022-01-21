package com.blueland.sys.log.service;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.model.PointMutationEntity;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 多、双点突变
 */
@Slf4j
@Service
public class MultiPointMutationService  {
	@SuppressWarnings("unused")
	@Autowired
	private SysOplogDao sysOplogDao;
	private String trimseq;
	private int Primer_Length;
	private int numA;
	private int numT;
	private int numG;
	private int numC;
	private String PrimerF;
	private String PrimerFTm;
	private String PrimerR;
	private String PrimerRTm;
	private String Spit5seq;
	private String Spit3seq;
	private int Spit5;
	private int Spit3;
	
	/**
	 * 多、双点突变
	 * @param params：
	 * mutUp（text1序列）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * mutDown（text3序列）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * mutSite：可以为空，如果不为空字段长度必须小于50，内容只包含大写ACGT四个字母
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
	public Map<String,Object> multiMutation(PointMutationEntity pointMutation){
		Map<String,Object> result = new HashMap();
		result.put("success", "false");
		
		if(StringUtils.isEmpty(pointMutation.getMutUp())){
			result.put("message", "mutUp不能为空");
			return result;
		}
		if(StringUtils.isEmpty(pointMutation.getMutDown())) {
			result.put("message", "mutDown不能为空");
			return result;
		}
		if(!StringUtils.isEmpty(pointMutation.getMutSite()) && pointMutation.getMutSite().length() > 50) {
			result.put("promptMessage","突变位点长度大于50 bp，应将其拆分为两个位点分别进行突变引物设计。");
			result.put("promptMessage_en","The length of the mutation site is over 50 bp, and should be treated as two mutation sites.");
			return result;
		}
		
		String Mut5 = pointMutation.getMutUp().toUpperCase();
		String Mut3 = pointMutation.getMutDown().toUpperCase();
		String Mut = "";
		if(!StringUtils.isEmpty(pointMutation.getMutSite())) {
			Mut = pointMutation.getMutSite().toUpperCase();
		}
		
		InterCal(Mut5,Mut3,Mut,17);
		PrimerDesign(Mut3);
		String OuterFTm = PrimerFTm;
		result.put("PrimerFvalue", Spit5seq + PrimerF);
		result.put("PrimerFbefore", "5'-");
		result.put("PrimerFafter", "- 3'");
		result.put("PrimerFabove1", "该突变位点的正向扩增引物序列为：");
		result.put("PrimerFabove1_en", "The forward amplification primer sequence for this mutation site:");
		
		PrimerDesign(Mut5);
		result.put("PrimerRvalue", Spit3seq + PrimerR);
		result.put("PrimerRbefore", "5'-");
		result.put("PrimerRafter", "- 3'");
		result.put("PrimerRabove1", "该突变位点的反向扩增引物序列为：");
		result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+PrimerRTm+"℃");
		result.put("PrimerRabove1_en", "The reverse amplification primer sequence for this mutation site:");
		result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+PrimerRTm+"℃");
		
		//计算同源臂
		String strMiddleHomo = CalMiddleHomo(result.get("PrimerFvalue").toString().toUpperCase(),result.get("PrimerRvalue").toString().toUpperCase());
		sequence(strMiddleHomo);
		Double GCRateMiddleHomo = 0d;
		if(!StringUtils.isEmpty(strMiddleHomo)) {
			GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateMiddleHomo);
			GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		}		
		
		result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+OuterFTm+"℃   同源臂长度："+strMiddleHomo.length()+"bp   同源臂GC含量："+GCRateMiddleHomo.toString()+"%   酶切位点：无");		
		result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+OuterFTm+"℃   Homologous Sequences Length:"+strMiddleHomo.length()+"bp   GC Content Of Homologous Sequences:"+GCRateMiddleHomo.toString()+"%   Restriction Enzyme Cutting Site:None");
		
		sequence(result.get("PrimerFvalue").toString().substring(0, 17));
		if(((double)(numA) + (double)(numT)) / 17d > 0.7d) {
			result.put("promptMessage","注意：正反向引物互补区域GC含量小于30%，重组环化效率可能会有所下降！");
			result.put("promptMessage_en","Note: The GC content of the complementary region of the forward and reverse primers is less than 30%, and the efficiency of recombination cyclization may decrease!");
		}else if(((double)(numA) + (double)(numT)) / 17d < 0.3d) {
			result.put("promptMessage","注意：正反向引物互补区域GC含量大于70%，重组环化效率可能会有所下降！");
			result.put("promptMessage_en","Note: The GC content of the complementary region of the forward and reverse primers is more than 70%, and the efficiency of recombination cyclization may decrease!");
		}
		
		result.put("success", "true");
		return result;
	}
	
	/*
	 * 计算获得同源臂字串
	 */
	private String CalMiddleHomo(String strRTBR,String strRTBF) {
		String resultStr = "";
		String strSeqRC = RC(strRTBF);
		if(strRTBR.length() <= strSeqRC.length()) {
			for(int i=0;i<strRTBR.length();i++) {
				String strTmpR = strRTBR.substring(0, strRTBR.length()-i);
				String strTmpF = strSeqRC.substring(strSeqRC.length()-strRTBR.length()+i, strSeqRC.length());
				if(strTmpF.equalsIgnoreCase(strTmpR)) {
					resultStr = strTmpF;
					break;
				}
			}
		}else {
			for(int i=0;i<strSeqRC.length();i++) {
				String strTmpR = strRTBR.substring(0, strSeqRC.length()-i);
				String strTmpF = strSeqRC.substring(i, strSeqRC.length());
				if(strTmpF.equalsIgnoreCase(strTmpR)) {
					resultStr = strTmpF;
					break;
				}
			}
		}
		return resultStr;
	}
	
	private void InterCal(String Seq1,String Seq2,String SeqM,int Length) {
		String SeqCombine = Seq1 + SeqM + Seq2;
		Double[] GC = new Double[0];
		int NiceGCNum = 0;
		
		//保证突变位点两个序列可以平均
		if((int)Math.abs(SeqM.length()-Length)/2 != Math.abs(SeqM.length()-Length)/2d) {
			Length = Length + 1;
		}
		
		if(SeqM.length() >=Length) {
			int tmpInt = SeqM.length()-Length+1;
			for(int i=0;i<tmpInt;i++) {
				sequence(SeqM.substring(i, i+Length));
				GC = Arrays.copyOf(GC, i+1);
				GC[i] = ((double) (numC) + (double) (numG)) / ((double) (numA) + (double) (numT) + (double) (numC) + (double) (numG));
			}
			for(int i=0;i<GC.length;i++) {			//'优先GC含量，再次满足离中线最近的组合
				if(Math.abs(GC[i]-0.5d) < Math.abs(GC[NiceGCNum]-0.5d)) {
					NiceGCNum = i;
				}else if(Math.abs(GC[i]-0.5d) == Math.abs(GC[NiceGCNum]-0.5d)) {
					if(Math.abs(i+1 - (GC.length+1d)/2d) < Math.abs(NiceGCNum+1 - (GC.length+1d)/2d)) {
						NiceGCNum = i;
					}
				}
			}
			NiceGCNum = NiceGCNum+1;
			Spit5 = SeqM.length() - NiceGCNum+1;
			Spit3 = SeqM.length() + Length - Spit5;
			Spit5seq = SeqM.substring(SeqM.length() - Spit5).toLowerCase();
			Spit3seq = RC(SeqM.substring(0, Spit3)).toLowerCase();
		}else {
			int tmpInt = Length-SeqM.length()+1;
			for(int i=0;i<tmpInt;i++) {
				sequence(SeqCombine.substring(Seq1.length()-Length+SeqM.length()+i, Seq1.length()+SeqM.length()+i));
				GC = Arrays.copyOf(GC, i+1);
				GC[i] = ((double) (numC) + (double) (numG)) / ((double) (numA) + (double) (numT) + (double) (numC) + (double) (numG));
			}
			for(int i=0;i<GC.length;i++) {			//'优先GC含量，再次满足离中线最近的组合
				if(Math.abs(GC[i]-0.5d) < Math.abs(GC[NiceGCNum]-0.5d)) {
					NiceGCNum = i;
				}else if(Math.abs(GC[i]-0.5d) == Math.abs(GC[NiceGCNum]-0.5d)) {
					if(Math.abs(i+1 - (GC.length+1d)/2d) < Math.abs(NiceGCNum+1 - (GC.length+1d)/2d)) {
						NiceGCNum = i;
					}
				}
			}
			NiceGCNum = NiceGCNum+1;
			Spit5 = Length-SeqM.length()-NiceGCNum+1;
			Spit3 = Length-Spit5-SeqM.length();
			Spit5seq = Seq1.substring(Seq1.length()-Spit5, Seq1.length())+SeqM.toLowerCase();
			String seqTmp = RC(SeqM+Seq2.substring(0, Spit3));
			Spit3seq = seqTmp.substring(0, Spit3) + seqTmp.substring(seqTmp.length()-SeqM.length()).toLowerCase();
		}
	}
	
	private void PrimerDesign(String Insertion) {
		String a = "";
		String b = "";
		String Tm = "";
		for(int i=18;i<=Insertion.length();i++) {
			a = Insertion.substring(0, i);
			Tm = Tm_Cal(a);
			if(Double.parseDouble(Tm) >=60) {
				break;
			}
		}
		PrimerF = a;
		PrimerFTm = Tm.toString();
		Tm = "";
		String seqRC = RC(Insertion);
		for(int i=18;i<=seqRC.length();i++) {
			b = seqRC.substring(0, i);
			Tm = Tm_Cal(b);
			if(Double.parseDouble(Tm) >=60) {
				break;
			}
		}
		PrimerR = b;
		PrimerRTm = Tm;
	}
	
	/**
	 * 计算输入的碱基对串并返回温度
	 * @param Oligo
	 * @return
	 */
	@SuppressWarnings("deprecation")
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
	 * 检查输入字段是否符合基本要求
	 * @param params
	 * @return
	 */
	private Map<String,Object> checkField(Map<String,String> params){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		if(params.get("mutUp")==null||StringUtils.isEmpty(params.get("mutUp"))){
			result.put("success", "false");
			result.put("message", "mutUp不能为空");
			return result;
		}
		if(params.get("mutDown")==null||StringUtils.isEmpty(params.get("mutDown"))) {
			result.put("success", "false");
			result.put("message", "mutDown不能为空");
			return result;
		}
		if(!StringUtils.isEmpty(params.get("mutSite").toString()) && params.get("mutSite").length() > 50) {
			result.put("success", "false");
			result.put("message", "突变位点长度大于50 bp，应将其拆分为两个位点分别进行突变引物设计");
			return result;
		}
		result.put("success", "true");
		return result;
	}
}

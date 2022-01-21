package com.blueland.sys.log.service;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.model.SingleFragmentCloneEntity;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 单片段克隆
 */
@Slf4j
@Service
public class SingleFragmentCloneService  {
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
	
	/**
	 * 单片段克隆
	 * @param params
	 * selectFlag（选择类型）：不能为空，选项为：单酶“Single”；双酶“Double”；PCR扩增“PCR”
	 * selectFlag=Single：
	 * vectorseq（text1序列）：字符串长度大于36，内容只包含大写ACGT四个字母
	 * insertseq（text3序列）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * fragmentRC：下拉框必选，不能为空；
	 * fragmentSites：下拉框必选，不能为空；
	 * 
	 * selectFlag=Double：
	 * vectorseq（text1序列）：字符串长度大于42，内容只包含大写ACGT四个字母
	 * insertseq（text3序列）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * fragment5RC（片段5）：下拉框必选,不能为空
	 * fragment5Sites：不能为空
	 * fragment3RC（片段3））：下拉框必选,不能为空
	 * fragment3Sites：不能为空
	 * 
	 * selectFlag=PCR：
	 * vectorseqU（text1序列）：字符串长度大于20，内容只包含大写ACGT四个字母
	 * vectorseqD（text2序列）：字符串长度大于20，内容只包含大写ACGT四个字母
	 * insertseq（text3序列）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * 
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * promptMessage：提示消息，弹出提示框，只有确认按钮，不影响程序后续操作
	 * 
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
	 * 
	 * ClonePrimerFvalue：返回第一行text的值
	 * ClonePrimerFbefore：返回第一行text前端的值
	 * ClonePrimerFafter：返回第一行text后端的值
	 * ClonePrimerFabove1：返回第一行text上面第1段的内容
	 * ClonePrimerFabove2：返回第一行text上面第2段的内容
	 * 
	 * ClonePrimerRvalue：返回第二行text的值
	 * ClonePrimerRbefore：返回第二行text前端的值
	 * ClonePrimerRafter：返回第二行text后端的值
	 * ClonePrimerRabove1：返回第二行text上面第1段的内容
	 * ClonePrimerRabove2：返回第二行text上面第2段的内容
	 */
	public Map<String,Object> singleClone(SingleFragmentCloneEntity singleFragmentClone){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		result.put("success", "false");
		
		Map<String,Object> checkMap = checkField(singleFragmentClone);
		if(!checkMap.get("success").toString().equals("true")) {
			result.put("message", checkMap.get("message").toString());
			return result;
		}
		String m = "";
		String n = "";
		String m1 = "";
		String n1 = "";
		String Com5RC = "";
		String Com3RC = "";
		int RC3 = 0;
		int RC5 = 0;
		String HomoF = "";
		String HomoR = "";		
				
		String selectFlagStr = singleFragmentClone.getSelectFlag();
		if(selectFlagStr.equals("Single")) {			//单酶切线性化载体实验方案开始
			String Vectorseq = singleFragmentClone.getVectorseq();
			String VectorseqRC = RC(Vectorseq);
			String Insertseq = singleFragmentClone.getInsertseq();
//			String InsertseqRC = RC(Insertseq);
			
			//把酶切位点名字赋值给m
			m = singleFragmentClone.getFragmentRC();
			//把酶切位点序列赋值给n
			n = singleFragmentClone.getFragmentSites();
			//调用RCCF sub拆分酶切位点，赋值给数组
			String[] sRC = RCCF(n);
			
			int[] RCcount = new int[0];
			int[] RCcountRC = new int[0];
			int[] RCcountRCTrim = new int[0];
			//把sRC()里的每个值进行查找，记录总共发现的次数以及最后一次出现的位置
			for(int k=0;k<sRC.length;k++) {
				//先在vectorseq中找第一轮
				//得到的结果是：发现次数 ubound(RCcount),最后一次发现位置 RCcount(ubound(RCcount))
				int i = 1;
				int j = 0;
				while(Vectorseq.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcount.length;
					RCcount = Arrays.copyOf(RCcount, j+1);
					RCcount[j] = Vectorseq.indexOf(sRC[k].toString(), i)+1;		//sRC标记位置+1
					i = Vectorseq.indexOf(sRC[k].toString(), i) + 2;			//i标记sRC标记位移位子+2
				}
				//再在vectorseqRC中找第二轮
				//得到的结果是：发现次数 ubound(RCcountRC),最后一次发现位置 RCcountRC(ubound(RCcountRC))
				i = 1;
				while(VectorseqRC.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcountRC.length;
					RCcountRC = Arrays.copyOf(RCcountRC, j+1);
					RCcountRC[j] = VectorseqRC.indexOf(sRC[k].toString(), i)+1;
					i = VectorseqRC.indexOf(sRC[k].toString(), i) + 2;
				}			
			}			

			//以RCcount为模板将RCcountRC中回文序列识别的区域删除
			for(int i=0;i<RCcountRC.length;i++) {
				for(int j=0;j<RCcount.length;j++) {
					if((Vectorseq.length() - RCcountRC[i] - RCcount[j] + 2) == n.length()) {
						RCcountRC[i] = 0;
					}
				}
			}
			
			//将RCcountRC中出了0以外的空值去掉，所以值转移至RCcountRCTrim数组中
			for(int i=0;i<RCcountRC.length;i++) {
				if(RCcountRC[i] > 0) {
					int j = RCcountRCTrim.length;
					RCcountRCTrim = Arrays.copyOf(RCcountRCTrim, j+1);
					RCcountRCTrim[j] = RCcountRC[i];
				}
			}
			
			//判断酶切位点是否唯一
			if((RCcount.length + RCcountRCTrim.length) == 0) { 
				result.put("success", "false");
				result.put("promptMessage", "所提供载体序列中不存在选定酶切位点，请确认后重新选择");
				result.put("promptMessage_en", "The selected restriction enzyme cleavage site is not present in the provided vector sequence, please re-select after confirmation.");
				return result;
			}else if((RCcount.length + RCcountRCTrim.length) > 1) {			//判断酶切位点是否唯一
				//提示消息
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "酶切位点在所提供载体序列中共计出现"+(RCcount.length+RCcountRCTrim.length)+"次，最外侧的两个酶切位点之间的序列将被切除");
					result.put("promptMessage_en", "The recognition site of the selected restriction enzyme exists "+(RCcount.length+RCcountRCTrim.length)+" times in the given vector sequence. Sequences between the most lateral-two recognition sites would be excised");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n酶切位点在所提供载体序列中共计出现"+(RCcount.length+RCcountRCTrim.length)+"次，最外侧的两个酶切位点之间的序列将被切除");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nThe recognition site of the selected restriction enzyme exists "+(RCcount.length+RCcountRCTrim.length)+" times in the given vector sequence. Sequences between the most lateral-two recognition sites would be excised");
				}
				
				int Most5 = Vectorseq.length();
				int Most3 = 0;
				//找最左边和最右边的酶切位点位置
				for(int i=0;i<RCcount.length;i++) {
					if(RCcount[i] < Most5) {
						Most5 = RCcount[i];
					}else if(RCcount[i] > Most3) {
						Most3 = RCcount[i];
					}
				}
				for(int i=0;i<RCcountRCTrim.length;i++) {
					if((Vectorseq.length()-RCcountRCTrim[i]-n.length()+2) < Most5) {
						Most5 = Vectorseq.length()-RCcountRCTrim[i]-n.length()+2;
					}else if((Vectorseq.length()-RCcountRCTrim[i]-n.length()+2) > Most3) {
						Most3 = Vectorseq.length()-RCcountRCTrim[i]-n.length()+2;
					}
				}
				HomoF = Vectorseq.substring(Most5-15-1, Most5+n.length()-1);
				HomoR = VectorseqRC.substring(VectorseqRC.length()-Most3-n.length()+1-15, VectorseqRC.length()-Most3+1);
			}else if((RCcount.length + RCcountRCTrim.length)==1) {		//选择同源序列赋值给HomoF,HomoR
				if(RCcount.length == 1) {
					if((RCcount[0]-15-1)>0 && (VectorseqRC.length()-RCcount[0]-n.length()+1-15)>0) {
						HomoF = Vectorseq.substring(RCcount[0]-15-1, RCcount[0]+n.length()-1);
						HomoR = VectorseqRC.substring(VectorseqRC.length()-RCcount[0]-n.length()+1-15, VectorseqRC.length()-RCcount[0]+1);
					}else {
						result.put("success", "false");
						result.put("promptMessage", "载体克隆位点上游或下游碱基不足20 bp，请重新输入");
						result.put("promptMessage_en", "The upstream or downstream bases of the vector cloning site is less than 20 bp. Please re-enter.");
						return result;
					}
				}else if(RCcountRCTrim.length == 1) {
					if((RCcountRCTrim[0]-15-1)>0 && (Vectorseq.length()-RCcountRCTrim[0]-n.length()+1-15)>0) {
						HomoF = Vectorseq.substring(Vectorseq.length()-RCcountRCTrim[0]-n.length()+1-15, Vectorseq.length()-RCcountRCTrim[0]+1);
						HomoR = VectorseqRC.substring(RCcountRCTrim[0]-15-1, RCcountRCTrim[0]+n.length()-1);
					}
				}else {
					result.put("success", "false");
					result.put("promptMessage", "载体克隆位点上游或下游碱基不足20 bp，请重新输入");
					result.put("promptMessage_en", "The upstream or downstream bases of the vector cloning site is less than 20 bp. Please re-enter.");
					return result;
				}
			}
			
			sequence(HomoF.substring(0, 15));
			if(((numA+numT)/15d) > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if(((numA+numT)/15d) < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateF = (numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateF);
			GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			sequence(HomoR.substring(0, 15));
			if(((numA+numT)/15d) > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段3'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段3'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if(((numA+numT)/15d) < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段3'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段3'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateR = (numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateR);
			GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			PrimerDesign(Insertseq);
			result.put("PrimerFvalue", HomoF.toLowerCase()+PrimerF.toUpperCase());
			result.put("PrimerFbefore", "5'-");
			result.put("PrimerFafter", "-3'");
			result.put("PrimerFabove1", "插入片段CE II正向扩增引物序列为（小写序列为5'端添加的重组序列）：");
			result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+PrimerFTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点："+n);
			result.put("PrimerFabove1_en", "The CE II forward amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:"+n);
			
			result.put("PrimerRvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("PrimerRbefore", "5'-");
			result.put("PrimerRafter", "-3'");
			result.put("PrimerRabove1", "插入片段CE II反向扩增引物序列为（小写序列为5'端添加的重组序列）：");
			result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+n);
			result.put("PrimerRabove1_en", "The CE II reverse amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+n);
			
		}else if(selectFlagStr.equals("Double")) {		//双酶切线性化载体实验方案开始
			String Vectorseq = singleFragmentClone.getVectorseq();
			String VectorseqRC = RC(Vectorseq);
			String Insertseq = singleFragmentClone.getInsertseq();
//			String InsertseqRC = RC(Insertseq);
			
			m = singleFragmentClone.getFragment5RC();				//把酶切位点名字赋值给m
			n = singleFragmentClone.getFragment5Sites();			//把酶切位点序列赋值给n
			Com5RC = singleFragmentClone.getFragment5Sites();
			
			m1 = singleFragmentClone.getFragment3RC();
			n1 = singleFragmentClone.getFragment3Sites();
			Com3RC = singleFragmentClone.getFragment3Sites();
			
			if(m == m1 || n == n1) {
				result.put("success", "false");
				result.put("promptMessage", "5'端线性化内切酶与3'端线性化内切酶识别位点相同，请重新选择");
				result.put("promptMessage_en", "The restriction enzyme selected for linearization at 5'-end is identical to the one at the 3'-end, please re-select.");
				return result;
			}
			
			//计算5'端同源臂
			String[] sRC = RCCF(n);			//调用RCCF sub拆分酶切位点，赋值给数组
			int[] RCcount = new int[0];
			int[] RCcountRC = new int[0];
			int[] RCcountRCTrim = new int[0];
			
			//把sRC()里的每个值进行查找，记录总共发现的次数以及最后一次出现的位置
			for(int k=0;k<sRC.length;k++) {
				//先在vectorseq中找第一轮
				//得到的结果是：发现次数 ubound(RCcount),最后一次发现位置 RCcount(ubound(RCcount))
				int i = 1;
				int j = 0;
				while(Vectorseq.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcount.length;
					RCcount = Arrays.copyOf(RCcount, j+1);
					RCcount[j] = Vectorseq.indexOf(sRC[k].toString(), i)+1;
					i = Vectorseq.indexOf(sRC[k].toString(), i) + 2;
				}
				//再在vectorseqRC中找第二轮
				//得到的结果是：发现次数 ubound(RCcountRC),最后一次发现位置 RCcountRC(ubound(RCcountRC))
				i = 1;
				while(VectorseqRC.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcountRC.length;
					RCcountRC = Arrays.copyOf(RCcountRC, j+1);
					RCcountRC[j] = VectorseqRC.indexOf(sRC[k].toString(), i)+1;
					i = VectorseqRC.indexOf(sRC[k].toString(), i) + 2;
				}			
			}
			
			//以RCcount为模板将RCcountRC中回文序列识别的区域删除
			for(int i=0;i<RCcountRC.length;i++) {
				for(int j=0;j<RCcount.length;j++) {
					if((Vectorseq.length() - RCcountRC[i] - RCcount[j] + 2) == n.length()) {
						RCcountRC[i] = 0;
					}
				}
			}
			
			//将RCcountRC中出了0以外的空值去掉，所以值转移至RCcountRCTrim数组中
			for(int i=0;i<RCcountRC.length;i++) {
				if(RCcountRC[i] > 0) {
					int j = RCcountRCTrim.length;
					RCcountRCTrim = Arrays.copyOf(RCcountRCTrim, j+1);
					RCcountRCTrim[j] = RCcountRC[i];
				}
			}
			
			/**
			 * 标注被选择中的颜色  ，代码没有被执行
			 */
			
			if((RCcount.length+RCcountRCTrim.length) > 1) {		//判断酶切位点是否唯一
				result.put("success", "false");
				result.put("promptMessage", "与插入片段5'端接壤的酶切位点在所提供载体序列中不唯一，请确认后重新输入");
				result.put("promptMessage_en", "The restriction enzyme cleavage site that borders the 5' end of the insert is non-unique in the vector sequence inputed,please recheck.");
				return result;
			}else if((RCcount.length+RCcountRCTrim.length) == 0) {
				result.put("success", "false");
				result.put("promptMessage", "与插入片段5'端接壤的酶切位点在所提供载体序列中不存在，请确认后重新选择");
				result.put("promptMessage_en", "The restriction enzyme cleavage site that borders the 5' end of the insert is non-existent in the vector sequence inputed,please recheck.");
				return result;
			}else if((RCcount.length+RCcountRCTrim.length) == 1) {
				if(RCcount.length == 1) {
					RC5 = RCcount[0];
				}else if(RCcountRCTrim.length == 1) {
					RC5 = Vectorseq.length()-RCcountRCTrim[0]-n.length()+2;
				}
			}
			//计算5'端同源臂结束
			
			//计算3'端同源臂
			sRC = RCCF(n1);
			RCcount = new int[0];
			RCcountRC = new int[0];
			RCcountRCTrim = new int[0];
			
			//把sRC()里的每个值进行查找，记录总共发现的次数以及最后一次出现的位置
			for(int k=0;k<sRC.length;k++) {
				//先在vectorseq中找第一轮
				//得到的结果是：发现次数 ubound(RCcount),最后一次发现位置 RCcount(ubound(RCcount))
				int i = 1;
				int j = 0;
				while(Vectorseq.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcount.length;
					RCcount = Arrays.copyOf(RCcount, j+1);
					RCcount[j] = Vectorseq.indexOf(sRC[k].toString(), i)+1;
					i = Vectorseq.indexOf(sRC[k].toString(), i) + 2;
				}
				//再在vectorseqRC中找第二轮
				//得到的结果是：发现次数 ubound(RCcountRC),最后一次发现位置 RCcountRC(ubound(RCcountRC))
				i = 1;
				while(VectorseqRC.indexOf(sRC[k].toString(), i)!=-1) {
					j = RCcountRC.length;
					RCcountRC = Arrays.copyOf(RCcountRC, j+1);
					RCcountRC[j] = VectorseqRC.indexOf(sRC[k].toString(), i)+1;
					i = VectorseqRC.indexOf(sRC[k].toString(), i) + 2;
				}			
			}
			
			//以RCcount为模板将RCcountRC中回文序列识别的区域删除
			for(int i=0;i<RCcountRC.length;i++) {
				for(int j=0;j<RCcount.length;j++) {
					if((Vectorseq.length() - RCcountRC[i] - RCcount[j] + 2) == n1.length()) {
						RCcountRC[i] = 0;
					}
				}
			}
			
			//将RCcountRC中出了0以外的空值去掉，所以值转移至RCcountRCTrim数组中
			for(int i=0;i<RCcountRC.length;i++) {
				if(RCcountRC[i] > 0) {
					int j = RCcountRCTrim.length;
					RCcountRCTrim = Arrays.copyOf(RCcountRCTrim, j+1);
					RCcountRCTrim[j] = RCcountRC[i];
				}
			}
			
			/**
			 * 标注被选择中的颜色  ，代码没有被执行
			 */
			
			if((RCcount.length+RCcountRCTrim.length) > 1) {		//判断酶切位点是否唯一
				result.put("success", "false");
				result.put("promptMessage", "与插入片段3'端接壤的酶切位点在所提供载体序列中不唯一，请确认后重新输入");
				result.put("promptMessage_en", "The restriction enzyme cleavage site that borders the 3' end of the insert is non-unique in the vector sequence inputed,please recheck.");
				return result;
			}else if((RCcount.length+RCcountRCTrim.length) == 0) {
				result.put("success", "false");
				result.put("promptMessage", "与插入片段3'端接壤的酶切位点在所提供载体序列中不存在，请确认后重新选择");
				result.put("promptMessage_en", "The restriction enzyme cleavage site that borders the 3' end of the insert is non-existent in the vector sequence inputed,please recheck.");
				return result;
			}else if((RCcount.length+RCcountRCTrim.length) == 1) {			//把找到的3'端的酶切位点赋值给变量RC3（位置矫正到正义链）
				if(RCcount.length == 1) {
					RC3 = RCcount[0];
				}else if(RCcountRCTrim.length == 1) {
					RC3 = Vectorseq.length()-RCcountRCTrim[0]-n1.length()+2;
				}
			}
			//计算3'端同源臂结束
			
			//判断载体序列是否为顺序
			if(RC5 < RC3) {
				if((RC5-15-1) > 0 && (Vectorseq.length()-RC3-Com3RC.length()+1-15) > 0) {
					HomoF = Vectorseq.substring(RC5-15-1, RC5+Com5RC.length()-1);
					HomoR = VectorseqRC.substring(VectorseqRC.length()-RC3-Com3RC.length()+1-15, VectorseqRC.length()-RC3+1);
				}else {
					result.put("success", "false");
					result.put("promptMessage", "载体克隆位点上游或下游碱基不足20 bp，请重新输入");
					result.put("promptMessage_en", "The upstream or downstream bases of the vector cloning site is less than 20 bp. Please re-enter.");
					return result;
				}
			}else if(RC5 > RC3) {
				if((RC3-15-1) > 0 && (Vectorseq.length()-RC5-Com5RC.length()+1-15) > 0) {
					HomoR = Vectorseq.substring(RC3-15-1, RC3+Com3RC.length()-1);
					HomoF = VectorseqRC.substring(VectorseqRC.length()-RC5-Com5RC.length()+1-15, VectorseqRC.length()-RC5+1);
				}else {
					result.put("success", "false");
					result.put("promptMessage", "载体克隆位点上游或下游碱基不足20 bp，请重新输入");
					result.put("promptMessage_en", "The upstream or downstream bases of the vector cloning site is less than 20 bp. Please re-enter.");
					return result;
				}
			}
			
			sequence(HomoF.substring(0, 15));
			if((numA+numT)/15d > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if((numA+numT)/15d < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateF = Double.valueOf(numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateF);
			GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			sequence(HomoR.substring(0, 15));
			if((numA+numT)/15d > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段3'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段3'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if((numA+numT)/15d < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段3'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段3'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateR = Double.valueOf(numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateR);
			GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			PrimerDesign(Insertseq);
			result.put("PrimerFvalue", HomoF.toLowerCase()+PrimerF.toUpperCase());
			result.put("PrimerFbefore", "5'-");
			result.put("PrimerFafter", "-3'");
			result.put("PrimerFabove1", "插入片段CE II正向扩增引物序列为（小写序列为5'端添加的重组序列）：");
			result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+PrimerFTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点："+Com5RC);
			result.put("PrimerFabove1_en", "The CE II forward amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:"+Com5RC);
			
			result.put("PrimerRvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("PrimerRbefore", "5'-");
			result.put("PrimerRafter", "-3'");
			result.put("PrimerRabove1", "插入片段CE II反向扩增引物序列为（小写序列为5'端添加的重组序列）：");
			result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+Com3RC);
			result.put("PrimerRabove1_en", "The CE II reverse amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+Com3RC);
			
		}else if(selectFlagStr.equals("PCR")) {		//载体PCR扩增实验方案开始
			String VectorSeqU = singleFragmentClone.getVectorseqU();
			String VectorSeqD = singleFragmentClone.getVectorseqD();
			String Insertseq = singleFragmentClone.getInsertseq();
			
			HomoF = VectorSeqU.substring(VectorSeqU.length()-20, VectorSeqU.length());
			HomoR = RC(VectorSeqD.substring(0, 20));
			
			sequence(HomoF.substring(0, 20));
			if((numA+numT)/20d > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if((numA+numT)/20d < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (5' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateF = Double.valueOf(numG+numC)/20d*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateF);
			GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			sequence(HomoR.substring(0, 20));
			if((numA+numT)/20d > 0.7d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量小于30%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is less than 30%, which may lead to the decrease in recombination efficiency.");
				}
			}else if((numA+numT)/20d < 0.3d) {
				if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
					result.put("promptMessage", "注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}else {
					result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：插入片段5'端重组序列GC含量大于70%，重组效率可能会有所下降！");
					result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (3' end of the insert) is more than 70%, which may lead to the decrease in recombination efficiency.");
				}
			}
			Double GCRateR = Double.valueOf(numG+numC)/20d*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateR);
			GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			PrimerDesign(Insertseq);		//设计插入片段扩增引物
			result.put("PrimerFvalue", HomoF.toLowerCase()+PrimerF.toUpperCase());
			result.put("PrimerFbefore", "5'-");
			result.put("PrimerFafter", "-3'");
			result.put("PrimerFabove1", "插入片段CE II正向扩增引物序列为（小写序列为5'端添加的重组序列）");
			result.put("PrimerFabove2", "基因特异性引物Tm计算值为："+PrimerFTm+"℃   同源臂长度："+HomoF.length()+"bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点：无");
			result.put("PrimerFabove1_en", "The CE II forward amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerFabove2_en", "Gene Specific Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+HomoF.length()+"bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:None");
			
			result.put("PrimerRvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("PrimerRbefore", "5'-");
			result.put("PrimerRafter", "-3'");
			result.put("PrimerRabove1", "插入片段CE II反向扩增引物序列为（小写序列为5'端添加的重组序列）：");
			result.put("PrimerRabove2", "基因特异性引物Tm计算值为："+PrimerRTm+"℃   同源臂长度："+HomoR.length()+"bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点：无");
			result.put("PrimerRabove1_en", "The CE II reverse amplification primer sequence of the insert (lowercase sequence is the recombination sequence added at the 5' end):");
			result.put("PrimerRabove2_en", "Gene Specific Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:"+HomoR.length()+"bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:None");
			
			PrimerDesign(VectorSeqD);
			result.put("ClonePrimerFvalue", PrimerF.toUpperCase());
			result.put("ClonePrimerFbefore", "5'-");
			result.put("ClonePrimerFafter", "-3'");
			result.put("ClonePrimerFabove1", "克隆载体正向扩增引物序列为：");
			result.put("ClonePrimerFabove2", "引物Tm计算值为："+PrimerFTm+"℃");
			result.put("ClonePrimerFabove1_en", "The forward amplification primer sequence of cloning vector:");
			result.put("ClonePrimerFabove2_en", "Primer Tm Value: "+PrimerFTm+"℃");
			
			PrimerDesign(VectorSeqU);
			result.put("ClonePrimerRvalue",PrimerR.toUpperCase());
			result.put("ClonePrimerRbefore", "5'-");
			result.put("ClonePrimerRafter", "-3'");
			result.put("ClonePrimerRabove1", "克隆载体反向扩增引物序列为：");
			result.put("ClonePrimerRabove2", "引物Tm计算值为："+PrimerRTm+"℃");
			result.put("ClonePrimerRabove1_en", "The reverse amplification primer sequence of cloning vector:");
			result.put("ClonePrimerRabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
		}
				
		result.put("success", "true");
		return result;
	}
	
	/**
	 * 把酶切位点序列传过来做位点拆分，拆分数据储存在sitestemp（）数组中
	 * @param e
	 */
	private String[] RCCF(String param) {
		String[] SitesTemp = new String[1];
		String[] sitestemp1 = new String[1];
		int paramLength = param.length();
		for(int a=0;a<paramLength;a++) {
			String tmpStr = param.substring(a,a+1);
			if(tmpStr.equals("A") || tmpStr.equals("G") || tmpStr.equals("C") || tmpStr.equals("T")) {
				for(int b=0;b<SitesTemp.length;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b] = tmpStr;
					}else {
						SitesTemp[b] = SitesTemp[b] + tmpStr;
					}					
				}
			}else if(tmpStr.equals("N")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*4);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*4] = "A";
						SitesTemp[b*4+1] = "G";
						SitesTemp[b*4+2] = "C";
						SitesTemp[b*4+3] = "T";
					}else {
						SitesTemp[b*4] = sitestemp1[b]+"A";
						SitesTemp[b*4+1] = sitestemp1[b]+"G";
						SitesTemp[b*4+2] = sitestemp1[b]+"C";
						SitesTemp[b*4+3] = sitestemp1[b]+"T";
					}
				}
			}else if(tmpStr.equals("V")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*3);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*3] = "A";
						SitesTemp[b*3+1] = "G";
						SitesTemp[b*3+2] = "C";
					}else {
						SitesTemp[b*3] = sitestemp1[b]+"A";
						SitesTemp[b*3+1] = sitestemp1[b]+"G";
						SitesTemp[b*3+2] = sitestemp1[b]+"C";
					}
				}
			}else if(tmpStr.equals("D")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*3);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*3] = "A";
						SitesTemp[b*3+1] = "G";
						SitesTemp[b*3+2] = "T";
					}else {
						SitesTemp[b*3] = sitestemp1[b]+"A";
						SitesTemp[b*3+1] = sitestemp1[b]+"G";
						SitesTemp[b*3+2] = sitestemp1[b]+"T";
					}					
				}
			}else if(tmpStr.equals("B")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*3);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*3] = "G";
						SitesTemp[b*3+1] = "C";
						SitesTemp[b*3+2] = "T";
					}else {
						SitesTemp[b*3] = sitestemp1[b]+"G";
						SitesTemp[b*3+1] = sitestemp1[b]+"C";
						SitesTemp[b*3+2] = sitestemp1[b]+"T";
					}					
				}
			}else if(tmpStr.equals("H")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*3);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*3] = "A";
						SitesTemp[b*3+1] = "C";
						SitesTemp[b*3+2] = "T";
					}else {
						SitesTemp[b*3] = sitestemp1[b]+"A";
						SitesTemp[b*3+1] = sitestemp1[b]+"C";
						SitesTemp[b*3+2] = sitestemp1[b]+"T";
					}
					
				}
			}else if(tmpStr.equals("W")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "A";
						SitesTemp[b*2+1] = "T";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"A";
						SitesTemp[b*2+1] = sitestemp1[b]+"T";
					}					
				}
			}else if(tmpStr.equals("S")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "G";
						SitesTemp[b*2+1] = "C";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"G";
						SitesTemp[b*2+1] = sitestemp1[b]+"C";
					}					
				}
			}else if(tmpStr.equals("K")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "G";
						SitesTemp[b*2+1] = "T";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"G";
						SitesTemp[b*2+1] = sitestemp1[b]+"T";
					}					
				}
			}else if(tmpStr.equals("M")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "A";
						SitesTemp[b*2+1] = "C";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"A";
						SitesTemp[b*2+1] = sitestemp1[b]+"C";
					}					
				}
			}else if(tmpStr.equals("Y")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "C";
						SitesTemp[b*2+1] = "T";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"C";
						SitesTemp[b*2+1] = sitestemp1[b]+"T";
					}
					
				}
			}else if(tmpStr.equals("R")) {
				int c = SitesTemp.length;
				sitestemp1 = new String[c];
				sitestemp1 = SitesTemp;
				SitesTemp = Arrays.copyOf(SitesTemp, c*2);
				for(int b=0;b<c;b++) {
					if(StringUtils.isEmpty(SitesTemp[b])) {
						SitesTemp[b*2] = "A";
						SitesTemp[b*2+1] = "G";
					}else {
						SitesTemp[b*2] = sitestemp1[b]+"A";
						SitesTemp[b*2+1] = sitestemp1[b]+"G";
					}
					
				}
			}
		}
		return SitesTemp;
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
	
	/**
	 * 检查输入字段是否符合基本要求
	 * @param params
	 * @return
	 */
	private Map<String,Object> checkField(SingleFragmentCloneEntity singleFragmentClone){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("success", "false");
		String selectFlagStr = singleFragmentClone.getSelectFlag();
		
		if(StringUtils.isEmpty(selectFlagStr)){
			result.put("message", "selectFlag不能为空");
			return result;
		}
		if(StringUtils.isEmpty(singleFragmentClone.getInsertseq())) {
			result.put("message", "insertseq不能为空");
			return result;
		}
		
		switch(selectFlagStr) {
		case "Single":
			if(StringUtils.isEmpty(singleFragmentClone.getVectorseq())) {
				result.put("message", "vectorseq不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragmentRC())) {
				result.put("message", "fragmentRC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragmentSites())) {
				result.put("message", "fragmentSites不能为空");
				return result;
			}
			break;
		case "Double":
			if(StringUtils.isEmpty(singleFragmentClone.getVectorseq())) {
				result.put("message", "vectorseq不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragment5RC())) {
				result.put("message", "fragment5RC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragment5Sites())) {
				result.put("message", "fragment5Sites不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragment3RC())) {
				result.put("message", "fragment3RC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getFragment3Sites())) {
				result.put("message", "fragment3Sites不能为空");
				return result;
			}
			break;
		case "PCR":
			if(StringUtils.isEmpty(singleFragmentClone.getVectorseqU())) {
				result.put("message", "vectorseqU不能为空");
				return result;
			}
			if(StringUtils.isEmpty(singleFragmentClone.getVectorseqD())) {
				result.put("message", "fragment5RC不能为空");
				return result;
			}
			break;
		default:
			result.put("message", "selectFlag类型错误");
			return result;
		}
		result.put("success", "true");
		return result;
	}
}

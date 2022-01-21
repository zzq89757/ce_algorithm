package com.blueland.sys.log.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.druid.util.StringUtils;
import com.blueland.model.MultiFragmentCloneEntity;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 多片段克隆
 */
@Slf4j
@Service
public class MultiFragmentCloneService  {
	
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
	/**
	 * 多片段克隆
	 * @param params
	 * selectFlag（选择类型）：不能为空，选项值为：单酶“Single”；双酶“Double”；PCR扩增“PCR”
	 * selectFlag=Single：
	 * vectorseq（text1序列）：字符串长度大于36，内容只包含大写ACGT四个字母
	 * fragmentRC：下拉框必选，不能为空；
	 * fragmentSites：下拉框必选，不能为空；
	 * 
	 * selectFlag=Double：
	 * vectorseq（text1序列）：字符串长度大于42，内容只包含大写ACGT四个字母
	 * fragment5RC（片段5）：下拉框必选,不能为空
	 * fragment5Sites：不能为空
	 * fragment3RC（片段3））：下拉框必选,不能为空
	 * fragment3Sites：不能为空
	 * 
	 * selectFlag=PCR：
	 * vectorseqU（text1序列）：字符串长度大于20，内容只包含大写ACGT四个字母
	 * vectorseqD（text2序列）：字符串长度大于20，内容只包含大写ACGT四个字母
	 *
	 * fragmentFlag（片段数量）：不能为空，选项值为：2、3、4、5，不同数字代表必须输入对应的插入序列数据（1~5）
	 * insertseq1（插入序列1）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * insertseq2（插入序列2）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * insertseq3（插入序列2）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * insertseq4（插入序列2）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * insertseq5（插入序列2）：字符串长度大于50，内容只包含大写ACGT四个字母
	 * 
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * promptMessage：提示消息，弹出提示框，只有确认按钮，不影响程序后续操作
	 * 
	 * RTB1Fvalue：返回第一行左边text的值
	 * RTB1Fbefore：返回第一行左边text前端的值
	 * RTB1Fafter：返回第一行左边text后端的值
	 * RTB1Fabove1：返回第一行左边text上面第1段的内容
	 * RTB1Fabove2：返回第一行左边text上面第2段的内容
	 * RTB1Rvalue：返回第一行右边text的值
	 * RTB1Rbefore：返回第一行右边text前端的值
	 * RTB1Rafter：返回第一行右边text后端的值
	 * RTB1Rabove1：返回第一行右边text上面第1段的内容
	 * RTB1Rabove2：返回第一行右边text上面第2段的内容
	 * 
	 * RTB2Fvalue：返回第二行左边text的值
	 * RTB2Fbefore：返回第二行左边text前端的值
	 * RTB2Fafter：返回第二行左边text后端的值
	 * RTB2Fabove1：返回第二行左边text上面第1段的内容
	 * RTB2Fabove2：返回第二行左边text上面第2段的内容
	 * RTB2Rvalue：返回第二行右边text的值
	 * RTB2Refore：返回第二行右边text前端的值
	 * RTB2Rafter：返回第二行右边text后端的值
	 * RTB2Rabove1：返回第二行右边text上面第1段的内容
	 * RTB2Rabove2：返回第二行右边text上面第2段的内容
	 * 
	 * RTB3Fvalue：返回第三行左边text的值
	 * RTB3Fbefore：返回第三行左边text前端的值
	 * RTB3Fafter：返回第三行左边text后端的值
	 * RTB3Fabove1：返回第三行左边text上面第1段的内容
	 * RTB3Fabove2：返回第三行左边text上面第2段的内容
	 * RTB3Rvalue：返回第三行右边text的值
	 * RTB3Refore：返回第三行右边text前端的值
	 * RTB3Rafter：返回第三行右边text后端的值
	 * RTB3Rabove1：返回第三行右边text上面第1段的内容
	 * RTB3Rabove2：返回第三行右边text上面第2段的内容
	 * 
	 * RTB4Fvalue：返回第四行左边text的值
	 * RTB4Fbefore：返回第四行左边text前端的值
	 * RTB4Fafter：返回第四行左边text后端的值
	 * RTB4Fabove1：返回第四行左边text上面第1段的内容
	 * RTB4Fabove2：返回第四行左边text上面第2段的内容
	 * RTB4Rvalue：返回第四行右边text的值
	 * RTB4Refore：返回第四行右边text前端的值
	 * RTB4Rafter：返回第四行右边text后端的值
	 * RTB4Rabove1：返回第四行右边text上面第1段的内容
	 * RTB4Rabove2：返回第四行右边text上面第2段的内容
	 * 
	 * RTB5Fvalue：返回第五行左边text的值
	 * RTB5Fbefore：返回第五行左边text前端的值
	 * RTB5Fafter：返回第五行左边text后端的值
	 * RTB5Fabove1：返回第五行左边text上面第1段的内容
	 * RTB5Fabove2：返回第五行左边text上面第2段的内容
	 * RTB5Rvalue：返回第五行右边text的值
	 * RTB5Refore：返回第五行右边text前端的值
	 * RTB5Rafter：返回第五行右边text后端的值
	 * RTB5Rabove1：返回第五行右边text上面第1段的内容
	 * RTB5Rabove2：返回第五行右边text上面第2段的内容
	 * 
	 * RTBVFvalue：返回第六行左边text的值
	 * RTBVFbefore：返回第六行左边text前端的值
	 * RTBVFafter：返回第六行左边text后端的值
	 * RTBVFabove1：返回第六行左边text上面第1段的内容
	 * RTBVFabove2：返回第六行左边text上面第2段的内容
	 * RTBVRvalue：返回第六行右边text的值
	 * RTBVRefore：返回第六行右边text前端的值
	 * RTBVRafter：返回第六行右边text后端的值
	 * RTBVRabove1：返回第六行右边text上面第1段的内容
	 * RTBVRabove2：返回第六行右边text上面第2段的内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> multiClone(MultiFragmentCloneEntity multiFragmentClone){
		Map<String,Object> result = new HashMap();
		Map<String,Object> checkMap = checkField(multiFragmentClone); 
		if(!checkMap.get("success").toString().equals("true")) {
			result.put("success", "false");
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
		
		String selectFlagStr = multiFragmentClone.getSelectFlag();
		if(selectFlagStr.equals("Single")) {
			String Vectorseq = multiFragmentClone.getVectorseq();
			String VectorseqRC = RC(Vectorseq);
			
			//把酶切位点名字赋值给m
			m = multiFragmentClone.getFragmentRC();
			//把酶切位点序列赋值给n
			n = multiFragmentClone.getFragmentSites();
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
		}else if(selectFlagStr.equals("Double")) {
			String Vectorseq = multiFragmentClone.getVectorseq();
			String VectorseqRC = RC(Vectorseq);
			
			m = multiFragmentClone.getFragment5RC();				//把酶切位点名字赋值给m
			n = multiFragmentClone.getFragment5Sites();			//把酶切位点序列赋值给n
			Com5RC = multiFragmentClone.getFragment5Sites();		
			m1 = multiFragmentClone.getFragment3RC();
			n1 = multiFragmentClone.getFragment3Sites();
			Com3RC = multiFragmentClone.getFragment3Sites();
			
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
		}else if(selectFlagStr.equals("PCR")) {				//载体PCR扩增实验方案开始
			String VectorSeqU = multiFragmentClone.getVectorseqU();
			String VectorSeqD = multiFragmentClone.getVectorseqD();
			
			HomoF = VectorSeqU.substring(VectorSeqU.length()-20, VectorSeqU.length());
			HomoR = RC(VectorSeqD.substring(0, 20));
			
			PrimerDesign(VectorSeqD);
			result.put("RTBVFvalue", PrimerF.toUpperCase());
			result.put("RTBVFbefore", "5'-");
			result.put("RTBVFafter", "- 3'");
			result.put("RTBVFabove1", "克隆载体正向扩增引物序列为：");
			result.put("RTBVFabove2", "引物Tm计算值为："+PrimerFTm+"℃");
			result.put("RTBVFabove1_en", "The forward amplification primer sequence of cloning vector:");
			result.put("RTBVFabove2_en", "Primer Tm Value: "+PrimerFTm+"℃");
			
			PrimerDesign(VectorSeqU);
			result.put("RTBVRvalue", PrimerR.toUpperCase());
			result.put("RTBVRbefore", "5'-");
			result.put("RTBVRafter", "- 3'");
			result.put("RTBVRabove1", "克隆载体反向扩增引物序列为：");
			result.put("RTBVRabove2", "引物Tm计算值为："+PrimerRTm+"℃");
			result.put("RTBVRabove1_en", "The reverse amplification primer sequence of cloning vector:");
			result.put("RTBVRabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
		}
		
		//以上程序将计算出来的载体正向同源臂赋值给HomoF，反向同源臂赋值给HomoR。如果载体是PCR扩增线性化的，将载体扩增引物输出到RTBVF和RTBVR中

		//下面的程序分别计算各个插入片段扩增引物，合并他们之间的HomoF和HomoR，输出引物序列至引物框中
		//根据不同的选择计算头尾同源臂长度及GC含量
		Double GCRateF = 0d;
		Double GCRateR = 0d;
		if(!selectFlagStr.equals("PCR")) {
			sequence(HomoF.substring(0, 15));
			GCRateF = Double.valueOf(numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateF);
			GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			sequence(HomoR.substring(0, 15));
			GCRateR = Double.valueOf(numG+numC)/15d*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateR);
			GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		}else {
			sequence(HomoF.substring(0, 20));
			GCRateF = Double.valueOf(numG+numC)/20d*100d;
			//换算GC含量百分比，并取小数点一位
			BigDecimal b = new BigDecimal(GCRateF);
			GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			sequence(HomoR.substring(0, 20));
			GCRateR = Double.valueOf(numG+numC)/20d*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateR);
			GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		
		if(GCRateF < 30d) {
			if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
				result.put("promptMessage", "注意：载体上游重组序列GC含量小于30%，重组效率可能会有所下降！");
				result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (upstream of the vector cloning site) is less than 30%, which may lead to the decrease in recombination efficiency.");
			}else {
				result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：载体上游重组序列GC含量小于30%，重组效率可能会有所下降！");
				result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (upstream of the vector cloning site) is less than 30%, which may lead to the decrease in recombination efficiency.");
			}
		}else if(GCRateF > 70d) {
			if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
				result.put("promptMessage", "注意：载体上游重组序列GC含量大于70%，重组效率可能会有所下降！");
				result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (upstream of the vector cloning site) is more than 70%, which may lead to the decrease in recombination efficiency.");
			}else {
				result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：载体上游重组序列GC含量大于70%，重组效率可能会有所下降！");
				result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (upstream of the vector cloning site) is more than 70%, which may lead to the decrease in recombination efficiency.");
			}
		}
//		Double GCRateF = Double.valueOf(numG+numC)/Double.valueOf(HomoF.length())*100d;
//		//换算GC含量百分比，并取小数点一位
//		BigDecimal b = new BigDecimal(GCRateF);
//		GCRateF = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		
//		sequence(HomoR.substring(0, 15));
		if(GCRateR < 30d) {
			if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
				result.put("promptMessage", "注意：载体下游重组序列GC含量小于30%，重组效率可能会有所下降！");
				result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (downstream of the vector cloning site) is less than 30%, which may lead to the decrease in recombination efficiency.");
			}else {
				result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：载体下游重组序列GC含量小于30%，重组效率可能会有所下降！");
				result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (downstream of the vector cloning site) is less than 30%, which may lead to the decrease in recombination efficiency.");
			}
		}else if(GCRateR > 70d) {
			if(result.get("promptMessage")==null||StringUtils.isEmpty(result.get("promptMessage").toString())) {
				result.put("promptMessage", "注意：载体下游重组序列GC含量大于70%，重组效率可能会有所下降！");
				result.put("promptMessage_en", "Note: The GC content of the recombinant sequence (downstream of the vector cloning site) is more than 70%, which may lead to the decrease in recombination efficiency.");
			}else {
				result.put("promptMessage", result.get("promptMessage").toString()+"\n注意：载体下游重组序列GC含量大于70%，重组效率可能会有所下降！");
				result.put("promptMessage_en", result.get("promptMessage_en").toString()+"\nNote: The GC content of the recombinant sequence (downstream of the vector cloning site) is more than 70%, which may lead to the decrease in recombination efficiency.");
			}
		}
//		Double GCRateR = Double.valueOf(numG+numC)/Double.valueOf(HomoR.length())*100d;
//		//换算GC含量百分比，并取小数点一位
//		b = new BigDecimal(GCRateR);
//		GCRateR = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		
		String Insertseq1Str = multiFragmentClone.getInsertseq1();
		String Insertseq2Str = multiFragmentClone.getInsertseq2();		
		InterCal(Insertseq1Str,Insertseq2Str,20);
//		PrimerDesign(Insertseq1Str);
		
		PrimerDesign(Insertseq1Str);
		result.put("RTB1Fvalue", HomoF.toLowerCase()+PrimerF.toUpperCase());
		result.put("RTB1Fbefore", "5'-");
		result.put("RTB1Fafter", "- 3'");
		result.put("RTB1Fabove1", "插入片段1正向扩增引物序列为：");
		result.put("RTB1Fabove1_en", "The forward amplification primer sequence of the insert 1:");
		if(selectFlagStr.equals("Single")) {
			result.put("RTB1Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点："+n);
			result.put("RTB1Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:"+n);
		}else if(selectFlagStr.equals("Double")) {
			result.put("RTB1Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点："+Com5RC);
			result.put("RTB1Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:"+Com5RC);
		}else if(selectFlagStr.equals("PCR")) {
			result.put("RTB1Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度："+HomoF.length()+"bp   同源臂GC含量："+GCRateF.toString()+"%   酶切位点：无");
			result.put("RTB1Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+HomoF.length()+"bp   GC Content Of Homologous Sequences:"+GCRateF.toString()+"%   Restriction Enzyme Cutting Site:None");
		}
				
		result.put("RTB1Rvalue", Spit3seq.toLowerCase()+PrimerR.toUpperCase());
		result.put("RTB1Rbefore", "5'-");
		result.put("RTB1Rafter", "- 3'");
		result.put("RTB1Rabove1", "插入片段1反向扩增引物序列为：");
		result.put("RTB1Rabove2", "引物Tm计算值为："+PrimerRTm+"℃");
		result.put("RTB1Rabove1_en", "The reverse amplification primer sequence of the insert 1:");
		result.put("RTB1Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
		
		PrimerDesign(Insertseq2Str);
		result.put("RTB2Fvalue", Spit5seq.toLowerCase()+PrimerF.toUpperCase());
		result.put("RTB2Fbefore", "5'-");
		result.put("RTB2Fafter", "- 3'");
		result.put("RTB2Fabove1", "插入片段2正向扩增引物序列为：");
		result.put("RTB2Fabove1_en", "The forward amplification primer sequence of the insert 2:");
		String strMiddleHomo = CalMiddleHomo(result.get("RTB1Rvalue").toString().toUpperCase(),Spit5seq.toUpperCase()+PrimerF.toUpperCase());
		sequence(strMiddleHomo);
		Double GCRateMiddleHomo = 0d;
		BigDecimal b = new BigDecimal(GCRateMiddleHomo);
		if((numG+numC) > 0 && strMiddleHomo.length() > 0) {
			GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
			//换算GC含量百分比，并取小数点一位
			b = new BigDecimal(GCRateMiddleHomo);
			GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
		}		
		result.put("RTB2Fabove2", "引物Tm计算值为："+PrimerFTm+"℃  同源臂长度："+strMiddleHomo.length()+"bp   同源臂GC含量："+GCRateMiddleHomo.toString()+"%   酶切位点：无");
		result.put("RTB2Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+strMiddleHomo.length()+"bp   GC Content Of Homologous Sequences:"+GCRateMiddleHomo.toString()+"%   Restriction Enzyme Cutting Site:None");
		
		result.put("RTB2Rvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
		result.put("RTB2Rbefore", "5'-");
		result.put("RTB2Rafter", "- 3'");
		result.put("RTB2Rabove1", "插入片段2反向扩增引物序列为：");
		result.put("RTB2Rabove1_en", "The reverse amplification primer sequence of the insert 2:");
		if(selectFlagStr.equals("Single")) {
			result.put("RTB2Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+n);		
			result.put("RTB2Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+n);
		}else if(selectFlagStr.equals("Double")) {
			result.put("RTB2Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+Com3RC);		
			result.put("RTB2Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+Com3RC);
		}else if(selectFlagStr.equals("PCR")) {
			result.put("RTB2Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度："+HomoR.length()+"bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点：无");		
			result.put("RTB2Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:"+HomoR.length()+"bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:None");
		}
		
		int fragmentFlagInt = Integer.parseInt(multiFragmentClone.getFragmentFlag());
		String Insertseq3Str = "";
		if(fragmentFlagInt > 2) {
			Insertseq3Str = multiFragmentClone.getInsertseq3();
			InterCal(Insertseq2Str,Insertseq3Str,20);
			result.put("RTB2Rvalue", Spit3seq.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB2Rbefore", "5'-");
			result.put("RTB2Rafter", "- 3'");
			result.put("RTB2Rabove1", "插入片段2反向扩增引物序列为：");
			result.put("RTB2Rabove2", "引物Tm计算值为："+PrimerRTm+"℃");
			result.put("RTB2Rabove1_en", "The reverse amplification primer sequence of the insert 2:");
			result.put("RTB2Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
			
			PrimerDesign(Insertseq3Str);
			result.put("RTB3Fvalue", Spit5seq.toLowerCase()+PrimerF.toUpperCase());
			result.put("RTB3Fbefore", "5'-");
			result.put("RTB3Fafter", "- 3'");
			result.put("RTB3Fabove1", "插入片段3正向扩增引物序列为：");
			result.put("RTB3Fabove1_en", "The forward amplification primer sequence of the insert 3:");
			strMiddleHomo = CalMiddleHomo(result.get("RTB2Rvalue").toString().toUpperCase(),Spit5seq.toUpperCase()+PrimerF.toUpperCase());
			sequence(strMiddleHomo);
//			GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
//			//换算GC含量百分比，并取小数点一位
//			b = new BigDecimal(GCRateMiddleHomo);
//			GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			GCRateMiddleHomo = 0d;
			if((numG+numC) > 0 && strMiddleHomo.length() > 0) {
				GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
				//换算GC含量百分比，并取小数点一位
				b = new BigDecimal(GCRateMiddleHomo);
				GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			result.put("RTB3Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度："+strMiddleHomo.length()+"bp   同源臂GC含量："+GCRateMiddleHomo.toString()+"%   酶切位点：无");			
			result.put("RTB3Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+strMiddleHomo.length()+"bp   GC Content Of Homologous Sequences:"+GCRateMiddleHomo.toString()+"%   Restriction Enzyme Cutting Site:None");
			
			result.put("RTB3Rvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB3Rbefore", "5'-");
			result.put("RTB3Rafter", "- 3'");
			result.put("RTB3Rabove1", "插入片段3反向扩增引物序列为：");
			result.put("RTB3Rabove1_en", "The reverse amplification primer sequence of the insert 3:");
			if(selectFlagStr.equals("Single")) {
				result.put("RTB3Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+n);			
				result.put("RTB3Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+n);
			}else if(selectFlagStr.equals("Double")) {
				result.put("RTB3Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+Com3RC);			
				result.put("RTB3Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+Com3RC);
			}else if(selectFlagStr.equals("PCR")) {
				result.put("RTB3Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度："+HomoR.length()+"bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点：无");			
				result.put("RTB3Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:"+HomoR.length()+"bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:None");
			}
		}
		
		String Insertseq4Str = "";
		if(fragmentFlagInt > 3) {
			Insertseq4Str = multiFragmentClone.getInsertseq4();
			InterCal(Insertseq3Str,Insertseq4Str,20);
			result.put("RTB3Rvalue", Spit3seq.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB3Rbefore", "5'-");
			result.put("RTB3Rafter", "- 3'");
			result.put("RTB3Rabove1", "插入片段3反向扩增引物序列为：");
			result.put("RTB3Rabove2", "引物Tm计算值为："+PrimerRTm+"℃");
			result.put("RTB3Rabove1_en", "The reverse amplification primer sequence of the insert 3:");
			result.put("RTB3Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
			
			PrimerDesign(Insertseq4Str);
			result.put("RTB4Fvalue", Spit5seq.toLowerCase()+PrimerF.toUpperCase());
			result.put("RTB4Fbefore", "5'-");
			result.put("RTB4Fafter", "- 3'");
			result.put("RTB4Fabove1", "插入片段4正向扩增引物序列为：");
			result.put("RTB4Fabove1_en", "The forward amplification primer sequence of the insert 4:");
			strMiddleHomo = CalMiddleHomo(result.get("RTB3Rvalue").toString().toUpperCase(),Spit5seq.toUpperCase()+PrimerF.toUpperCase());
			sequence(strMiddleHomo);
//			GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
//			//换算GC含量百分比，并取小数点一位
//			b = new BigDecimal(GCRateMiddleHomo);
//			GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			GCRateMiddleHomo = 0d;
			if((numG+numC) > 0 && strMiddleHomo.length() > 0) {
				GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
				//换算GC含量百分比，并取小数点一位
				b = new BigDecimal(GCRateMiddleHomo);
				GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			result.put("RTB4Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度："+strMiddleHomo.length()+"bp   同源臂GC含量："+GCRateMiddleHomo.toString()+"%   酶切位点：无");			
			result.put("RTB4Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+strMiddleHomo.length()+"bp   GC Content Of Homologous Sequences:"+GCRateMiddleHomo.toString()+"%   Restriction Enzyme Cutting Site:None");
			
			result.put("RTB4Rvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB4Rbefore", "5'-");
			result.put("RTB4Rafter", "- 3'");
			result.put("RTB4Rabove1", "插入片段4反向扩增引物序列为：");
			result.put("RTB4Rabove1_en", "The reverse amplification primer sequence of the insert 4:");
			if(selectFlagStr.equals("Single")) {
				result.put("RTB4Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+n);			
				result.put("RTB4Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+n);
			}else if(selectFlagStr.equals("Double")) {
				result.put("RTB4Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+Com3RC);			
				result.put("RTB4Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+Com3RC);
			}else if(selectFlagStr.equals("PCR")) {
				result.put("RTB4Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度："+HomoR.length()+"bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点：无");			
				result.put("RTB4Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:"+HomoR.length()+"bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:None");
			}
		}
		
		String Insertseq5Str = "";
		if(fragmentFlagInt > 4) {
			Insertseq5Str = multiFragmentClone.getInsertseq5();
			InterCal(Insertseq4Str,Insertseq5Str,20);
			result.put("RTB4Rvalue", Spit3seq.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB4Rbefore", "5'-");
			result.put("RTB4Rafter", "- 3'");
			result.put("RTB4Rabove1", "插入片段4反向扩增引物序列为：");
			result.put("RTB4Rabove2", "引物Tm计算值为："+PrimerRTm+"℃");
			result.put("RTB4Rabove1_en", "The reverse amplification primer sequence of the insert 4:");
			result.put("RTB4Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃");
			
			PrimerDesign(Insertseq5Str);
			result.put("RTB5Fvalue", Spit5seq.toLowerCase()+PrimerF.toUpperCase());
			result.put("RTB5Fbefore", "5'-");
			result.put("RTB5Fafter", "- 3'");
			result.put("RTB5Fabove1", "插入片段5正向扩增引物序列为：");
			result.put("RTB5Fabove1_en", "The forward amplification primer sequence of the insert 5:");
			strMiddleHomo = CalMiddleHomo(result.get("RTB4Rvalue").toString().toUpperCase(),Spit5seq.toUpperCase()+PrimerF.toUpperCase());
			sequence(strMiddleHomo);
//			GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
//			//换算GC含量百分比，并取小数点一位
//			b = new BigDecimal(GCRateMiddleHomo);
//			GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			GCRateMiddleHomo = 0d;
			if((numG+numC) > 0 && strMiddleHomo.length() > 0) {
				GCRateMiddleHomo = Double.valueOf(numG+numC)/Double.valueOf(strMiddleHomo.length())*100d;
				//换算GC含量百分比，并取小数点一位
				b = new BigDecimal(GCRateMiddleHomo);
				GCRateMiddleHomo = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			result.put("RTB5Fabove2", "引物Tm计算值为："+PrimerFTm+"℃   同源臂长度："+strMiddleHomo.length()+"bp   同源臂GC含量："+GCRateMiddleHomo.toString()+"%   酶切位点：无");			
			result.put("RTB5Fabove2_en", "Primer Tm Value: "+PrimerFTm+"℃   Homologous Sequences Length:"+strMiddleHomo.length()+"bp   GC Content Of Homologous Sequences:"+GCRateMiddleHomo.toString()+"%   Restriction Enzyme Cutting Site:None");
			
			result.put("RTB5Rvalue", HomoR.toLowerCase()+PrimerR.toUpperCase());
			result.put("RTB5Rbefore", "5'-");
			result.put("RTB5Rafter", "- 3'");
			result.put("RTB5Rabove1", "插入片段5反向扩增引物序列为：");
			result.put("RTB5Rabove1_en", "The reverse amplification primer sequence of the insert 5:");
			if(selectFlagStr.equals("Single")) {
				result.put("RTB5Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+n);			
				result.put("RTB5Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+n);
			}else if(selectFlagStr.equals("Double")) {
				result.put("RTB5Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度：15bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点："+Com3RC);			
				result.put("RTB5Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:15bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:"+Com3RC);
			}else if(selectFlagStr.equals("PCR")) {
				result.put("RTB5Rabove2", "引物Tm计算值为："+PrimerRTm+"℃   同源臂长度："+HomoR.length()+"bp   同源臂GC含量："+GCRateR.toString()+"%   酶切位点：无");			
				result.put("RTB5Rabove2_en", "Primer Tm Value: "+PrimerRTm+"℃   Homologous Sequences Length:"+HomoR.length()+"bp   GC Content Of Homologous Sequences:"+GCRateR.toString()+"%   Restriction Enzyme Cutting Site:None");
			}
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
		if(strRTBR.length() <= strRTBF.length()) {
			for(int i=0;i<strRTBR.length();i++) {
				String strTmpR = strRTBR.substring(0, strRTBR.length()-i);
				String strTmpF = strSeqRC.substring(strSeqRC.length()-strRTBR.length()+i, strSeqRC.length());
				if(strTmpF.equalsIgnoreCase(strTmpR)) {
					resultStr = strTmpF;
					break;
				}
			}
		}else {
			for(int i=0;i<strRTBF.length();i++) {
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
	
	private void InterCal(String Seq1,String Seq2,int Length) {
		String SeqCombine = Seq1 + Seq2;
		int Spit5 = 0;
		int Spit3 = 0;
		Double[] GC = new Double[0];
		int NiceGCNum = 0;
		
		for(int i=0;i<=Length;i++) {
			sequence(SeqCombine.substring(Seq1.length()-Length+i,Seq1.length()+i));
			GC = Arrays.copyOf(GC, i+1);
			GC[i] = ((double) (numC) + (double) (numG)) / ((double) (numA) + (double) (numT) + (double) (numC) + (double) (numG));
		}
		for(int i=0;i<GC.length;i++) {			//'优先GC含量，再次满足离中线最近的组合
			if(Math.abs(GC[i]-0.5d) < Math.abs(GC[NiceGCNum]-0.5d)) {
				NiceGCNum = i;
			}else if(Math.abs(GC[i]-0.5d) == Math.abs(GC[NiceGCNum]-0.5d)) {
				if(Math.abs(i - (GC.length+1d)/2d) < Math.abs(NiceGCNum - (GC.length+1d)/2d)) {
					NiceGCNum = i;
				}
			}
		}
		
		Spit5 = Seq1.length()-(Seq1.length()-Length+NiceGCNum);
		Spit3 = Length-Spit5;
		
		Spit5seq = Seq1.substring(Seq1.length()-Spit5, Seq1.length());
		Spit3seq = RC(Seq2.substring(0, Spit3));
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
	private Map<String,Object> checkField(MultiFragmentCloneEntity multiFragmentClone){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		result.put("success", "false");
		String selectFlagStr = multiFragmentClone.getSelectFlag();
		
		if(StringUtils.isEmpty(selectFlagStr)){
			result.put("message", "selectFlag不能为空");
			return result;
		}
		if(StringUtils.isEmpty(multiFragmentClone.getFragmentFlag())) {
			result.put("message", "fragmentFlag不能为空");
			return result;
		}
		
		switch(selectFlagStr) {
		case "Single":
			if(StringUtils.isEmpty(multiFragmentClone.getVectorseq())) {
				result.put("success", "false");
				result.put("message", "vectorseq不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragmentRC())) {
				result.put("success", "false");
				result.put("message", "fragmentRC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragmentSites())) {
				result.put("success", "false");
				result.put("message", "fragmentSites不能为空");
				return result;
			}
			break;
		case "Double":
			if(StringUtils.isEmpty(multiFragmentClone.getVectorseq())) {
				result.put("success", "false");
				result.put("message", "vectorseq不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragment5RC())) {
				result.put("success", "false");
				result.put("message", "fragment5RC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragment5Sites())) {
				result.put("success", "false");
				result.put("message", "fragment5Sites不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragment3RC())) {
				result.put("success", "false");
				result.put("message", "fragment3RC不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getFragment3Sites())) {
				result.put("success", "false");
				result.put("message", "fragment3Sites不能为空");
				return result;
			}
			break;
		case "PCR":
			if(StringUtils.isEmpty(multiFragmentClone.getVectorseqU())) {
				result.put("success", "false");
				result.put("message", "vectorseqU不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getVectorseqD())) {
				result.put("success", "false");
				result.put("message", "fragment5RC不能为空");
				return result;
			}
			break;
		default:
			result.put("success", "false");
			result.put("message", "selectFlag类型错误");
			return result;
		}
		
		switch(multiFragmentClone.getFragmentFlag()) {
		case "5":
			if(StringUtils.isEmpty(multiFragmentClone.getInsertseq5())) {
				result.put("success", "false");
				result.put("message", "insertseq5不能为空");
				return result;
			}
		case "4":
			if(StringUtils.isEmpty(multiFragmentClone.getInsertseq4())) {
				result.put("success", "false");
				result.put("message", "insertseq4不能为空");
				return result;
			}
		case "3":
			if(StringUtils.isEmpty(multiFragmentClone.getInsertseq3())) {
				result.put("success", "false");
				result.put("message", "insertseq3不能为空");
				return result;
			}
		case "2":
			if(StringUtils.isEmpty(multiFragmentClone.getInsertseq2())) {
				result.put("success", "false");
				result.put("message", "insertseq2不能为空");
				return result;
			}
			if(StringUtils.isEmpty(multiFragmentClone.getInsertseq1())) {
				result.put("success", "false");
				result.put("message", "insertseq1不能为空");
				return result;
			}
			break;
		default:
			result.put("message", "fragmentFlag类型错误");
			return result;
		}
		
		
		result.put("success", "true");
		return result;
	}
}

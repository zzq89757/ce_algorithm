package com.blueland.sys.log.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.blueland.model.RestructureDNAEntity;
import com.blueland.sys.log.dao.SysOplogDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 重组反应DNA使用量计算
 */
@Slf4j
@Service
public class RestructureDNAService  {
	@SuppressWarnings("unused")
	@Autowired
	private SysOplogDao sysOplogDao;
	
	/**
	 * 重组反应DNA使用量计算
	 * @param params：
	 * selectFlag（选择类型）：不能为空，选项值为：入门克隆“SimpleClone”；单片段克隆“SingleClone”；多片段克隆“MultiClone”；单位点突变“SinglePoint”；双位点突变“DoublePoint”；多为点突变“MultiPoint”
	 * textMark（标注当前是第几个Text）：不能为空，内容为：text1，text2，text3，text4，text5，text6；
	 * textValue：不能为空，必须是数据可以包含小数点不限制位数
	 * @return
	 * success：正确true，错误false
	 * message：错误的原因
	 * promptMessage：提示消息，弹出提示框，只有确认按钮，不影响程序后续操作
	 * reValue：返回当前行计算结果 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> restructureDNA(RestructureDNAEntity restructureDNA){
		Map<String,Object> result = new HashMap();
		result.put("success", "false");
		
		if(StringUtils.isEmpty(restructureDNA.getSelectFlag())){
			result.put("message", "selectFlag不能为空");
			return result;
		}
		if(StringUtils.isEmpty(restructureDNA.getTextMark())) {
			result.put("message", "textMark不能为空");
			return result;
		}
		if(StringUtils.isEmpty(restructureDNA.getTextValue())) {
			result.put("message", "textValue不能为空");
			return result;
		}
		
		String selectFlagStr = restructureDNA.getSelectFlag();
		String textMarkStr = restructureDNA.getTextMark();
		Double textValueD = 0d;
		try {
			textValueD = Double.valueOf(restructureDNA.getTextValue());
		}catch(Exception e) {
			result.put("message", "textValue非数字");
			return result;
		}
		Double reDouble = 0d;
		int reInt = 0;
		textValueD = textValueD * 1000d;
		if(textValueD == 0d) {
			result.put("reValue", "");
			result.put("success", "true");
			return result;
		}
		if(textMarkStr.equalsIgnoreCase("text1")) {
			switch(selectFlagStr) {
			case "SimpleClone":
				reDouble = 0.04 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 20) {
					reInt = 20;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			case "SingleClone":
			case "MultiClone":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 50) {
					reInt = 50;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			case "SinglePoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 400) {
					reInt = 400;
				}else if(reDouble <= 50) {
					reInt = 50;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			case "DoublePoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 20) {
					reInt = 20;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			case "MultiPoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text1选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}else if(textMarkStr.equalsIgnoreCase("text2")) {
			switch(selectFlagStr) {
			case "SingleClone":
			case "DoublePoint":
				reDouble = 0.04 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 20) {
					reInt = 20;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			case "MultiClone":
			case "MultiPoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text2选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}else if(textMarkStr.equalsIgnoreCase("text3")) {
			switch(selectFlagStr) {
			case "MultiClone":
			case "MultiPoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text3选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}else if(textMarkStr.equalsIgnoreCase("text4")) {
			switch(selectFlagStr) {
			case "MultiClone":
			case "MultiPoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text4选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}else if(textMarkStr.equalsIgnoreCase("text5")) {
			switch(selectFlagStr) {
			case "MultiClone":
			case "MultiPoint":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text5选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}else if(textMarkStr.equalsIgnoreCase("text6")) {
			switch(selectFlagStr) {
			case "MultiClone":
				reDouble = 0.02 * textValueD;
				if(reDouble >= 200) {
					reInt = 200;
				}else if(reDouble <= 10) {
					reInt = 10;
				}else {
					BigDecimal b = new BigDecimal(reDouble);
					reDouble = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
					reInt = reDouble.intValue();
				}
				break;
			default:
				result.put("success", "false");
				result.put("message", "text5选择类型selectFlag不存在");
				return result;
			}
			result.put("reValue", reInt+" ng");
		}
		
		result.put("success", "true");
		return result;
	}
		
	/**
	 * 检查输入字段是否符合基本要求
	 * @param params
	 * @return
	 */
	private Map<String,Object> checkField(Map<String,String> params){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> result = new HashMap();
		if(params.get("selectFlag")==null||StringUtils.isEmpty(params.get("selectFlag"))){
			result.put("success", "false");
			result.put("message", "selectFlag不能为空");
			return result;
		}
		if(params.get("textMark")==null||StringUtils.isEmpty(params.get("textMark"))) {
			result.put("success", "false");
			result.put("message", "textMark不能为空");
			return result;
		}
		if(params.get("textValue")==null||StringUtils.isEmpty(params.get("textValue"))) {
			result.put("success", "false");
			result.put("message", "textValue不能为空");
			return result;
		}
				
		result.put("success", "true");
		return result;
	}
}

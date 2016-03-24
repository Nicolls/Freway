package com.freway.ebike.bluetooth;


/*
 * http://www.jirou.com/tool/reliang.php
 <!-- The Option Value is the Number of Calories for this Activity --> 
 <select name="cmbActivity"> 
 <option value="0" selected=""><font><font>&lt; - 肌肉网 - 选择运动 - &gt;</font></font></option> 
 <option value="575.5"><font><font>健美操，一般</font></font></option> 
 <option value="958.8"><font><font>跳绳，适中，一般</font></font></option> 
 <option value="766.6"><font><font>篮球，游戏</font></font></option> 
 <option value="766.6"><font><font>跑步8公里（10分钟一公里）</font></font></option>  
 <option value="671.1"><font><font>滑冰，冰，一般</font></font></option> 
 <option value="766.6"><font><font>滑雪，越野，适度的努力</font></font></option>   
 <option value="958.8"><font><font>足球，有竞争力</font></font></option> 
 <option value="480"><font><font>垒球，或快或慢间隔的运动</font></font></option> 
 <option value="383.3"><font><font>骑自行车，&lt;10英里每小时，休闲</font></font></option> 
 <option value="766.6"><font><font>骑自行车，1213.9英里每小时，适度的努力</font></font></option> 
 <option value="478.8"><font><font>骑自行车，固定，一般</font></font></option> 
 <option value="431.1"><font><font>健美操，家庭，轻/中度努力</font></font></option>  
 <option value="958.8"><font><font>游泳的时候，自由泳，速度快，大力</font></font></option> 
 <option value="766.6"><font><font>游泳的时候，自由泳，轻/中度努力</font></font></option> 
 <option value="671.1"><font><font>网球，一般</font></font></option> 
 <option value="240"><font><font>散步，3.2公里，速度缓慢</font></font></option> 
 <option value="335.5"><font><font>4.8公里，稳健的步伐，行走的狗散步，</font></font></option> 
 <option value="575.5"><font><font>散步，5.6公里，上坡</font></font></option> 
 <option value="383.3"><font><font>散步，6.4公里，非常轻快的步伐</font></font></option> 
 <option value="766.6"><font><font>散步，爬楼梯</font></font></option> 
 <option value="287.7"><font><font>举重，轻或中度的器械</font></font></option>  
 <option value="335.5"><font><font>打扫卫生，一般</font></font></option> 
 <option value="575.5"><font><font>舞蹈，有氧，芭蕾或现代舞</font></font></option> 
 <option value="431.1"><font><font>舞蹈，一般</font></font></option> 
 <option value="671.1"><font><font>赛艇，平稳的</font></font></option> 
 <option value="575.5"><font><font>击剑</font></font></option> 
 <option value="383.3"><font><font>钓鱼，一般</font></font></option>  
 <option value="383.3"><font><font>高尔夫球，一般</font></font></option> 
 <option value="1150"><font><font>手球，一般</font></font></option> 
 <option value="671.1"><font><font>慢跑，一??般</font></font></option> 
 <option value="958.8"><font><font>散打，空手道，拳击，跆拳道</font></font></option> 
 <option value="383.3"><font><font>划船,一般</font></font></option>  
 </select> 

 **/
public class MapKcalCaculate {
	// http://www.360doc.com/content/10/0122/10/482127_14140171.shtml
	public static final float TYPE_TEST = 383.3f;
	/** 骑自行车0－10mph的速度，每小日消耗的卡路里 */
	public static final float TYPE_BIKE_0_10_VALUE = 245.3f;
	public static final float TYPE_BIKE_11_13_VALUE = 478.8f;
	public static final float TYPE_BIKE_14_25_VALUE = 766.6f;
	public static final float TYPE_BIKE_26_31 = 965.5f;
	public static final float TYPE_BIKE_31_MAX = 1005f;
	private static MapKcalCaculate mapKcalCaculate;

	public static void main(String[] args) {
		System.out.println("result:" + MapKcalCaculate.getInstance().caculate(60, 100, TYPE_TEST));
	}

	public static MapKcalCaculate getInstance() {
		if (mapKcalCaculate == null) {
			mapKcalCaculate = new MapKcalCaculate();
		}
		return mapKcalCaculate;
	}

	/**
	 * @param time运行时长
	 * @param weight体重
	 * @param value运动类型对应的每小时消耗卡路里数
	 * */
	public float caculate(float time, float weight, float value) {
		float calories = 0;
		float minutes = 60;
		float inertia = 190;
		float realTime = time / minutes;
		float realWeight = weight / inertia;
		calories = realTime * realWeight * value;
		return calories;
	}
}

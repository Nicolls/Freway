package com.freway.ebike.bluetooth;

public class ControllerKcalCaculate {
	
	// 传参 踏的圈数
	// 返回 无,直接计算为全局变量
	//
	long sys_ride_value;//作为显示运动量的值
	byte set_mode_flag;//这个为通信第九字节读取的状态
	long last_runstate_cal_flag =0; //前一次骑行状态
	long kcal_spd_normol_val =0;//正常档位速度内
	long kcal_spd_over_val =0;//超出档位预期速度
	long kcal_change_cnt =1; //同步延迟
	long speed_tmp;//用于显示速度的值
	long last_kcal_val;//累积值,骑行卡路里 = last_kcal_val+更新值
	long kcal_ov_val;//溢出值,代表着溢出几次,在通信处大于65500并且之后小于100产生,出现一次累积1
//	final long wheel_value=2180;
	//
	//======================卡路里计算公式============================================
	//以3.4为中心踏频,也就是21km/h，单车1小时21公里 655卡，单车1小时16公里415卡，单车1小时9公里245卡
	//参照值，以21KM/H的卡路里为655，68kg级， = 3+4档位骑一个小时
	//
	// 传参 踏的圈数
	// 返回 无,直接计算为全局变量
	//



	float Kcale_Proc(int wheel_value,long kcal_num,long speedTemp,byte mode)
	{
		float result=0;
		set_mode_flag=mode;
		speed_tmp=speedTemp;
		int kcal_flag;
		kcal_flag = set_mode_flag&0x07; //判断前一次状态
		//如果切换了骑行状态,那么卡路里计算缓存要重置
		if(last_runstate_cal_flag != kcal_flag)
		{
	        last_runstate_cal_flag = kcal_flag;
	        last_kcal_val = sys_ride_value;
	        kcal_num =0;
	        kcal_change_cnt = 1;
	        kcal_spd_over_val =0;
			kcal_spd_normol_val =0;
		}
		else
		{
	            switch(kcal_flag)
	            {
	              case 0: //运动模式 直接输出
	                       kcal_num = kcal_num*26;
	                      break;
	              case 1: // 电动模式 不产生运动量
	                       kcal_num = 0;
	                      break;
	              case 2: // 助力1
	                  if(speed_tmp>12000)  //大于12KM/H 此时助力几乎无效
	                  {
	                      if(kcal_spd_over_val != 0)
	                      {
	                            if(kcal_num >= kcal_spd_over_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_over_val;
	                            }
	                            kcal_num = kcal_num*26;
	                      }
	                      else  //记录大于12KM/H的状态
	                      {
	                            kcal_spd_over_val = kcal_num; //记录
	                            last_kcal_val = sys_ride_value; //累计
	                            kcal_num =0;
	                            kcal_spd_normol_val =0;
	                      }
	                  }
	                  else
	                  {
	                      if(speed_tmp<10000)
	                      {
	                        if(kcal_spd_normol_val != 0)
	                        {
	                            if(kcal_num >= kcal_spd_normol_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_normol_val;										
	                            }
	                            kcal_num = kcal_num*104/5;
	                        }
	                        else
	                        {
	                                kcal_spd_over_val = 0;
	                                kcal_spd_normol_val = kcal_num;//记录
	                                last_kcal_val = sys_ride_value;//累计
	                                kcal_num =0;	
	                        }									
	                      }
	                      else //速度属于助力标称值
	                      {
	                        if(kcal_spd_normol_val != 0)
	                        {
	                            if(kcal_num >= kcal_spd_normol_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_normol_val;
	                            }
	                            kcal_num = kcal_num*104/5;
	                        }	
	                        if(kcal_spd_over_val != 0)
	                        {
	                            if(kcal_num >= kcal_spd_over_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_over_val;
	                            }
	                            kcal_num = kcal_num*26;
	                        }									
	                      }
	                  }
	              break;
	              case 3: //助力2 >19km/h
	                  if(speed_tmp>19000)
	                  {
	                      if(kcal_spd_over_val != 0)
	                      {
	                        if(kcal_num >= kcal_spd_over_val)
	                        {
	                                kcal_num = kcal_num - kcal_spd_over_val;
	                        }									
	                        kcal_num = kcal_num*26;								
	                      }
	                      else
	                      {
	                        kcal_spd_over_val = kcal_num;
	                        last_kcal_val = sys_ride_value;
	                        kcal_num =0;
	                        kcal_spd_normol_val =0;
	                      }
	                  }
	                  else
	                  {
	                      if(speed_tmp<16000)
	                      {
	                          if(kcal_spd_normol_val != 0)
	                          {
	                                  if(kcal_num >= kcal_spd_normol_val)
	                                  {
	                                          kcal_num = kcal_num - kcal_spd_normol_val;
	                                  }
	                                  kcal_num = kcal_num*13;
	                          }
	                          else
	                          {
	                                  kcal_spd_over_val = 0;
	                                  kcal_spd_normol_val = kcal_num;
	                                  last_kcal_val = sys_ride_value;
	                                  kcal_num =0;	
	                          }									
	                      }
	                      else
	                      {
	                          if(kcal_spd_normol_val != 0)
	                          {
	                              if(kcal_num >= kcal_spd_normol_val)
	                              {
	                                      kcal_num = kcal_num - kcal_spd_normol_val;
	                              }
	                              kcal_num = kcal_num*13;
	                          }	
	                          if(kcal_spd_over_val != 0)
	                          {
	                              if(kcal_num >= kcal_spd_over_val)
	                              {
	                                      kcal_num = kcal_num - kcal_spd_over_val;
	                              }
	                              kcal_num = kcal_num*26;
	                          }									
	                      }
	                  }
	              break;
	              case 4: //助力3 >27km/h
	                  if(speed_tmp>26000)
	                  {
	                      if(kcal_spd_over_val != 0)
	                      {
	                              if(kcal_num >= kcal_spd_over_val)
	                              {
	                                      kcal_num = kcal_num - kcal_spd_over_val;
	                              }
	                              kcal_num = kcal_num*26;
	                      }
	                      else
	                      {
	                              kcal_spd_over_val = kcal_num;
	                              last_kcal_val = sys_ride_value;
	                              kcal_num =0;
	                              kcal_spd_normol_val =0;
	                      }
	                  }
	                  else
	                  {
	                      if(speed_tmp<24000)
	                      {
	                        if(kcal_spd_normol_val != 0)
	                        {
	                              if(kcal_num >= kcal_spd_normol_val)
	                              {
	                                      kcal_num = kcal_num - kcal_spd_normol_val;
	                              }
	                              kcal_num = kcal_num*26/5;
	                        }
	                        else
	                        {
	                              kcal_spd_over_val = 0;
	                              kcal_spd_normol_val = kcal_num;
	                              last_kcal_val = sys_ride_value;
	                              kcal_num =0;	
	                        }									
	                      }
	                      else
	                      {
	                        if(kcal_spd_normol_val != 0)
	                        {
	                            if(kcal_num >= kcal_spd_normol_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_normol_val;
	                            }
	                            kcal_num = kcal_num*26/5;
	                        }	
	                        if(kcal_spd_over_val != 0)
	                        {
	                            if(kcal_num >= kcal_spd_over_val)
	                            {
	                                    kcal_num = kcal_num - kcal_spd_over_val;
	                            }
	                            kcal_num = kcal_num*26;
	                        }									
	                      }
	                  }
	              break;
	              default:
	                      break;
	            }			
		}
		//// 延迟,等待1S，2.0不需要
		//if(kcal_change_cnt >0)
		//{
		//	kcal_change_cnt++;
		//	if(kcal_change_cnt > 3)
		//	{
	 //           kcal_change_cnt =0;			
		//	}
		//}
		//else
		//{
		//	kcal_num =0;
		//}
	        //系数K，档位倍率*2πR/一圈的点数  wheel_value = 218/100
	        //  Kcal = 圈数*wheel_value*系数k（这里是/10）*655/21000 = kcal_num*4*655/2100000
		sys_ride_value = kcal_ov_val*65535+kcal_num;
		sys_ride_value = sys_ride_value/10;		 
		sys_ride_value = sys_ride_value*wheel_value*655;	
		sys_ride_value = sys_ride_value/2100000;			

		sys_ride_value = sys_ride_value + last_kcal_val;
		result=sys_ride_value/10f;
		return result;
	}
	
}

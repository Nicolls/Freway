package com.freway.ebike.protocol;
/**
 * @author Nicolls
 * @Description 操作码
 * @date 2015年10月25日
 */
public class CommandCode {
	/**电源*/
	public static final byte ERROR=-1;
	/**电源*/
	public static final byte POWER=1;
	/**概况*/
	public static final byte SURVEY=0x31;
	/**车前灯*/
	public static final byte LIGHT_FRONT=3;
	/**车后灯*/
	public static final byte LIGHT_BEHIND=4;
}

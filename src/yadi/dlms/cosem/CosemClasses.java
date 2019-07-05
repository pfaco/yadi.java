/*
 * YADI (Yet Another DLMS Implementation)
 * Copyright (C) 2018 Paulo Faco (paulofaco@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package yadi.dlms.cosem;

public enum CosemClasses {
	DATA(1),
	REGISTER(3),
	EXTENDED_REGISTER(4),
	DEMAND_REGISTER(5),
	REGISTER_ACTIVATION(6),
	PROFILE_GENERIC(7),
	CLOCK(8),
	SCRIPT_TABLE(9),
	SCHEDULE(10),
	SPECIAL_DAYS_TABLE(11),
	ASSOCIATION_SN(12),
	ASSOCIATION_LN(15),
	SAP_ASSIGNMENT(17),
	IMAGE_TRANSFER(18),
	IEC_LOCAL_PORT_SETUP(19),
	ACTIVITY_CALENDAR(20),
	REGISTER_MONITOR(21),
	SINGLE_ACTION_SCHEDULE(22),
	IEC_HDLC_SETUP(23),
	UTILITY_TABLES(26),
	DATA_PROTECTION(30),
	PUSH_SETUP(40),
	REGISTER_TABLE(61),
	COMPACT_DATA(62),
	STATUS_MAPPING(63),
	SECURITY_SETUP(64),
	PARAMETER_MONITOR(65),
	SENSOR_MANAGER(67),
	ARBITRATOR(68),
	DISCONNECT_CONTROL(70),
	LIMITER(71),
	ACCOUNT(111),
	CREDIT(112),
	CHARGE(113),
	TOKEN_GATEWAY(115),
	FUNCTION_CONTROL(122),
	ARRAY_MANAGER(123);
	
	public final int id;
	
	private CosemClasses(int id) {
		this.id = id;
	}
}

package com.hakyn.game.living;

public class HKResistance {
	
	public static final short RESISTANCETYPE_WATER = 1;
	public static final short RESISTANCETYPE_FIRE = 2;
	public static final short RESISTANCETYPE_WIND = 3;
	public static final short RESISTANCETYPE_EARTH = 4;
	
	public short ResistanceType;
	public float ResistancePercent;
	
	public HKResistance(short rtype, float rprecent) {
		this.ResistanceType = rtype;
		this.ResistancePercent = rprecent;
	}
	
}

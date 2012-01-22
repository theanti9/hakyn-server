package com.hakyn.game.living;

public class HKStats {
	
	public int HP;
	public int Level;
	public int Str;
	public int Def;
	public int Agi;
	public int Speed;
	public float DmgMultiplier;
	
	// Create the object with the given stats
	public HKStats(int hp, int level, int str, int def, int agi, int speed, float dmg) {
		this.HP = hp;
		this.Level = level;
		this.Str = str;
		this.Def = def;
		this.Agi = agi;
		this.Speed = speed;
		this.DmgMultiplier = dmg;
	}
	
	
	
	// Write methods to do damage/dodge/hit/etc. calculations
	
	
}

/*
 * HKMonster.java
 * 
 * Class representing monster database objects
 */

package com.hakyn.game.living;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hakyn.db.MySqlDbConnection;

public class HKMonster {
	
	public int MonsterID;
	public String MonsterName;
	public HKStats MonsterStats;
	public int MaxSpawn;
	public float SpawnChance;
	public List<HKResistance> ResistanceList;
	public List<HKMonsterSkill> MonsterSkills;
	
	public HKMonster(int monsterid, String monstername, HKStats monsterstats, int maxspawn, float spawnchance, List<HKResistance> resList, List<HKMonsterSkill> mskill) {
		this.MonsterID = monsterid;
		this.MonsterName = monstername;
		this.MonsterStats = monsterstats;
		this.MaxSpawn = maxspawn;
		this.SpawnChance = spawnchance;
		this.ResistanceList = resList;
		this.MonsterSkills = mskill;
	}
	
	public static List<HKMonster> getZoneSpawnableMonsters(int zoneId) {
		List<HKMonster> spawnable = new ArrayList<HKMonster>();
		
		try {
			Statement sql = MySqlDbConnection.getInstance().getNewStatement();
			// Make sure the query actually works. If it doesn't, let it fall through and return an empty list
			if (sql.execute("SELECT * FROM  `monster_spawns` LEFT JOIN  `monsters` ON  `monster_spawns`.`MonsterID` =  `monsters`.`MonsterID` WHERE  `monster_spawns`.`ZoneId` = " + zoneId)) {
				ResultSet res = sql.getResultSet();
				while (res.next()) {
					// Make a list for resistance objects and check to see if there are any
					List<HKResistance> rlist = new ArrayList<HKResistance>();
					if (res.getInt("MonsterResistance1Type") != 0) {
						rlist.add(new HKResistance(res.getShort("MonsterResistance1Type"), res.getFloat("MonsterResistance1Percent")));
						// if there's a first, there might be a second
						if (res.getInt("MonsterResistance2Type") != 0) {
							rlist.add(new HKResistance(res.getShort("MonsterResistance2Type"), res.getFloat("MonsterResistance2Percent")));
						}
					}
					// Loop over the skill fields to see if there are any, break when you find one that's null
					List<HKMonsterSkill> mskill = new ArrayList<HKMonsterSkill>();
					for (int i = 1; i < 4; i++) {
						if (res.getInt("MonsterSkill"+ i) == 0) {
							break;
						}
						mskill.add(new HKMonsterSkill(res.getInt("MonsterSkill"+i), res.getFloat("MonsterSkill"+i+"Percent")));
					}
					// finally add everything to the spawnable list
					spawnable.add(new HKMonster(res.getInt("MonsterID"), res.getString("MonsterName"), new HKStats( res.getInt("MonsterHP"),
							res.getInt("MonsterLevel"),
							res.getInt("MonsterStr"),
							res.getInt("MonsterDef"),
							res.getInt("MonsterAgi"),
							res.getInt("MonsterSPeed"),
							res.getFloat("MonsterDamageMultiplier")), res.getInt("MaxSpawn"),
																	  res.getFloat("MonsterSpawnChance"),
																	  rlist,
																	  mskill));
				}
				
			}
		} catch (SQLException e) {
			// Do nothing. Return empty list
		}
		return spawnable;
	}

}

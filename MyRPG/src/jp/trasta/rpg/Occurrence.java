package jp.trasta.rpg;

import java.util.Random;

class Occurrence{
	private Random rand = new Random();//乱数
	private int map;
	private int cell;
	Occurrence(int map,int cell){
		this.map=map;
		this.cell=cell;
	}
	public Monster getOccurrence(){
		Monster monster=new Monster();
		switch(this.map){
			case 8: 
				switch(this.cell){
					case 0 : 
						int o[]={1,2,1};
						int ran=rand.nextInt(5)+60;
						int i=o[rand.nextInt(3)];
						return monster.getMonster(i,ran);
				}
				break;
			case 2 : 
				switch(this.cell){
					case 0 : 
						int o[]={1,2,3};
						int ran=rand.nextInt(30)+50;
						int i=o[rand.nextInt(3)];
						return monster.getMonster(i,ran);
				}
				break;
		}
		return monster.getMonster(0,1);
	}
}
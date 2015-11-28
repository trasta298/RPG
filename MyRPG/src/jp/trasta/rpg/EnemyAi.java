package jp.trasta.rpg;

import java.util.ArrayList;
import java.util.Random;

class EnemyAi{
	Random rand = new Random();
	private Monster e_list[];
	private Monster p_list[];
	private int atk;

	EnemyAi(Monster enemy_list[],int num,Monster player_list[]){
		e_list=enemy_list;
		p_list=player_list;
		atk=num;
	}

	public int getAction(){
		return 0; //今は攻撃だけ
	}

	public int getID(){
		return 0;
	}

	public int getTarget(){
		ArrayList<Integer> n = new ArrayList<Integer>();
		for(int i=0;i<3;i++){
			if(this.p_list[i].islive()) n.add(new Integer(i));
		}
		return n.get(rand.nextInt(n.size()));
	}

}
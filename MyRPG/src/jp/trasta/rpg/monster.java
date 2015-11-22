package jp.trasta.rpg;

import java.util.ArrayList;

public class Monster {
	private int id;//モンスターID
	private int lv;//モンスターのレベル
	private int exp;//経験値
	private int atp;//攻撃力
	private int hp;//HP
	private int defense_p;//物理耐性
	private int defense_m;//魔法耐性
	private int map;//魔力
	private ArrayList<Trick> tricks = new ArrayList<Trick>();//使えるマジックのリスト
	private String name;//名前
	private String Nname;//ニックネーム
	Monster(int id,String nname,String name){
		this.id=id;
		this.Nname=nname;
		this.name=name;
	}
	//次のレベルに上がるまでに必要な経験値を返す(現在の経験値無視)
	public int getExpTable(){
		return 0;
	}
	public int getLv() {
		return lv;
	}
};

class Slime extends Monster {
	Slime(String nname,int lv,ArrayList<Trick> tricks){
		super(1,nname,"スライム");
	}
	public void setLv(int lv){

	}
};
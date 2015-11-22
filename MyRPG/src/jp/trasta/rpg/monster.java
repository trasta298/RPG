package jp.trasta.rpg;

import java.util.ArrayList;

public class Monster {
	private int id;//モンスターID
	private int lv;//モンスターのレベル
	private int exp;//経験値
	private int atp;//攻撃力
	private int hp;//HP
	private int defense;//防御力
	private int map;//魔力
	private ArrayList<Magic> magics = new ArrayList<Magic>();//使えるマジックのリスト
	private String name;//名前
	private String Nname;//ニックネーム

	Monster(int id,String nname,String name){
		this.id=id;
		this.Nname=nname;
		this.name=name;
	}
};

class slime extends Monster {
	slime(String nname){
		super(1,nname,"スライム");
	}
};
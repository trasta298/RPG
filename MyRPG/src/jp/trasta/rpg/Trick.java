package jp.trasta.rpg;

/**
 * Created by trasta298 on 2015/11/22.
 */

public class Trick {
	private int id;//ID
	private String name;//魔法の名前
	Trick(int id,String name){
		this.id=id;
		this.name=name;
	}
	public void attack(Monster sttacker,Monster victim){

	}
}

class ig extends Trick{
	private int baseatp=10;
	ig(int id){
	   super(id,"イグ");
	}
}
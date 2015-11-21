package jp.trasta.rpg;

class monster {
	int Lv;
	int id;
	String Nname;
	int gra;
	
	monster(String name){
		this.Nname = name;
	}
};

class slime extends monster {
	slime(){
		super("slime");
	}
};
package jp.trasta.rpg;

import java.util.ArrayList;
import java.util.Random;

class States {
	public int hp;//HP
	public int speed;//素早さ
	public int atp;//攻撃力
	public int map;//魔力
	public int dp;//物理耐性
	public int dm;//魔法耐性
	States(){}
};

class Monster {
	Random rand = new Random();//乱数
	private int id;//モンスターID
	private int rank;//モンスターランク 0~8 0が最高
	private int temper;//性格
	private int lv;//モンスターのレベル
	private States states=new States();
	private int initial[]=new int[6];//個体値
	private int tribal[]=new int[6];//種族値
	private int exp;//経験値
	private int exptable;//経験値テーブル 30:300万テーブル など
	private ArrayList<Trick> tricks = new ArrayList<Trick>();//使えるマジックのリスト
	private String name;//名前
	private String Nname;//ニックネーム
	Monster(){}
	Monster(int id,String name,int Rank,String n,int e){
		this.id=id;
		this.name=name;
		this.rank=Rank;
		this.Nname=n;
		this.exptable=e;
	}
	// idからモンスターを取得 type 0:味方 1:敵
	public Monster getMonster(int id,int type,int Lv,String n){
		Monster list[] = {
			new Nazo(type,Lv,n),
			new Slime(type,Lv,n),
			new Golem(type,Lv,n)
		};
		return list[id];
	}
	private int random_v(int min,int max){
		int e = 0;
		for(int i=min;i<max;i++){
			if(i==max-1){
				int ran=rand.nextInt(3);
				if(ran==0) e=max;
				else e=i;
				break;
			}else{
				int ran=rand.nextInt(2);
				if(ran==0){
					e=i;
					break;
				}
			}
		}
		return e;
	}
	protected void setMonsterData(int Tribal[],int Initial[][],int Temper){
		this.tribal=Tribal;
		this.temper=Temper;
		for(int i=0;i<6;i++){
			this.initial[i]=random_v(Initial[i][0],Initial[i][1]);
		}
	}
	public void setData(int Lv,int p[]){
		this.lv=Lv;
		this.states.hp=p[0];
		this.states.speed=p[1];
		this.states.atp=p[2];
		this.states.map=p[3];
		this.states.dp=p[4];
		this.states.dm=p[5];
	}
	//次のレベルに上がるまでに必要な経験値を返す(現在の経験値無視)
	public int getNextExp(int lv){
		float a = 0,b = 0,c = 0;
		int d=50;
		int i;
		if(exptable==30){
			a=0.019f;b=0.1f;c=29477.2f;//300万テーブル
		}else if(exptable==35){
			a=0.0215f;b=0.1f;c=29195.3f;//350万テーブル
		}
		if(lv<51){
			i=(int) Math.floor((int)(10*(1+(1/(1+(lv+(1/a))*b))))^(lv-1)*lv);
		}else{
			i=(int) Math.floor((int)(10*(1+(1/(1+(d+(1/a))*b))))^(int)((d-1)*d+Math.sqrt((lv-d)/d)*c));
		}
		return i;
	}
	public int getLv() {
		return lv;
	}
	public void setLv(int lv,int type) {//type 0:味方 1:敵
		this.lv=lv;
		this.states.hp=(int)(((this.tribal[0]*2+this.initial[0])*lv+5)*getTemperValue(this.temper)[0]);
	}
	private float[] getTemperValue(int temper){
		// hp speed atp map dp dm
		float TemperValue[][] = {
			{1,1,1,1,1,1},
			{1.1f,0.9f,1,1,1,1}
		};
		return TemperValue[temper];
	}
};

class Nazo extends Monster {
	// hp speed atp map dp dm
	private int tribal[]={10,10,10,10,10,10};
	private int initial[][]={{10,20},{10,20},{10,20},{10,20},{10,20},{10,20}};
	private int temper=rand.nextInt(2);
	Nazo(int type,int lv,String n){
		super(0,"アンノウン",0,n,35);
		setMonsterData(this.tribal,this.initial,this.temper);
		setLv(lv,type);
	}
};

class Slime extends Monster {
	private int tribal[]={10,10,10,10,10,10};
	private int initial[][]={{10,20},{10,20},{10,20},{10,20},{10,20},{10,20}};
	private int temper=rand.nextInt(2);
	Slime(int type,int lv,String n){
		super(1,"スライム",8,n,30);
		setMonsterData(this.tribal,this.initial,this.temper);
		setLv(lv,type);
	}
};

class Golem extends Monster {
	private int tribal[]={10,10,10,10,10,10};
	private int initial[][]={{10,20},{10,20},{10,20},{10,20},{10,20},{10,20}};
	private int temper=rand.nextInt(2);
	Golem(int type,int lv,String n){
		super(2,"ゴーレム",8,n,30);
		setMonsterData(this.tribal,this.initial,this.temper);
		setLv(lv,type);
	}
};

package jp.trasta.rpg;

import java.util.ArrayList;

public class Monster {
	private int id;//�����X�^�[ID
	private int lv;//�����X�^�[�̃��x��
	private int exp;//�o���l
	private int atp;//�U����
	private int hp;//HP
	private int defense_p;//�����ϐ�
	private int defense_m;//���@�ϐ�
	private int map;//����
	private ArrayList<Trick> tricks = new ArrayList<Trick>();//�g����}�W�b�N�̃��X�g
	private String name;//���O
	private String Nname;//�j�b�N�l�[��
	Monster(int id,String nname,String name){
		this.id=id;
		this.Nname=nname;
		this.name=name;
	}
	//���̃��x���ɏオ��܂łɕK�v�Ȍo���l��Ԃ�(���݂̌o���l����)
	public int getExpTable(){
		return 0;
	}
	public int getLv() {
		return lv;
	}
};

class Slime extends Monster {
	Slime(String nname,int lv,ArrayList<Trick> tricks){
		super(1,nname,"�X���C��");
	}
	public void setLv(int lv){

	}
};
package jp.trasta.rpg;

import java.util.ArrayList;

public class Monster {
	private int id;//�����X�^�[ID
	private int lv;//�����X�^�[�̃��x��
	private int exp;//�o���l
	private int atp;//�U����
	private int hp;//HP
	private int defense;//�h���
	private int map;//����
	private ArrayList<Magic> magics = new ArrayList<Magic>();//�g����}�W�b�N�̃��X�g
	private String name;//���O
	private String Nname;//�j�b�N�l�[��

	Monster(int id,String nname,String name){
		this.id=id;
		this.Nname=nname;
		this.name=name;
	}
};

class slime extends Monster {
	slime(String nname){
		super(1,nname,"�X���C��");
	}
};
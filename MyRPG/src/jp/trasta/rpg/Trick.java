package jp.trasta.rpg;

/**
 * Created by trasta298 on 2015/11/22.
 */

public class Trick {
    private int id;//�ZID
    private String name;//�Z��
    Trick(int id,String name){
        this.id=id;
        this.name=name;
    }
}

class ig extends Trick{
    private int baseatp=10;
    ig(int id){
       super(id,"�C�O");
    }
    public void attack(Monster sttacker,Monster victim){

    }
}
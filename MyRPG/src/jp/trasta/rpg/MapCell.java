package jp.trasta.rpg;

class MapCell{
	int gra;//グラフィックデータ
	boolean move_flag;//通過可能フラグ

	MapCell(int gra,boolean move_flag){
		this.gra=gra;
		this.move_flag=move_flag;
	}
}

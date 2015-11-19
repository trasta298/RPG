package jp.trasta.rpg;

class Event{
	int no;//イベントＮＯ
	String filename;//スクリプト名
	int gra;//グラフィックス
	boolean move_flag;//通過可能か
	int type_of_move;//移動タイプ(0=移動しない、1=移動する)
	int type_of_event;//イベントタイプ(0=触れた時に起動、1=決定した時に起動)
	float speed;//速度
	int x,y;//座標
	float px,py;//P座標
	boolean moving_flag;//移動中か(yes=true)
	int moving_dir;//移動中の方向（上=0,下=1,左=2,右=3）
	float sum_move_length;//移動距離（３２ドットずつ）（休憩用にも使う）

	Event(int no){
		this.no = no;
	}
}

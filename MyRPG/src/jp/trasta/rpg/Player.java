package jp.trasta.rpg;

public class Player{
	int chip_no;//チップNO
	float px,py;//P座標
	int x,y;//マップ上での座標
	float speed;//速度
	boolean moving_flag;//移動中か(yes=true)
	int moving_dir;//移動中の方向（上=0,下=1,左=2,右=3）
	float sum_move_length;//移動距離（３２ドットずつ）
	int anime_no;//２通りのアニメ
	float anime_wait;//アニメウェイト

	Player(int chip_no){
		this.chip_no = chip_no;
		moving_dir = 1;
		anime_no = 0;
	}
}
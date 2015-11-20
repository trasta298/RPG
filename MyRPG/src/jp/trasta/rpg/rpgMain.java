package jp.trasta.rpg;

import java.util.*;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;

import java.util.Random;

//サーフェイスビューの利用
public class rpgMain extends SurfaceView 
	implements SurfaceHolder.Callback,Runnable {

	private SurfaceHolder holder;//サーフェイスホルダー
	private Thread thread;//スレッド
	private Paint paint = new Paint();

	WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
	Display disp = wm.getDefaultDisplay();
	int diswidth = disp.getWidth();//画面横
	int disheight = disp.getHeight();//画面縦

	Player player = new Player(176);
	Random rand = new Random();//乱数

	//キーの状態(離している=0、押し続けている=1、押した瞬間=2）
	private int key_states[] = new int[5];//４方向（上=0,下=1,左=2,右=3）＋Ｚキー=4

	Bitmap chara_image,map_image;

	private float FrameTime;//１フレームあたりの時間（秒）
	private long waitTime;//現在時刻保存用
	private float touchX,touchY;
	private float touch_downX=-1;
	private float touch_downY=-1;
	private float control_keyX;
	private float control_keyY;
	float camera_px, camera_py; // カメラ座標
	final int MAP_HANI = 15;// マップ範囲
	int camera_mode = 1; //カメラをどこに固定するか方向(上=0,下=1,左=2,右=3)

	//方向テーブル(上下左右の順)
	int dir_x[] = {0,0,-1,+1};
	int dir_y[] = {-1,+1,0,0};

	private final int CHIP_SIZE=diswidth/11;//チップサイズ
	final int MAX_EVENT_TABLE=100;//イベントテーブルの最大個数
	final int KAKUHO_NO=99;//確保イベントNO（キャラが次に着く位置）

	//マップデータ
	int now_map_no;//現在のマップNO
	int map_data[][] = new int[256][256];//256×256
	int map_width,map_height;//マップの横幅、高さ（チップ数）

	//イベントデータ
	int event_data[][] = new int[256][256];
	//イベントテーブル
	Event eventtbl[] = new Event[MAX_EVENT_TABLE];
	int event_num;//そのマップでのイベントの個数
	
	//ゲーム進行スイッチ
	boolean game_switch[] = new boolean[128];

	//------------ゲーム管理フラグ-------------
	//0=マップ移動（黒幕下がる）
	//1=マップ移動（黒幕上がる）
	//2=イベント中
	//3=会話イベント
	//4=文字が流れている途中
	//5=セーブ
	//7=デバッグフラグ
	//8=テーブル更新予約フラグ
	//9=ダイアログ中
	//10=スクリプトスキップ中
	boolean game_flags[] = new boolean[16];


	//------------ゲーム管理ウェイト-----------
	//0=黒い幕の昇降ウェイト
	//1=会話の流れる文字ウェイト
	float game_waits[] = new float[8];

	//------------ゲーム管理バッファ-----------
	//0=現在のイベントNO
	//1=読み込むマップNO
	//2=移動先X座標
	//3=移動先Y座標
	//4=移動後向き
	//5=現在のスクリプトの行
	//6=会話中の文字の位置。
	//7=現在選択中選択肢
	//8=選択肢の数
	int game_buffer[] = new int[16];

	//------------ゲーム管理文字列-----------
	//0=現在のスクリプトファイル名
	//1=現在の会話内容
	//2=分岐１
	//3=分岐２
	//4=分岐３
	String game_strings[] = new String[8];
	char game_talks[] = new char[256];//会話内容char配列

	//セルテーブル
	MapCell celltbl[] = {
		new MapCell(0,true),	//平原 00
		new MapCell(1,true),	//芝１01
		new MapCell(2,true),	//芝２02
		new MapCell(3,true),	//家 03
		new MapCell(4,false),	//木１04
		new MapCell(5,false),	//木２ 05
		new MapCell(6,true),	//城 06
		new MapCell(7,false),	//海 07
		new MapCell(8,true),	//塔（上）08
		new MapCell(9,true),	//塔（下）09
		new MapCell(10,true),	//洞窟 0a
		new MapCell(11,false), //岩山 0b
		new MapCell(12,true),	//宇宙１0c
		new MapCell(13,true),	//宇宙２0d
		new MapCell(14,false), //クリスタル 0e
		new MapCell(15,true),	//宇宙の城 0f
		new MapCell(16,true),	//真っ黒 10
		new MapCell(17,true),	//茶色 11
		new MapCell(18,false), //灰色 12
		new MapCell(19,true),	//ツボ 13
		new MapCell(20,true),	//空 14
		new MapCell(21,true),	//地面２ 15
		new MapCell(22,true),	//宝箱 16
		new MapCell(23,true),	//青壁 17
		new MapCell(24,true),	//黒 18
		new MapCell(25,false), //机 19
		new MapCell(26,true),	//川１1a
		new MapCell(27,true),	//川２ 1b
		new MapCell(28,false), //洞窟岩 1c
		new MapCell(29,true),	//洞窟地面 1d
		new MapCell(30,true),	//草原１ 1e
		new MapCell(31,true),	//草原２ 1f
		new MapCell(32,true),	//雪地面 20
		new MapCell(33,true),	//雪芝１ 21
		new MapCell(34,true),	//雪芝２ 22
		new MapCell(35,true),	//雪家 23
		new MapCell(36,false), //雪木 24
		new MapCell(37,true),	//雪城 25
		new MapCell(38,false), //雪池 26
		new MapCell(39,true),	//雪洞窟 27
		new MapCell(40,true),	//雪木 28
		new MapCell(41,true),	//雪芝３ 29
		new MapCell(42,true),	//木の壁 2a
	};

	//マップデータ構造
	MapFile mapfiles[] = {
		new MapFile(0,"サンプル草原",20,15),
		new MapFile(1,"最終決戦",64,64),
		new MapFile(2,"世界フィールド",64,64),
		new MapFile(3,"テッペの街",64,32),
		new MapFile(4,"雪山１",20,15),
		new MapFile(5,"雪山２",64,64),

		new MapFile(6,"雪山３",20,15),
		new MapFile(7,"麓の村",20,15),
		new MapFile(8,"オアシス村",20,15),
		new MapFile(9,"ゴーマの神殿",20,15),
		new MapFile(10,"ハルー博物館",20,15),
		new MapFile(11,"ラストダンジョン１",20,40),
		new MapFile(12,"ラストダンジョン２",40,20),
		new MapFile(13,"ラストダンジョン３",20,15),
		new MapFile(14,"ラストダンジョン４",40,15),
		new MapFile(15,"ラストダンジョン５",20,15),

		new MapFile(16,"ラストダンジョン６",20,15),
		new MapFile(17,"ラストダンジョン７",20,60),
		new MapFile(18,"ラストダンジョン８",20,15),
		new MapFile(19,"ラストダンジョン９",20,60),
		new MapFile(20,"ラストダンジョン１０",20,15),

		new MapFile(21,"Ａの塔１階",32,32),
		new MapFile(22,"Ａの塔２階",24,24),
		new MapFile(23,"Ａの塔４階",20,20),
		new MapFile(24,"Ａの塔３階",20,15),
		new MapFile(25,"スタッフロール",20,15),

		new MapFile(26,"幻の島",20,15),
		new MapFile(27,"幻の町",64,32),
		new MapFile(30,"学校",32,32),
		new MapFile(32,"アスファル１階",32,32),
		new MapFile(33,"アスファル２階",32,32),

		new MapFile(34,"洞窟１",32,32),
		new MapFile(35,"彼方",50,50),
		new MapFile(40,"川１",80,15),
		new MapFile(41,"川２",20,15),
		new MapFile(42,"川３",80,15),

		new MapFile(43,"海１",80,15),
		new MapFile(44,"海２",80,15),
		new MapFile(50,"宇宙",24,16),
		new MapFile(51,"魔王の城１階",32,32),
		new MapFile(52,"魔王の城２階",32,32),

	};
	final int MAXMAPNUM = 10;//マップの個数


	//コンストラクタ
	@Override	
	public rpgMain(Context context) {
		super(context);

		//画像の読み込み
		Resources r=getResources();
		map_image=BitmapFactory.decodeResource(r,R.drawable.map_chip);
		chara_image=BitmapFactory.decodeResource(r,R.drawable.chara_chip);
				
		//イベントテーブル初期化
		for(int i=0;i<64;i++){
			eventtbl[i] = new Event(i);
		}
		
		//サーフェイスホルダーの生成
		holder=getHolder();
		holder.addCallback(this);
		//タッチイベントとトラックボールイベントを使うために必須
		setFocusable(true);
		player.speed=diswidth/3;//速度/毎秒

		readMapData(8);

		//初期位置
		player.x = 2;
		player.y = 3;
		player.px = player.x*CHIP_SIZE;
		player.py = player.y*CHIP_SIZE;
		game_flags[5]=true;
		game_buffer[5]=0;
	}

	//マップデータを読み込む
	 private void readMapData(int mapno){
		int i,j,x,y;
		InputStream is = null;
		int id;
		String sid;

		//マップデータ＆イベントデータリセット
		for(i=0;i<256;i++){
			for(j=0;j<256;j++){
				event_data[i][j]=0;
				map_data[i][j]=0;
			}
		}
		now_map_no = mapno;
		//横幅、縦幅決定
		id=1;
		for(i=0;i<MAXMAPNUM;i++){
			if(mapno == mapfiles[i].no){
				id = i;
				break;
			}
		}

		map_width = mapfiles[id].width;
		map_height = mapfiles[id].height;

		//文字列変換
		if(mapfiles[id].no < 10){sid = "0"+mapfiles[id].no;}
		else{sid = ""+mapfiles[id].no;}

		//---------マップデータ読み込み-------------

		try{
			AssetManager as = getResources().getAssets(); 
			is = as.open("mapdata/map"+sid+".dat");
			x=y=0;
			while((i=is.read()) != -1){
				map_data[x][y]=i;
				if(++x == map_width){
					++y;
					x=0;
					if(y==map_width)break; //終了条件
				}
			}
			is.close();
		}catch(IOException e){}
		//---------イベントデータ読み込み-------------
		try{
			AssetManager as = getResources().getAssets(); 
			is = as.open("eventdata/event"+sid+".dat");
			x=y=0;
			while((i=is.read()) != -1){
				event_data[x][y]=(short)i;
				if(++x == map_width){
					++y;
					x=0;
					if(y==map_width)break; //終了条件
				}
			}
			is.close();
		}catch(IOException e){}

/**/

		//------------イベントテーブル更新----------------
		Load_Event_Table();

		//主人公の初期座標
		player.x = game_buffer[2];
		player.y = game_buffer[3];
		player.moving_dir = game_buffer[4];

		player.px = player.x*CHIP_SIZE;
		player.py = player.y*CHIP_SIZE;

		//フレーム時間初期化(ロード時間が長いので)
		waitTime = System.currentTimeMillis();
	}

	//イベントテーブル更新
	void Load_Event_Table(){
		int i,j,x,y;
		InputStream is = null;
		int id;
		String sid;
		char line[] = new char[256];
		String str="";
		int no=0;//イベントＮＯ保存用
		int sw=0;//スイッチ
		int maxno=0;

		//マップNOから添え字取得
		id=1;
		for(i=0;i<MAXMAPNUM;i++){
			if(now_map_no == mapfiles[i].no){
				id = i;
				break;
			}
		}
		//文字列変換
		if(mapfiles[id].no < 9){sid = "0"+mapfiles[id].no;}
		else{sid = ""+mapfiles[id].no;}

		x=y=0;
		try{
			AssetManager as = getResources().getAssets(); 
			is = as.open("eventtable/"+sid+".txt");

			while((i=is.read()) != -1){
				//一行ずつ読み込む
				line[x++] = (char)i;
				//改行がきた場合
				if(i == '\r'){continue;}
				if(i == '\n'){
					//改行コード以降切り捨てて、String型を読み込む
					String sline = Harulib.HaruChop(line);

					//条件を読む
					//現在はゲーム進行スイッチのみ
					str = Harulib.HaruSplit(sline,"<>",0);

					//NULLと書いてるものはそのまま実行
					if(str.equals("NULL")){;}
					else{
						//数字のものはゲーム進行スイッチにより読み込む
						sw = Integer.parseInt(str);

						//条件がマッチしているか？
						if(game_switch[sw]){
							;//続ける
						}else{
							//この行は破棄
							x=0;
							y++;
							continue;
						}
					}

					//イベントNO
					str = Harulib.HaruSplit(sline,"<>",1);
					no = Integer.parseInt(str);
					eventtbl[no].no = Integer.parseInt(str);

					//スクリプト名
					str = Harulib.HaruSplit(sline,"<>",2);
					eventtbl[no].filename = str;

					//グラフィックス
					str = Harulib.HaruSplit(sline,"<>",3);
					eventtbl[no].gra = Integer.parseInt(str);
					//System.out.println("y="+y+" eventtbl[y].gra="+eventtbl[y].gra);
					
					if(eventtbl[no].gra >= 1){
						eventtbl[no].type_of_event = 1;
					}

					//動作フラグ
					str = Harulib.HaruSplit(sline,"<>",4);
					if(str.equals("true")){
						eventtbl[no].move_flag = true;
					}else{
						eventtbl[no].move_flag = false;
					}

					//移動タイプ
					str = Harulib.HaruSplit(sline,"<>",5);
					eventtbl[no].type_of_move = Integer.parseInt(str);

					eventtbl[no].speed = diswidth/3;

					//最大値保存
					if(maxno < no){
						maxno = no;
					}

					x=0;
					y++;
				}
			}
			is.close();
		}catch(IOException e){}
		
		//イベントの数(最大値)
		event_num = maxno+1;

		//KAKUHO_NO番目に入れる
		//eventtbl[KAKUHO_NO].move_flag = true;
		//eventtbl[KAKUHO_NO].type_of_event = 1;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchX = event.getX();
		touchY = event.getY();
		float dx=20.0f;
		float dy=20.0f;
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN : 
				touch_downX=event.getX();
				touch_downY=event.getY();
				break;
			case MotionEvent.ACTION_UP : 
				dx=touchX-touch_downX;
				dy=touchY-touch_downY;
				touch_downX=-1;
				touch_downY=-1;
				key_states[0] = 0;
				key_states[1] = 0;
				key_states[2] = 0;
				key_states[3] = 0;
				key_states[4] = 0;
				break;
		}
		if(touch_downX>-1&&touch_downY>-1&&!game_flags[9]){
			float control_keyX=touchX-touch_downX;
			float control_keyY=touchY-touch_downY;
			if(Math.abs(control_keyX)<Math.abs(control_keyY)){
				key_states[2] = 0;
				key_states[3] = 0;
				if(Math.abs(control_keyY)>15){
					if(control_keyY>0){
						key_states[1] = 1;
						key_states[4] = 1;
					}else{
						key_states[0] = 1;
						key_states[4] = 1;
					}
				}else{
				}
			}else if(Math.abs(control_keyX)>Math.abs(control_keyY)){
				key_states[0] = 0;
				key_states[1] = 0;
				if(Math.abs(control_keyX)>15){
					if(control_keyX>0){
						key_states[3] = 1;
						key_states[4] = 1;
					}else{
						key_states[2] = 1;
						key_states[4] = 1;
					}
				}else{
				}
			}
		}
		if(Math.abs(dx)<15&&Math.abs(dy)<15&&key_states[4]!=1){
			if(game_flags[9]){
				if(touchX>diswidth/4&&touchX<diswidth/2&&touchY>disheight-diswidth/4) key_states[0] = 2;
				else if(touchX>diswidth/2&&touchX<diswidth/4*3&&touchY>disheight-diswidth/4) key_states[1] =2;
				else key_states[4]=2;
			}
			else key_states[4]=2;
		}
		return true;
	}

	//サーフェイスの生成
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		waitTime=System.currentTimeMillis();//現在時刻保存
		//スレッドの開始
		thread=new Thread(this);
		thread.start();
	}

	//サーフェイスの変更
	@Override
	public void surfaceChanged(SurfaceHolder holder,
		int format,int w,int h) {
	}	
	
	//サーフェイスの破棄
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread=null;
	}

	//ループ処理
	@Override
	public void run() {
		while(thread!=null) {
			//１フレーム時間計算（秒）
			FrameTime = ( System.currentTimeMillis()-waitTime ) /1000.0f;
			waitTime = System.currentTimeMillis();
			update();
			try {
				Thread.sleep(10);
			}catch (Exception e) {
			}
		}
	}

	private void update(){
		move();
		paint();
		//キー状態で移動処理
		if(game_flags[0] || game_flags[1] || game_flags[2]|| game_flags[3]){;}
		else{
			KeyForMove();
		}

		//Zキー（決定キー）を押した時（）
		if(key_states[4] == 2){
			//決定キーのいろんな処理
			ZAction();

			//キーの状態を押し続けに変更。
			key_states[4] = 3;
		}

		//上下キーを押した時
		for(int i=0;i<2;i++){
			if(key_states[i] == 2&&game_flags[9]){
				//上の場合
				if(i==0){
					if(game_buffer[7] > 0)game_buffer[7]--;
				}
				//下の場合
				else if(i==1){
					if(game_buffer[7] < game_buffer[8]-1)game_buffer[7]++;
				}

				key_states[i] = 0;
			}
		}

		//会話中の場合、文字が流れていく
		if(game_flags[3]){
			//0.05秒に1文字
			game_waits[1] += FrameTime;
			if(game_waits[1] >= 0.05f){
				game_waits[1] = 0.0f;
				game_buffer[6]++;
				if(game_buffer[6] > game_strings[1].length()){
					game_buffer[6] = game_strings[1].length();
					//完了
					game_flags[4] = false;
				}
			}
		}

		//移動中の場合
		if(player.moving_flag){
			MyMoving();
			//アニメーション(0.2秒に1回)
			player.anime_wait += FrameTime;
			if(player.anime_wait >= 0.2f){
				player.anime_no = 1 - player.anime_no;
				player.anime_wait = 0.0f;
			}
		}
		//イベント中
		if(game_flags[2]&&game_flags[5]){
			//マップ移動時は停止中
			if(game_flags[0] || game_flags[1]){;}
			else{
				ActionScript();//スクリプト実行
			}
		}

		//イベント移動（エフェクト中、イベント中、会話中は受け付けない）
		if(game_flags[0] || game_flags[1] || game_flags[2] || game_flags[3]){;}
		else{
			//EventMove();
		}

		//マップ移動開始（幕閉じ）
		if(game_flags[0]){
			//黒い幕が降りてくる(1.1秒)
			game_waits[0] += FrameTime;
			if(game_waits[0] >= 1.1f){
				game_flags[0] = false;
				game_flags[1] = true;
				//マップロード
				readMapData(game_buffer[1]);
			}
		}
		//マップ移動２（幕開け）
		else if(game_flags[1]){
			//黒い幕が上っていく(1.2秒)
			game_waits[0] -= FrameTime;
			if(game_waits[0] <= 0.0f){
				game_flags[1] = false;
				game_waits[0] = 0.0f;
				game_flags[5]=true;
			}
		}
	}

	//イベント移動開始
	void EventMove(){
		int i;
		int ran;
		int dir;
		int next_x,next_y;
		boolean ismove;

		for(i=0;i<event_num;i++){
			//動くタイプの時
			if(eventtbl[i].type_of_move == 1){
				//休憩中の時
				if(eventtbl[i].sum_move_length < 0.0f){
					eventtbl[i].sum_move_length += FrameTime;
				}else
				//現在、移動中でないなら移動開始（２秒ペースで休憩）
				if(!eventtbl[i].moving_flag){
					//ランダムに方向を決める
					ran=rand.nextInt();
					if(ran<0){ran=-ran;}
					dir = ran%4;//方向

					//その方向は通行可能か？
					next_x = eventtbl[i].x + dir_x[dir];
					next_y = eventtbl[i].y + dir_y[dir];


					//領域外
					if(next_x < 0 || next_y >= map_width || 
						next_y < 0 || next_y >= map_height){
					 	//通行不可
					 	ismove = false;
					 }
					//障害物
					else if(!celltbl[ map_data[next_x][next_y] ].move_flag){
					 	//通行不可
					 	ismove = false;
					}
					//イベント
					else if(event_data[next_x][next_y] >= 1){
						//通行不可
						ismove = false;
					}
					//主人公の位置
					else if(next_x == player.x && next_y == player.y){
						//通行不可
						ismove = false;
					}
					//それ以外
					else{
					 	//通行可
					 	ismove = true;

					 	//移動開始
					 	eventtbl[i].sum_move_length = 0.0f;
					 	eventtbl[i].moving_dir = dir;

						//移動先イベントデータにKAKUHO_NOを入れる
						event_data[next_x][next_y] = KAKUHO_NO;
					}

					eventtbl[i].moving_flag = ismove;
				}
				//移動中の時
				else{
					//その方向に向かって移動
					CharaMoving( eventtbl[i] );
				}
			}
		}
	}

	private void paint() {
		//ダブルバッファリング
		Canvas canvas=holder.lockCanvas();
		if (canvas==null) return;
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		RectF bounds = new RectF(0, 0, diswidth, diswidth);
		DispMap(canvas);
		DispMy(canvas);
		underscreen(canvas);

		//黒い幕（マップ移動時）
		if(game_flags[0] || game_flags[1]){
			paint.setColor(Color.BLACK);
			canvas.drawRect(0,0,diswidth,(int)(disheight*game_waits[0]),paint);
		}

		//会話
		if(game_flags[3] || game_flags[9]){
			//ウインドウを作る(下1/3)
			paint.setColor(Color.BLACK);
			canvas.drawRect(0,(int)diswidth,(int)diswidth,(int)disheight,paint);
			paint.setTextSize(CHIP_SIZE/2);
			//文字
			paint.setColor(Color.WHITE);

			//一文字ずつ書いていく

			int x,y;//描画位置
			x=y=0;
			for(int i=0;i<game_buffer[6];i++){
				//19文字で改行
				if(x==19){
					x=0;
					y++;
				}
				canvas.drawText(String.valueOf(game_talks[i]),CHIP_SIZE/2+x*CHIP_SIZE/2,diswidth+CHIP_SIZE/2 + y*CHIP_SIZE/2,paint);
				x++;
			}
		}

		//ダイアログ
		if(game_flags[9]){
			//ウインドウを作る(真ん中上)
			paint.setColor(Color.BLACK);
			canvas.drawRect(diswidth/4,diswidth/3*2,diswidth/4+CHIP_SIZE/2*9,diswidth/3*2+(CHIP_SIZE/2)*game_buffer[8]+10,paint);

			//文字配置
			paint.setColor(Color.WHITE);
			for(int i=0;i<game_buffer[8];i++){
				canvas.drawText(game_strings[i+2],diswidth/4+CHIP_SIZE/2,diswidth/3*2+ CHIP_SIZE/2+i*CHIP_SIZE/2,paint);
			}

			int x = (27%16)*64;
			int y = (27/16)*64;

				Rect srcRect1 = new Rect(x,y,x+64,y+64);
				Rect destRect1 = new Rect(diswidth/4,disheight-diswidth/4,diswidth/2, disheight);
				canvas.drawBitmap(chara_image,srcRect1,destRect1,paint);

			x = (28%16)*64;
			y = (28/16)*64;

				srcRect1 = new Rect(x,y,x+64,y+64);
				destRect1 = new Rect(diswidth/2,disheight-diswidth/4,diswidth/4*3, disheight);
				canvas.drawBitmap(chara_image,srcRect1,destRect1,paint);

			//カーソル
			//キャラチップからグラフィックスの位置を取得
			x = (197%16)*64;
			y = (197/16)*64;

				srcRect1 = new Rect(x,y,x+64/2,y+64/2);
				destRect1 = new Rect(diswidth/4, diswidth/3*2+2 +game_buffer[7]*CHIP_SIZE/2,diswidth/4+CHIP_SIZE/2, diswidth/3*2+ 2+game_buffer[7]*CHIP_SIZE/2+CHIP_SIZE/2);
				canvas.drawBitmap(chara_image,srcRect1,destRect1,paint);

		}


		holder.unlockCanvasAndPost(canvas);
	}

	private void move(){
		// mode1 主人公の中心
		if( camera_mode == 1 ){
			camera_px = player.px + (CHIP_SIZE/2);
			camera_py = player.py + (CHIP_SIZE/2);
		}
		// mode2 城
		else{
			camera_px = CHIP_SIZE*8 + (CHIP_SIZE/2);
			camera_py = CHIP_SIZE*4 + (CHIP_SIZE/2);
		}

		int half_diswidth=diswidth/2;
		//限界値チェック
		if(camera_px < half_diswidth)camera_px = half_diswidth;
		if(camera_py < half_diswidth)camera_py = half_diswidth;
		if(camera_px > (map_width*CHIP_SIZE - half_diswidth) ){
			camera_px = (map_width*CHIP_SIZE - half_diswidth);
		}
		if(camera_py > (map_height*CHIP_SIZE - half_diswidth)){
			camera_py = (map_height*CHIP_SIZE - half_diswidth);
		}
	}

	//---------------------画面下------------------------
	void underscreen(Canvas canvas){
		paint.setTextSize(CHIP_SIZE/3);
		canvas.drawText(mapfiles[now_map_no].mapname+" (" +player.x+ "," +player.y+ ")   Mapsize("+map_width+","+map_height+")",0,CHIP_SIZE/3*1,paint);
		canvas.drawText("fps: "+(int)(1/FrameTime),0,CHIP_SIZE/3*2,paint);
		int half_chip=CHIP_SIZE/2;
		paint.setStyle(Paint.Style.FILL);
		if(touch_downX>-1&&touch_downY>-1){
			canvas.drawCircle(touch_downX,touch_downY,CHIP_SIZE,paint);
		}
	}

	//---------------------マップ描画------------------------
	void DispMap(Canvas canvas){
		int i,j,x,y;
		int cell_data,gra;
		int start_x;
		int start_y;
		int end_x;
		int end_y;
		int dsp_x,dsp_y;

 
		//マップデータの範囲（カメラからMAP_HANIチップ分）
		start_x = (int)camera_px/CHIP_SIZE - 9;
		start_y = (int)camera_py/CHIP_SIZE - 9;
		end_x = start_x + 2*9;
		end_y = start_y + 2*20;

		//描画位置ＩＪ
		int disp_start_i,disp_i,disp_start_j,disp_j;
		disp_start_i = start_y;
		disp_start_j = start_x;

		//限界
		if(start_x < 0){start_x = 0;}
		if(start_y < 0){start_y = 0;}
		if(end_x > map_width){end_x = map_width;}
		if(end_y > map_height){end_y = map_height;}

		for(i=start_y,disp_i=disp_start_i; ;i++,disp_i++){
			//終了
			if(i == end_y){break;}
			for(j=start_x,disp_j=disp_start_j; ;j++,disp_j++){
				//終了
				if(j == end_x){break;}

				//セルデータ取得
				cell_data = map_data[j][i];

				//セルデータからマップグラフィックスを取得
				gra = celltbl[ cell_data ].gra;

				//マップ座標(ワールド座標)
				dsp_x = j*CHIP_SIZE;
				dsp_y = i*CHIP_SIZE;

				//座標変換
				dsp_x = WorldToScreenX( dsp_x );
				dsp_y = WorldToScreenY( dsp_y );

				//マップチップからグラフィックスの位置を取得
				x = (gra%16)*64;
				y = (gra/16)*64;

				Rect srcRect1 = new Rect(x+1,y+1,x+63,y+63);
				Rect destRect1 = new Rect(dsp_x,dsp_y,dsp_x+CHIP_SIZE,dsp_y+CHIP_SIZE);
				canvas.drawBitmap(map_image,srcRect1,destRect1,paint);

				//---------次はイベント描画-------------

				//イベントデータからグラフィックスを取得
				gra = eventtbl[ event_data[j][i] ].gra;

				//グラフィックスが-1の時はスキップ
				if(gra == -1){}
				else{
					//グラフィックスの位置を取得
					x = (gra%16)*64;
					y = (gra/16)*64;

					Rect srcRect3 = new Rect(x,y,x+64,y+64);
					Rect destRect3 = new Rect(dsp_x-2,dsp_y-2,dsp_x+CHIP_SIZE+2,dsp_y+CHIP_SIZE+2);
					canvas.drawBitmap(chara_image,srcRect3,destRect3,paint);

				}
				
				/*イベントがある場合はＥマークを表示（デバッグ用）
				if(event_data[j][i] >= 1 && game_flags[7]){
					グラフィックスの位置を取得
					x = (13%16)*CHIP_SIZE;
					y = (13/16)*CHIP_SIZE;

					bufferg.drawImage(chara_image,dsp_x,dsp_y,
					 dsp_x+CHIP_SIZE,dsp_y+CHIP_SIZE,x,y,x+CHIP_SIZE,y+CHIP_SIZE,this);
*/
			}
		}
	}

	//--------------主人公描画------------------
	void DispMy(Canvas canvas){
		//描画開始位置
		float view_x;
		float view_y;
		//座標変換
		view_x = WorldToScreenX( player.px );
		view_y = WorldToScreenY( player.py );

		//描画
		//チップNO定義
		int no = player.chip_no + player.moving_dir*2 + player.anime_no;
		//チップNOからグラフィックスの位置を取得
		int x = (no%16)*64;
		int y = (no/16)*64;
		Rect srcRect2 = new Rect(x,y,x+64,y+64);
		Rect destRect2 = new Rect((int)view_x,(int)view_y,(int)view_x+CHIP_SIZE,(int)view_y+CHIP_SIZE);
		canvas.drawBitmap(chara_image,srcRect2,destRect2,paint);
	}

	// ワールド座標からスクリーン座標へ
	int WorldToScreenX( float p ){
		return (int)(p - camera_px+diswidth/2);
	}
	int WorldToScreenY( float p ){
		return (int)(p - camera_py+diswidth/2);
	}

	//Zキーの処理
	void ZAction(){
		//ダイアログ中は決定
		if(game_flags[9]){
			game_flags[9] = false;
			System.out.println("Done:Dialog");
			game_flags[5]=true;
		}
		//会話中の時はウインドウを消す(文字が流れ終わった時)
		if(game_flags[3]){
			if(!game_flags[4]){
				game_flags[3] = false;
				game_flags[5]=true;
			}
			else game_buffer[6] = game_strings[1].length();
			game_flags[4] = false;
		}
		//そうでなくてイベント中でないなら、イベントチェック
		else if(!game_flags[2]){
			//その方向にイベントがあるかチェック
			//決定ボタンが押されると実行するものを実行
			check_event(player.x + dir_x[player.moving_dir] , player.y + dir_y[player.moving_dir],1);
		}
	}



	//キー状態で移動処理
	
	//キー状態で移動処理
	public void KeyForMove(){
		int i;
		int next_x,next_y;
		boolean ismove;

		//上下左右キー
		for(i=0;i<4;i++){
			if(key_states[i] == 1){
				//現在移動中か？
				if(!player.moving_flag){
					//移動中でないなら移動
					player.moving_dir = i;

					//その方向は通行可能か？
					next_x = player.x + dir_x[player.moving_dir];
					next_y = player.y + dir_y[player.moving_dir];

					/*世界マップならループさせる
					if(now_map_no == 2){
						if(next_x < 0){next_x += map_width;}
						if(next_x >= map_width){next_x -= map_width;}
						if(next_y < 0){next_y += map_height;}
						if(next_y >= map_height){next_y -= map_height;}
					}*/

					//領域外(通常マップ時)
					if(next_x < 0 || next_y >= map_width || 
						next_y < 0 || next_y >= map_height){
						//通行不可
						ismove = false;
					}
					//障害物
					else if(!celltbl[ map_data[next_x][next_y] ].move_flag){
					 	//通行不可
					 	ismove = false;
					}
					//イベントで、通行不可タイプ
					else if(!eventtbl[ event_data[next_x][next_y] ].move_flag){
					 	//通行不可
					 	ismove = false;
					}
					//確保イベント
					else if(event_data[next_x][next_y] == KAKUHO_NO){
					 	//通行不可
					 	ismove = false;
					}
					//それ以外
					else{
					 	//通行可
					 	ismove = true;

					 	//移動開始
					 	player.sum_move_length = 0.0f;

						//移動先イベントデータにKAKUHO_NOを入れる(0の時)
						if(event_data[next_x][next_y] == 0){
							//event_data[next_x][next_y] = KAKUHO_NO;
						}
					}
					player.moving_flag = ismove;
				}
			}
		}
	}

	//自分移動
	public void MyMoving(){
		//その方向に移動
		player.px += FrameTime * player.speed * dir_x[player.moving_dir];
		player.py += FrameTime * player.speed * dir_y[player.moving_dir];

		//総合距離加算
		player.sum_move_length += FrameTime * player.speed;

		//一定距離動いたか
		if(player.sum_move_length >= (float)CHIP_SIZE){
			//ピッタリの位置にする
			player.x += dir_x[player.moving_dir];
			player.y += dir_y[player.moving_dir];

			/*世界マップの時
			if(player.x < 0){player.x += map_width;}
			if(player.x >= map_width){player.x -= map_width;}
			if(player.y < 0){player.y += map_height;}
			if(player.y >= map_height){player.y -= map_height;}*/

			player.px = player.x*CHIP_SIZE;
			player.py = player.y*CHIP_SIZE;
			player.moving_flag = false;

			//この位置の確保イベントを消す(KAKUHO_NOの時)
			if(event_data[player.x][player.y] == KAKUHO_NO){
				event_data[player.x][player.y] = 0;
			}

			//イベントチェック
			//その上に重なると起動するもの。
			check_event(player.x,player.y,0);

		}
	}

	//キャラ移動
	void CharaMoving(Event event){
		int ran;

		//その方向に移動
		event.px += FrameTime * event.speed * dir_x[event.moving_dir];
		event.py += FrameTime * event.speed * dir_y[event.moving_dir];

		//総合距離加算
		event.sum_move_length += FrameTime * event.speed;

		//一定距離動いたか
		if(event.sum_move_length >= (float)CHIP_SIZE){
			//前の位置のイベントを消す
			event_data[event.x][event.y] = 0;

			//ピッタリの位置にする
			event.x += dir_x[event.moving_dir];
			event.y += dir_y[event.moving_dir];
			event.px = event.x*CHIP_SIZE;
			event.py = event.y*CHIP_SIZE;
			event.moving_flag = false;
			event_data[event.x][event.y] = event.no;

			//小休止(ランダム１秒～３秒)
			ran=rand.nextInt();
			if(ran<0){ran=-ran;}

			event.sum_move_length = -(ran%3+1);
		}
	}

	//その場所のイベントチェック
	void check_event(int x,int y,int type){
		//領域外の時
		if(x < 0 || y < 0 || x >= map_width || y >= map_height)return;

		//イベントがあった場合
		if(event_data[x][y] != 0 && event_data[x][y] != KAKUHO_NO){
			//上に乗った時。それは重なると起動するものか？
			if(type == 0 && !eventtbl[ event_data[x][y] ].move_flag){
				//違う場合は抜ける
				return;
			}
			
			//決定ボタンを押した時。それは遠くから起動するものか？
			if(type == 1 && eventtbl[ event_data[x][y] ].move_flag){
				//違う場合は抜ける
				return;
			}

			game_buffer[0] = eventtbl[ event_data[x][y] ].no;//イベントNO記憶
			game_flags[2] = true;//イベントフラグセット
			//スクリプトファイル名決定
			game_strings[0] = "eventscript/"+eventtbl[ game_buffer[0] ].filename+".txt";
		}
	}

	//スクリプト実行
	void ActionScript(){
		String filename;
		int i,x;
		char line[] = new char[256];
		InputStream is = null;

		//指定の行まで行く
		int line_num = 0;
		boolean done_flag=true;//最後まで読みきったか
		boolean next_flag;//次の行も読み取るか
		try{
			
			AssetManager as = getResources().getAssets(); 
			is = as.open("eventscript/"+eventtbl[ game_buffer[0] ].filename+".txt");
			BufferedReader r = new BufferedReader(new InputStreamReader(is));

			x=0;
			while((i=r.read()) != -1){
				//一行ずつ読み込む
				line[x++] = (char)i;

				//改行がきた場合
				if(i == '\n'){

					//その行は読み込む行か？
					if(line_num == game_buffer[5]){
						//そうであった場合、読み込んで解析
						next_flag = AnalyzeScript(line);
						game_buffer[5]++;

						//マップ移動、会話イベント、ダイアログなら抜ける
						if(!next_flag){
							done_flag = false;
							break;
						}
						//そうでないならそのまま読み込み続ける
						line_num++;
					}else{
						//違うなら、改行数増やして探索続行
						line_num++;
					}
					x=0;
				}
			}
			is.close();
		}catch(IOException e){}

		//最後まで読み切った場合は、フラグ解除
		if(done_flag){
			game_flags[2] = false;
			game_buffer[5] = 0;

			//テーブル更新予約があればテーブル更新
			if(game_flags[8]){
				Load_Event_Table();
				game_flags[8] = false;
			}
		}
	}

	//指定の行を読み込んで解析(戻り値がある時はそれを実行し次の行へ続く)
	public boolean AnalyzeScript(char line[]){
		int i,x,y;
		String command;
		String value;
		String buffer;
		boolean next_flag=true;

		//改行コード以降切り捨て（改行含む）
		String sline = Harulib.HaruChop(line);

		//コマンド取得
		command = Harulib.HaruSplit(sline,":",0);

		//値取得
		value = Harulib.HaruSplit(sline,":",1);

		//ラベル確認
		if(command.equals("IF_SCENE1")){
			//そのラベル以降は実行すべきか？
			if(game_buffer[7] == 0){
				game_flags[10] = false;//スキップ解除してそのまま実行
			}
			else{
				game_flags[10] = true;
			}
		}else if(command.equals("IF_SCENE2")){
			//そのラベル以降は実行すべきか？
			if(game_buffer[7] == 1){
				game_flags[10] = false;//スキップ解除してそのまま実行
			}
			else{
				game_flags[10] = true;
			}
		}else if(command.equals("IF_SCENE3")){
			//そのラベル以降は実行すべきか？
			if(game_buffer[7] == 2){
				game_flags[10] = false;//スキップ解除してそのまま実行
			}
			else{
				game_flags[10] = true;
			}
		}else if(command.equals("END_SCENE")){
			//スキップ終了
			game_flags[10] = false;
		}

		//スキップ実行の時
		if(game_flags[10]){
			return true;//抜ける
		}

		//コマンドによってイベント実行

		//マップ移動コマンド
		if(command.equals("move")){

			//移動イベント開始
			game_flags[0] = true;

			//値を解析

			buffer = Harulib.HaruSplit(value,"=",0);
			game_buffer[1] = Integer.parseInt(buffer);//移動先マップNO

			buffer = Harulib.HaruSplit(value,"=",1);
			game_buffer[2] = Integer.parseInt(buffer);//X

			buffer = Harulib.HaruSplit(value,"=",2);
			game_buffer[3] = Integer.parseInt(buffer);//Y

			buffer = Harulib.HaruSplit(value,"=",3);
			game_buffer[4] = Integer.parseInt(buffer);//方向
			
			next_flag=false;
		}

		//会話コマンド
		else if(command.equals("talk")){

			//会話イベント開始
			game_flags[3] = true;

			//流れ始める。
			game_flags[4] = true;

			//会話
			game_strings[1] = value;

			//char配列にコピー
			game_strings[1].getChars(0,game_strings[1].length(),game_talks,0);

			//文字の位置
			game_buffer[6] = 1;//１文字目
			
			next_flag=false;
		}

		//ゲーム進行スイッチオンコマンド
		else if(command.equals("switch_on")){
			System.out.println("Do:switch_on");

			game_switch[ Integer.parseInt(value) ] = true;

			//テーブル更新予約
			game_flags[8] = true;
		}
		//分岐1
		else if(command.equals("SCENE1")){
			System.out.println("Do:SCENE1");
			game_strings[2] = value;
			game_buffer[8] = 1;
		}
		//分岐2
		else if(command.equals("SCENE2")){
			System.out.println("Do:SCENE2");
			game_strings[3] = value;
			game_buffer[7] = 0;
			game_buffer[8] = 2;//選択肢の数
		}
		//分岐3
		else if(command.equals("SCENE3")){
			System.out.println("Do:SCENE3");
			game_strings[4] = value;
			game_buffer[7] = 0;
			game_buffer[8] = 3;//選択肢の数
		}
		//ダイアログを出す
		else if(command.equals("DIALOG")){
			game_flags[9] = true;
			next_flag=false;
		}

		game_flags[5]=next_flag;
		return next_flag;
	}



}
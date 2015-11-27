package jp.trasta.rpg;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.*;

public class MainActivity extends Activity {
	rpgMain rpg;
	//アクティビティ起動時に呼ばれる
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		rpg=new rpgMain(this);
		setContentView(rpg);
	}
	// BACKボタンが押された時の処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			// アラートダイアログ
			rpg.onbackkey();
			return true;
		}
		return false;
	}
}

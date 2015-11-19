package jp.trasta.rpg;

//はるライブラリ
class Harulib{
	/********************************************************************************/
	/*                                                                              */
	/*  ある文字列で分割 buf = HaruSplit("aa<>bb<>cc<>dd","<>",1) ---> buf = "bb"   */
	/*                                                                              */
	/********************************************************************************/
	static String HaruSplit(String sline,String c,int turn){
		char token[] = new char[ c.length() ];//分割文字列
		int i,j,k;
		boolean search_flag;
		int count=0;
		char buffer[] = new char[sline.length()+1 ];//バッファ
		String str;
		char line[] = new char[sline.length() ];//文字配列

		//System.out.println("Do:HaruSplit()");
		//System.out.println("sline = "+sline+",turn = "+turn);

		//トークン(区切り文字列)を文字配列に。
		c.getChars(0,c.length(),token,0);

		//文字列をchar配列に。
		sline.getChars(0,sline.length(),line,0);

		//System.out.println("token = "+String.valueOf(token)+",line = "+String.valueOf(line));

		k=0;
		search_flag = false;
		for(i=0;i<line.length;i++){
			//最初の一文字が見つかった
			if(line[i] == token[0]){
				//フラグセット
				search_flag = true;
				//最後まで検索
				for(j=0;j<token.length;j++){
					if(line[i+j] != token[j]){
						//違ったらフラグ解除
						search_flag = false;
						break;
					}
				}

				//見つかった
				if(search_flag){
					if(turn == count){break;}//終了条件
					i+=token.length-1;
					count++;
					k=0;
					buffer[k]=0;
					continue;
				}
			}
			buffer[k] = line[i];
			k++;
			buffer[k] = 0;
		}
		
		//もしフラグがないようなら、空白文字として返す
		if(!search_flag){
			return "";
		}

		//終端記号までの文字数を得る
		int length = HaruStrlen(buffer);

		//新しいcharに。
		char cbuffer[] = new char[length];
		for(i=0;i<length;i++){
			cbuffer[i] = buffer[i];
		}
		
		//char -> string
		str = String.valueOf(cbuffer);

		//System.out.println("str = "+str);
		//System.out.println("Done:HaruSplit()");

		return str;
	}

	//終端記号までの文字数を得る
	static int HaruStrlen(char buffer[]){
		int i;

		for(i=0;i<buffer.length;i++){
			if(buffer[i] == 0){
				break;
			}
		}
		
		return i;
	}

	//改行コード以降切捨ててStringを返す
	static String HaruChop(char line[]){
		int i;
		int p;
		boolean flag;

		//System.out.println("Do:HaruChop()");

		flag = false;
		p=0;
		for(i=0;i<line.length;i++){
			if(!flag && (line[i] == '\r' || line[i] == '\n') ){
				flag = true;
				//その位置を記憶
				p = i;
			}

			if(flag){
				line[i] = 0;
			}
		}
		
		//文字列バッファを確保
		StringBuffer sb = new StringBuffer(p);
		
		//入れる
		for(i=0;i<p;i++){
			sb.append(line[i]);
		}
		//System.out.println("sb.toString() = "+sb.toString());
		//System.out.println("Done:HaruChop()");
		
		return sb.toString();
	}
}

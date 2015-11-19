package jp.trasta.rpg;

//�͂郉�C�u����
class Harulib{
	/********************************************************************************/
	/*                                                                              */
	/*  ���镶����ŕ��� buf = HaruSplit("aa<>bb<>cc<>dd","<>",1) ---> buf = "bb"   */
	/*                                                                              */
	/********************************************************************************/
	static String HaruSplit(String sline,String c,int turn){
		char token[] = new char[ c.length() ];//����������
		int i,j,k;
		boolean search_flag;
		int count=0;
		char buffer[] = new char[sline.length()+1 ];//�o�b�t�@
		String str;
		char line[] = new char[sline.length() ];//�����z��

		//System.out.println("Do:HaruSplit()");
		//System.out.println("sline = "+sline+",turn = "+turn);

		//�g�[�N��(��؂蕶����)�𕶎��z��ɁB
		c.getChars(0,c.length(),token,0);

		//�������char�z��ɁB
		sline.getChars(0,sline.length(),line,0);

		//System.out.println("token = "+String.valueOf(token)+",line = "+String.valueOf(line));

		k=0;
		search_flag = false;
		for(i=0;i<line.length;i++){
			//�ŏ��̈ꕶ������������
			if(line[i] == token[0]){
				//�t���O�Z�b�g
				search_flag = true;
				//�Ō�܂Ō���
				for(j=0;j<token.length;j++){
					if(line[i+j] != token[j]){
						//�������t���O����
						search_flag = false;
						break;
					}
				}

				//��������
				if(search_flag){
					if(turn == count){break;}//�I������
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
		
		//�����t���O���Ȃ��悤�Ȃ�A�󔒕����Ƃ��ĕԂ�
		if(!search_flag){
			return "";
		}

		//�I�[�L���܂ł̕������𓾂�
		int length = HaruStrlen(buffer);

		//�V����char�ɁB
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

	//�I�[�L���܂ł̕������𓾂�
	static int HaruStrlen(char buffer[]){
		int i;

		for(i=0;i<buffer.length;i++){
			if(buffer[i] == 0){
				break;
			}
		}
		
		return i;
	}

	//���s�R�[�h�ȍ~�؎̂Ă�String��Ԃ�
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
				//���̈ʒu���L��
				p = i;
			}

			if(flag){
				line[i] = 0;
			}
		}
		
		//������o�b�t�@���m��
		StringBuffer sb = new StringBuffer(p);
		
		//�����
		for(i=0;i<p;i++){
			sb.append(line[i]);
		}
		//System.out.println("sb.toString() = "+sb.toString());
		//System.out.println("Done:HaruChop()");
		
		return sb.toString();
	}
}

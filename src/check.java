
public class check {

	static String trainPath="big.txt";					//ѵ���ļ�·��
	static String dicPath = "englishDictionary.txt";	//�ֵ�·��
	static String englishPath = "english.txt";			//Ҫ����ļ�·��
	static suggest su=null;

	public static void main(String[] args) {
		su =new suggest(dicPath,trainPath); 			
		su.check(englishPath);
		System.out.println("1234");
	}

}

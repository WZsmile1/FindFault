
public class check {

	static String trainPath="big.txt";					//训练文件路径
	static String dicPath = "englishDictionary.txt";	//字典路径
	static String englishPath = "english.txt";			//要检查文件路径
	static suggest su=null;

	public static void main(String[] args) {
		su =new suggest(dicPath,trainPath); 			
		su.check(englishPath);
		System.out.println("1234");
	}

}

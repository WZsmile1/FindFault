import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class suggest {

	HashSet<String> dictionary = new HashSet<>();
	Map<String, Integer> map = new HashMap<String, Integer>();
	static String dicPath;
	ArrayList<String> english = new ArrayList<String>();
	
	public suggest(String dicpath,String trainpath){			//词典，训练文件
		dicPath=dicpath;
		dictionary=read(dicpath);
		map=train(trainpath);						
	}
	
	HashSet<String> read(String dicpath) {					//读字典
		HashSet<String> A = new HashSet<String>();
		try {
			File filename = new File(dicpath); 					
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader); 
			String line = "";
			while ((line = br.readLine()) != null) {// 一次读入一行数据
				A.add(line);
			}
			reader.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return A;
	}

	void Add(String s) {				//将单词加入字典
		try {
			RandomAccessFile randomFile = new RandomAccessFile(dicPath, "rw");// 打开一个随机访问文件流，按读写方式
			long fileLength = randomFile.length();// 文件长度，字节数
			randomFile.seek(fileLength);// 将写文件指针移到文件尾。
			randomFile.writeBytes("\r\n" + s);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Map<String, Integer> train(String trainpath) {		//统计文件中单词出次数，根据次数对推荐序列排序
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			InputStream is = new FileInputStream(trainpath);			// 读取文件big.txt
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s = "";
			while ((s = br.readLine()) != null) {
				s = s.replaceAll("\\pP|\\pS|\\pM|\\pN|\\pC", " ");		// 去掉文档中除字母外的所有符号
				s = s.toLowerCase();			// 将文档转成小写，然后切分成单词，存在list中
				String[] splits = s.split(" ");
				for (int j = 0; j < splits.length; j++) {
					if (!splits[j].equals(" ") && !splits[j].equals(null)&& !splits[j].equals("")) {
						if (map.containsKey(splits[j])) {
							Integer count = map.get(splits[j]);
							map.put(splits[j], count + 1);
						} else {
							map.put(splits[j], 1);
						}
					}
				}
			}
			is.close();
		} catch (IOException e) {
			System.out.println("Don't find big.txt.");
		}
		return map;
	}
	
	List<Entry<String, Integer>> suggestword(String word) {		//推荐
		ArrayList<String> edit = editErrorWord(word);
		HashSet<String> list = new HashSet<>();
		for (String s : edit)
			if (dictionary.contains(s)) {
				list.add(s);
				if (dictionary.contains(s + "s"))
					list.add(s + "s");
				if (dictionary.contains(s + "es"))
					list.add(s + "es");
				if (dictionary.contains(s + "er"))
					list.add(s + "er");
			}
		if (list.size() < 3)
			for (String s : edit)
				for (String w : editErrorWord(s))
					if (dictionary.contains(w))
						list.add(w);
		Map<String, Integer> wordsMap = new HashMap<String, Integer>();
		for (String s : list) {
			wordsMap.put(s, map.get(s) == null ? 0 : map.get(s));
		}
		List<Map.Entry<String, Integer>> info = new ArrayList<Map.Entry<String, Integer>>(wordsMap.entrySet());
		Collections.sort(info, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
				return obj2.getValue() - obj1.getValue();
			}
		});
		return info;
	}

	ArrayList<String> editErrorWord(String word) {
		ArrayList<String> edit = new ArrayList<String>();
		for (int i = 0; i < word.length(); ++i)edit.add(word.substring(0, i) + word.substring(i + 1));	//删除一个字母
		for (int i = 0; i < word.length() - 1; ++i)edit.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1)+ word.substring(i + 2));//交换相邻两个字母顺序
		for (int i = 0; i < word.length(); ++i)for (char c = 'a'; c <= 'z'; ++c)edit.add(word.substring(0, i) + String.valueOf(c) + word.substring(i + 1));//添加一个字母
		for (int i = 0; i <= word.length(); ++i)for (char c = 'a'; c <= 'z'; ++c)edit.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));//替换一个字母
		return edit;
	}
	
	void check(String englishPath) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(englishPath));
			String line = "";
			boolean bool = true;
			boolean f = true;
			while ((line = in.readLine()) != null) {
				line = line.replaceAll("\\pS|\\pM|\\pN|\\pC", " ");
				line = line.replaceAll("\\,+|\\.+|\\?+|\\:+|\\\"+|\\;+|\\-+", " ");
				line = line.replaceAll(" +", " ");
				line = line.toLowerCase();
				String[] dic = line.split(" ");
				for (int i = 0; i < dic.length; i++) {
					if(dic[i].equals("")) {
						continue;
					}
					f = true;
					if (dictionary.contains(dic[i])) {
						f = false;
					}
					if (f) {
						Scanner sc = new Scanner(System.in);
						String judge;
						bool = false;
						System.out.println("Error" + " :" + dic[i]);
						System.out.println("Would you sure this word(" + dic[i]+ ") is currect,if you sure please input 1 otherwise input other value.");
						judge = sc.nextLine();
						if (judge.equals("1")) {
							System.out.println("Would you want this word(" + dic[i]+ ") add to englishDictionary.txt,if you want please input 1 otherwise input other value.");
							judge = sc.nextLine();
							if (judge.equals("1")) {
								Add(dic[i]);
								System.out.println("Add success.");
								dictionary=read(dicPath);
							}
						} else {
							System.out.println("Would you want to change this word(" + dic[i]+ ") to currect,if you want please input 1 otherwise input other value.");
							judge = sc.nextLine();
							if (judge.equals("1")) {
								List<Entry<String, Integer>> list = suggestword(dic[i]);
								int n = 1;
								String[] str = new String[20];
								for (Entry<String, Integer> s : list) {
									System.out.print(n + ": " + s.getKey() + " ");
									str[n - 1] = s.getKey();
									n++;
									if (n > 10)
										break;
								}
								if(n>1) {
									System.out.println();
									System.out.println("If you want change this word(" + dic[i]+ ") to suggest,please input num otherwise input other any value.");
									judge = sc.nextLine();
									Pattern pattern = Pattern.compile("[0-9]*"); // 判断输入是否为数字
									Matcher isNum = pattern.matcher(judge);
									if (!isNum.matches()) {
										System.out.println("Please input correct word.");
										judge = sc.nextLine();
										dic[i] = judge;
										System.out.println("Modified success.");
									} else {
										int num = Integer.parseInt(judge);
										if (num > 0 && num < n) {
											dic[i] = str[num - 1];
											System.out.println("Modified success.");
										} else {
											System.out.println("Please input correct word.");
											judge = sc.nextLine();
											dic[i] = judge;
											System.out.println("Modified success.");
										}
									}
								}else {
									System.out.println("Don't have recommended words,Please input correct word.");
									judge = sc.nextLine();
									dic[i] = judge;
									System.out.println("Modified success.");
								}
								
							}
						}

					}
				}
				String s = "";
				for (int i = 0; i < dic.length; i++) {
					s += dic[i] + " ";
				}
				english.add(s);
			}
			in.close();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(englishPath)));
			for (int i = 0; i < english.size(); i++) {
				out.println(english.get(i));
			}
			out.close();
			if (bool) {
				System.out.print("This txt is true.");
			} else {
				System.out.print("This txt is end.");
			}
		} catch (IOException e) {
			System.out.println("Don't find "+englishPath+".");
		}
	}
}

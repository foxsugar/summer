package com.code.server.game.poker.hitgoldflower;
import java.util.ArrayList;
import java.util.Collections;

public class PokerItem {
	
	public String color;  //颜色
	public String value; //数值
	public Integer index; //代表序号
	private	static String[] numbers = {"A","K","Q","J","10","9","8","7","6","5","4","3","2"};
	private static String[] colors  = {"HEI","HONG","HUA","PIAN"};
	
	public PokerItem() {		
	}
	
	public PokerItem(String color, String value) {
		this.color = color;
		this.value = value;
		
		int index = 0;
		for(int i = 0; i < numbers.length; i++){
			String a = numbers[i];
			if(a.equals(value)){
				index = i;
				break;
			}
		}
		
		int hua = 0;
		for(int i = 0; i < colors.length; i++){
			String a = numbers[i];
			if(a.equals(color)){
				hua = i;
				break;
			}
		}
		index = 4 * index + 2;
		index += hua;
		this.index = index;
		
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	//根据需要创建一张牌
	public static PokerItem createItem(int index){
		int idx = index - 2;
		PokerItem item = new PokerItem();
		item.color = colors[idx % 4];
		item.value = numbers[idx / 4];
		item.index = index;
		return item;
	}
	//创建所有的扑克牌
	public static ArrayList<PokerItem> createPokers(){
		
		ArrayList<PokerItem> list = new ArrayList<>();
		for(int i = 2; i < 54; i++){
			PokerItem item = PokerItem.createItem(i);
			list.add(item);
		}
		return list;
	}
	//洗牌
	public static ArrayList<PokerItem> shufflePokers(ArrayList<PokerItem> list){
		Collections.shuffle(list);
		return list;
	}
	//打印所有的牌
	public static void showPokers(ArrayList<PokerItem> list){
		for(PokerItem item : list){
			System.out.println(item.color + " " + item.value);
		}
	}
	//对手中的牌进行排序
	public static void sortPokers(ArrayList<PokerItem> list){
		ListUtils.sort(list, true, "index");
	}
	//判断是否是豹子
	public static boolean BaoZi(ArrayList<PokerItem> list){
		
		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}
		//首先对传过来的牌进行排序
		sortPokers(list);
		//如果四张牌不在一个组里 
		PokerItem item = list.get(0);
		int chushu = (item.index - 2) / 4;
		for(PokerItem aItem : list){
			if((aItem.index - 2)/ 4 != chushu){
				return false;
			}
		}
		return true;
	}
	//是否是顺子
	public static boolean ShunZi(ArrayList<PokerItem> list){

		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}

		//首先对传过来的牌进行排序
		sortPokers(list);

		PokerItem item = list.get(0);
		int chushu = (item.index - 2) / 4;

		if (chushu == 0){

			PokerItem item1 = list.get(1);
			PokerItem item2 = list.get(2);

			int chushu1 = (item1.index - 2) / 4;
			int chushu2 = (item2.index - 2) / 4;

			if (chushu == 0 && chushu1 ==  11 && chushu2 == 12){
				return true;
			}

		}

		for(PokerItem aItem : list){
			if(item == aItem){
				continue;
			}
			int mChushu = (aItem.index - 2)/ 4;
			if(mChushu - chushu != 1){
				return false;
			}
			chushu = mChushu;
		}
		return true;
	}
	//是不是金花
	public static boolean JinHua(ArrayList<PokerItem> list){
		
		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}
		sortPokers(list);
		PokerItem item = list.get(0);
		
		for(PokerItem aItem : list){
			if(!aItem.color.equals(item.color)){
				return false;
			}
		}
		return true;
	}
	
	//是不是顺金
	public static boolean ShunJin(ArrayList<PokerItem> list){
		
		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}
		boolean ret1 = JinHua(list);
		boolean ret2 = ShunZi(list);
		return ret1 && ret2;
	}
	
	//是不是对子
	public static boolean DuiZi(ArrayList<PokerItem> list){
		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}
		sortPokers(list);
		for(int i = 0; i < list.size() - 1; i++){
			
			for(int j = i + 1; j < list.size(); j++){
				
				PokerItem item0 = list.get(i);
				PokerItem item1 = list.get(j);
				if(item0.value.equals(item1.value)){
					return true;
				}
			}
		}
		return false;
	}
	
	//是不是235
	public static boolean is235(ArrayList<PokerItem> list){
		
		int size = list.size();
		if(size != 3){
			throw new RuntimeException("必须传入三张牌！！！");
		}

		sortPokers(list);
		PokerItem item1 = list.get(0);
		PokerItem item2 = list.get(1);
		PokerItem item3 = list.get(2);

		boolean isJinHua = JinHua(list);
		if (isJinHua){
			return false;
		}

		if(((item1.index - 2) / 4 == 9) && ((item2.index - 2) / 4 == 11) && ((item3.index - 2) / 4 == 12)){
			return true;
		}
		
		return false;
	}
	
	//比较两个豹子
	public static int baoZiCompare(Player p1, Player p2){
		
		PokerItem aItem = p1.getPokers().get(0);
		PokerItem bItem = p2.getPokers().get(0);
		
		if(aItem.index < bItem.index){
			return 0;
		}else{
			return 2;
		}
	}

	public static int ShunJin(Player p1, Player p2){
		return shunZi(p1, p2);
	}

	public static int ShunJinCompare(Player p1, Player p2){
		return shunZiCompare(p1, p2);
	}

	public static int shunZiCompare(Player p1, Player p2){

		ArrayList<PokerItem> list1 = p1.getPokers();
		ArrayList<PokerItem> list2 = p2.getPokers();
		PokerItem item1 = list1.get(0);
		PokerItem item2 = list2.get(0);
		int chushu1 = (item1.index - 2) / 4;
		int chushu2 = (item2.index - 2) / 4;

		if(chushu1 == chushu2){

			PokerItem itemA = list1.get(1);
			PokerItem itemB = list2.get(1);

			//如果第一张是A
			if (chushu1 == 0){

				if ((itemA.index - 2) / 4 == (itemB.index - 2) / 4){
					return 1;
				}else if((itemA.index - 2) / 4 > (itemB.index - 2) / 4){
					return 2;
				}else {
					return 0;
				}
			}

			return 1;


		}else if(chushu1 > chushu2){
			PokerItem itemA = list2.get(1);
			PokerItem itemB = list2.get(2);

			if ((chushu2 == 0) && ((itemA.index-2) / 4 == 11) && ((itemB.index-2) / 4 == 12)){
				return 0;
			}

			return 2;
		}else{

			PokerItem itemA = list1.get(1);
			PokerItem itemB = list1.get(2);
			if (chushu1 == 0 && ((itemA.index-2) / 4 == 11) && ((itemB.index-2) / 4 == 12)){
				return 2;
			}
			return 0;
		}
	}

	public static int shunZi(Player p1, Player p2){

		ArrayList<PokerItem> list1 = p1.getPokers();
		ArrayList<PokerItem> list2 = p2.getPokers();
		PokerItem item1 = list1.get(0);
		PokerItem item2 = list2.get(0);
		int chushu1 = (item1.index - 2) / 4;
		int chushu2 = (item2.index - 2) / 4;

		if(chushu1 == chushu2){

			PokerItem itemA = list1.get(1);
			PokerItem itemB = list2.get(1);

			//如果第一张是A
			if (chushu1 == 0){


				if ((itemA.index - 2) / 4 == (itemB.index - 2) / 4){
					//第二张相等
					return 1;
				}else if((itemA.index - 2) / 4 > (itemB.index - 2) / 4){
					//第二张
					if ((itemB.index - 2) / 4 == 1){
						return 2;
					}
					return 0;

				}else {

					if ((itemA.index - 2) / 4 == 1){
						return 0;
					}

					return 2;
				}
			}

			return 1;


		}else if(chushu1 > chushu2){
			return 2;
		}else{
			return 0;
		}
	}
	
	public static int JinHua(Player p1, Player p2){
		
//		//判断235
//		boolean ret1 = is235(p1.getPokers());
//		boolean ret2 = is235(p2.getPokers());
//
//		if(ret1 == true && ret2 == true){
//			return 1;
//		}else if(ret1 == true){
//			return 2;
//		}else if(ret2 == true){
//			return 0;
//		}
		
		ArrayList<PokerItem> list1 = p1.getPokers();
		ArrayList<PokerItem> list2 = p2.getPokers();
		
		for(int i = 0; i < list1.size(); i++){
			PokerItem item1 = list1.get(i);
			PokerItem item2 = list2.get(i);
			int chushu1 = (item1.index - 2) / 4;
			int chushu2 = (item2.index - 2) / 4;
			if(chushu1 < chushu2){
				return 0;
			}else if(chushu1 > chushu2){
				return 2;
			}
		}
		return 1;
	}
	//对子
	public static int DuiZi(Player p1, Player p2){
		
		ArrayList<PokerItem> list1 = p1.getPokers();
		ArrayList<PokerItem> list2 = p2.getPokers();
		System.out.println("list1 : "+list1);
		System.out.println("list2 : "+list2);

		//因为是对子 所以中间的牌必定是对子中的一个
		//先比较对
		PokerItem item1 = list1.get(1);
		PokerItem item2 = list2.get(1);
		System.out.println("item1 : " + item1);
		System.out.println("item2 : " + item2);
		int dui1 = (item1.index - 2) / 4;
		int dui2 = (item2.index - 2) / 4;
		if(dui1 < dui2){
			System.out.println("list1 小");
			return 0;
		}else if(dui1 > dui2){
			System.out.println("list1 大");
			return 2;
		}else{
			//对子相等 比较单
			PokerItem signle1 = list1.get(0).value.equals(item1.value) ? list1.get(2) : list1.get(0);
			PokerItem signle2 = list2.get(0).value.equals(item2.value) ? list2.get(2) : list2.get(0);
			
			int chushu1 = (signle1.index - 2) / 4;
			int chushu2 = (signle2.index - 2) / 4;
			System.out.println("chushu1 : " + chushu1);
			System.out.println("chushu2 : " + chushu2);

			if(chushu1 < chushu2){
				return 0;
			}else if(chushu1 == chushu2){
				return 1;
			}else{
				return 2;
			}
		}
	}
	//单子 和金花算法一样
	public static int DanZi(Player p1, Player p2){
		return  JinHua(p1, p2);
	}

	@Override
	public String toString() {
		return "PokerItem{" +
				"color='" + color + '\'' +
				", value='" + value + '\'' +
				", index=" + index +
				'}';
	}
}

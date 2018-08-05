package com.code.server.game.poker.zhaguzi;

import java.util.ArrayList;

public class Player {
	
    public enum CardCategory{
    	BaoZi, ShunJin, JinHua, ShunZi, DuiZi, DanZi
    }

	public enum Rules{
    	//欢乐：AKQ > A23 > 其他
		XiaoYao, HuanLe
	}
    
    public Player() {
	}

	public  String transfromCategoryToString(){

    	String str = "";

    	if (this.getCategory().equals(CardCategory.BaoZi)){
    		str = "豹子";
		}else if (this.getCategory().equals(CardCategory.BaoZi.ShunJin)){
    		str = "顺金";
		}else if(this.getCategory().equals(CardCategory.JinHua)){
			str = "金花";
		}else if (this.getCategory().equals(CardCategory.DuiZi)){
			str = "对子";
		}else if (this.getCategory().equals(CardCategory.DanZi)){
			str = "单子";
		}
		return str;
	}
    
    public Player(Long uid, String c1, String v1, String c2, String v2, String c3, String v3) {

		this.uid = uid;
		PokerItem item1 = new PokerItem(c1, v1);
		PokerItem item2 = new PokerItem(c2, v2);
		PokerItem item3 = new PokerItem(c3, v3);
		ArrayList<PokerItem> list = new ArrayList<>();
		list.add(item1);
		list.add(item2);
		list.add(item3);
		this.setPokers(list);
	}
    
    
    public Player(Long uid, int v1, int v2, int v3) {
    	
		this.uid = uid;
		PokerItem item1 = PokerItem.createItem(v1);
		PokerItem item2 = PokerItem.createItem(v2);
		PokerItem item3 = PokerItem.createItem(v3);
		ArrayList<PokerItem> list = new ArrayList<>();
		list.add(item1);
		list.add(item2);
		list.add(item3);
		this.setPokers(list);
	}

	static Rules rules_;
	private CardCategory category;
	public CardCategory getCategory() {
		return category;
	}
	public void setCategory(CardCategory category) {
		this.category = category;
	}
	private Long uid;
	private ArrayList<PokerItem> pokers;
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public ArrayList<PokerItem> getPokers() {
		return pokers;
	}
	public void setPokers(ArrayList<PokerItem> pokers) {
		this.pokers = pokers;
		
		//设置的时候顺便要判断类型
		boolean ret = PokerItem.BaoZi(pokers);
		if(ret == true){
			this.setCategory(CardCategory.BaoZi);
		}else{
			ret = PokerItem.ShunJin(pokers);
			if(ret == true){
				this.setCategory(CardCategory.ShunJin);
			}else{
				ret = PokerItem.JinHua(pokers);
				if(ret == true){
					this.setCategory(CardCategory.JinHua);
				}else{
					ret = PokerItem.ShunZi(pokers);
					if(ret == true){
						this.setCategory(CardCategory.ShunZi);
					}else{
						ret = PokerItem.DuiZi(pokers);
						if(ret == true){
							this.setCategory(CardCategory.DuiZi);
						}else{
							this.setCategory(CardCategory.DanZi);
						}
					}
				}
			}
		}
	}
	
	public static int catoryValue(CardCategory c){
		
		int value = 0;
		switch (c) {
		case BaoZi:
			value = 1;
			break;
		case ShunJin:
			value = 2;
			break;
		case JinHua:
			value = 3;
			break;
		case ShunZi:
			value = 4;
			break;
		case DuiZi:
			value = 5;
			break;
		case DanZi:
			value = 6;
			break;
		}
		return value;
	}



	public static ArrayList<Player> findWinners(Player...player){
		
		if(player.length > 1){
			//如果有235 和豹子 还有其他 就不处理

			//判断豹子
			Player baozi = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret = PokerItem.BaoZi(p.getPokers());
				if(ret){
					baozi = p;
					break;
				}
			}

			Player p235 = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret = PokerItem.is235(p.getPokers());
				if(ret){
					p235 = p;
					break;
				}
			}

			Player pother = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret1 = PokerItem.is235(p.getPokers());
				boolean ret2 = PokerItem.BaoZi(p.getPokers());
				if(ret1 == false && ret2 == false){
					pother = p;
					break;
				}
			}
			if(p235 != null && pother!=null && baozi!=null){
				if(p235 != pother){

					ArrayList list = new ArrayList();
					for(Player plr : player){
						list.add(plr);
					}
					return list;
				}
			}
		}

		for(int i = 0; i <  player.length - 1; i++){
			
			Player p1 = player[i];
			for(int j = i + 1; j < player.length; j++){
				//对数组进行排序
				if (player[i].comparePlayer(player[j]) == 2){

					Player temp = player[i];
					player[i] = player[j];
					player[j] = temp;
				}
			}
		}
		ArrayList<Player> list = new ArrayList<Player>();
		
		Player player1 = player[0];
		list.add(player1);
		for(int i = 1; i < player.length; i++){
			Player onePlayer = player[i];
			if(player1.comparePlayer(onePlayer) == 0){
				break;
			}
			list.add(onePlayer);
		}
		return list;
	}

	public static ArrayList<Player> findWinners(Rules rules, Player...player){

		rules_ = rules;
		if(player.length > 1){
			//如果有235 和豹子 还有其他 就不处理

			//判断豹子
			Player baozi = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret = PokerItem.BaoZi(p.getPokers());
				if(ret){
					baozi = p;
					break;
				}
			}

			Player p235 = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret = PokerItem.is235(p.getPokers());
				if(ret){
					p235 = p;
					break;
				}
			}

			Player pother = null;
			for(int i = 0; i < player.length; i++){
				Player p = player[i];
				boolean ret1 = PokerItem.is235(p.getPokers());
				boolean ret2 = PokerItem.BaoZi(p.getPokers());
				if(ret1 == false && ret2 == false){
					pother = p;
					break;
				}
			}
			if(p235 != null && pother!=null && baozi!=null){
				if(p235 != pother){

					ArrayList list = new ArrayList();
					for(Player plr : player){
						list.add(plr);
					}
					return list;
				}
			}
		}else {
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(player[0]);
			return list;
		}

		for(int i = 0; i <  player.length - 1; i++){

//			Player p1 = player[i];
			for(int j = i + 1; j < player.length; j++){
				//对数组进行排序
//				Player p2 = player[j];

//				if(p1.comparePlayer(p2) == 2){
//					Player temp = player[i];
//					player[i] = player[j];
//					p1 = player[i];
//					player[j] = temp;
//				}

				if (player[i].comparePlayer(player[j]) == 2){

					Player temp = player[i];
					player[i] = player[j];
					player[j] = temp;
				}

			}
		}
		ArrayList<Player> list = new ArrayList<Player>();

		Player player1 = player[0];
		list.add(player1);
		for(int i = 1; i < player.length; i++){
			Player onePlayer = player[i];
			if(player1.comparePlayer(onePlayer) == 0){
				break;
			}
			list.add(onePlayer);
		}
		return list;
	}

	public static int comparePlayer(Player p1, Player p2){
		// 先对一个235 和一个豹子进行判断
		boolean ret1 = PokerItem.is235(p1.getPokers());
		boolean ret2 = PokerItem.is235(p2.getPokers());
		//如果p1是235
		if((ret1 == true) && (ret2 != true)){

			//如果p2是豹子
			if(PokerItem.BaoZi(p2.getPokers()) == true){
				return 0;
			}

		}else if(((ret1 != true) && (ret2 == true))){

			//如果p1是豹子
			if(PokerItem.BaoZi(p1.getPokers()) == true){
				return 2;
			}
		}

		int value1 = Player.catoryValue(p1.category);
		int value2 = Player.catoryValue(p2.category);

		if(value1 > value2){
			return 2;
		}else if(value1 < value2){
			return 0;
		}else{

			if(value1 == 1){
				return PokerItem.baoZiCompare(p1, p2);
			}else if(value1 == 2){
				if (rules_ == Rules.HuanLe){
					return PokerItem.ShunJinCompare(p1, p2);
				}else {
					return PokerItem.ShunJin(p1, p2);
				}

			}else if(value1 == 3){
				return PokerItem.JinHua(p1, p2);
			}else if(value1 == 4){

				if (rules_ == Rules.HuanLe){
					return PokerItem.shunZiCompare(p1, p2);
				}else{
					return PokerItem.shunZi(p1, p2);
				}

			}else if(value1 == 5){
				return PokerItem.DuiZi(p1, p2);
			}else{
				return PokerItem.DanZi(p1, p2);
			}
		}
	}
	
	public int comparePlayer(Player player){
		
		return Player.comparePlayer(this, player);
	}

	@Override
	public String toString() {
		return "Player [category=" + category + ", uid=" + uid + ", pokers=" + pokers + "]";
	}
}

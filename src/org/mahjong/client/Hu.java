package org.mahjong.client;

import java.util.List;

public class Hu {
	private List<Integer> huList;
    
	public static Hu fromHuEntryInGameState(List<Integer> huEntryList){
		return new Hu(huEntryList);
	}
	
	public Hu(List<Integer> huEntryList){
		this.huList = huEntryList;
	}
	
	public List<Integer> getHuList(){
		return huList;
	}
}

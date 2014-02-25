package org.mahjong.client;

import java.util.List;

public class Peng implements Melds{
    private Integer pengFirst;
	private Integer pengSecond;
    
	public static Peng fromPengEntryInGameState(List<Integer> pengEntryList){
		return new Peng(pengEntryList);
	}
	
	public Peng(List<Integer> pengEntryList){
		if(pengEntryList != null){
		this.pengFirst = pengEntryList.get(0);
		this.pengSecond = pengEntryList.get(1); 
		}
	}
	
	public Integer getFirst(){
		return pengFirst;
	}
	
	public Integer getSecond(){
		return pengSecond;
	}
	
	public Integer getThird(){
		throw new RuntimeException("There is no such field in Peng!");
	}
}

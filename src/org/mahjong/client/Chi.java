package org.mahjong.client;

import java.util.List;

public class Chi implements Melds{
	private Integer chiFirst;
	private Integer chiSecond;
	
	public static Chi fromChiEntryInGameState(List<Integer> chiEntryList){
		return new Chi(chiEntryList);
	}
	
	public Chi(List<Integer> chiEntryList){
		if(chiEntryList != null){
		this.chiFirst = chiEntryList.get(0);
		this.chiSecond = chiEntryList.get(1);
		}
	}
	
	public Integer getFirst(){
		return chiFirst;
	}
	
	public Integer getSecond(){
		return chiSecond;
	}
	
	public Integer getThird() {
		throw new RuntimeException("There is no such field in Chi");
	}
}

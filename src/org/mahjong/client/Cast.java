package org.mahjong.client;

public class Cast {
	private Integer castTileNum;
	public static Cast fromCastEntryInGameState(Integer castEntry){
		return new Cast(castEntry);
	}
	
	public Cast(Integer castTileNum){
		this.castTileNum = castTileNum;
	}
	
	public Integer getValue(){
		return castTileNum;
	}
}

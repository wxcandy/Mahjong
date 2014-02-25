package org.mahjong.client;

public enum Position {
	E, N, W, S;
	
	public boolean isEast(){
		return this == E;
	}
	
	public boolean isNorth(){
		return this == N;
	}
	
	public boolean isWest(){
		return this == W;
	}
	
	public boolean isSouth(){
		return this == S;
	}
	
	public String nameOfChi(){
		if(this==E) return "chiByE"; 
		if(this==N) return "chiByN";
		if(this==W) return "chiByW";
		if(this==S) return "chiByS";
		return null;
	}
	
	public String nameOfPeng(){
		if(this==E) return "pengByE";
		if(this==N) return "pengByN";
		if(this==W) return "pengByW";
		if(this==S) return "pengByS";
		return null;
	}
	
	public String nameOfGang(){
		if(this==E) return "gangByE";
		if(this==N) return "gangByN";
		if(this==W) return "gangByW";
		if(this==S) return "gangByS";
		return null;
	}
	
	public Position getNextTurnOfPosition() {
		if(this==E) return N;
		if(this==N) return W;
		if(this==W) return S;
		if(this==S) return E;
		return null;
	}
}

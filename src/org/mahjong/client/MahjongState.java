package org.mahjong.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class MahjongState {
	private final Position turn;
	
	private final ImmutableList<Integer> playerIds;
	
	private final ImmutableList<Optional<Tile>> tiles;
	
	private final ImmutableList<Integer> E;
	private final ImmutableList<Integer> chiByE;
	private final ImmutableList<Integer> pengByE;
	private final ImmutableList<Integer> gangByE;
	private final ImmutableList<Integer> S;
	private final ImmutableList<Integer> chiByS;
	private final ImmutableList<Integer> pengByS;
	private final ImmutableList<Integer> gangByS;
	private final ImmutableList<Integer> W;
	private final ImmutableList<Integer> chiByW;
	private final ImmutableList<Integer> pengByW;
	private final ImmutableList<Integer> gangByW;
	private final ImmutableList<Integer> N;
	private final ImmutableList<Integer> chiByN;
	private final ImmutableList<Integer> pengByN;
	private final ImmutableList<Integer> gangByN;
	
	private final int wallIndex;
	
	private final ImmutableList<Integer> wallEast;
	private final ImmutableList<Integer> wallSouth;
	private final ImmutableList<Integer> wallNorth;
	private final ImmutableList<Integer> wallWest;
	
	private final int specialTile;
	
	private final Optional<Cast> cast;
	private final Position nextTurnOfCastPlayer;
	private final Position playerWhoIssueCast;
	
	private final boolean isChi;
	private final Optional<Chi> chi;
	
	private final boolean isPeng;
	private final Optional<Peng> peng;
	
	//private final boolean isGang;
	//private final Optional<Gang> gang;
	//private boolean isGangStatus;
	//private boolean isGangCheckStatus;
	
	private final boolean isHu;
	
	private final ImmutableList<Integer> middleTiles;
	
	private final boolean isHuStatus;
	private final boolean isPengStatus;
	private final boolean isChiStatus;
	
	private final boolean isHuCheckStatus;
	private final boolean isPengCheckStatus;
	private final boolean isChiCheckStatus;
	
	private final int isHuIsAllowed;
	private final int isPengIsAllowed;
	private final int isChiIsAllowed;
	
	private final int choiceForHu;
	private final int choiceForPeng;
	private final int choiceForChi;
	
	public MahjongState(
		Position turn,
		ImmutableList<Integer> playerIds,
		ImmutableList<Optional<Tile>> tiles,
		ImmutableList<Integer> E,
		ImmutableList<Integer> chiByE,
		ImmutableList<Integer> pengByE,
		ImmutableList<Integer> gangByE,
		ImmutableList<Integer> S,
		ImmutableList<Integer> chiByS,
		ImmutableList<Integer> pengByS,
		ImmutableList<Integer> gangByS,
		ImmutableList<Integer> W,
		ImmutableList<Integer> chiByW,
		ImmutableList<Integer> pengByW,
		ImmutableList<Integer> gangByW,
		ImmutableList<Integer> N,
		ImmutableList<Integer> chiByN,
		ImmutableList<Integer> pengByN,
		ImmutableList<Integer> gangByN,
        int wallIndex,
        ImmutableList<Integer> wallEast,
		ImmutableList<Integer> wallSouth,
		ImmutableList<Integer> wallNorth,
		ImmutableList<Integer> wallWest,
		int specialTile,
		Optional<Cast> cast,
		Position nextTurnOfCastPlayer,
		Position playerWhoIssueCast,
		boolean isChi,
		Optional<Chi> chi,
		boolean isPeng,
		Optional<Peng> peng,
		boolean isHu,
		ImmutableList<Integer> middleTiles,
		boolean isHuStatus,
		boolean isPengStatus,
		boolean isChiStatus,
		boolean isHuCheckStatus,
		boolean isPengCheckStatus,
		boolean isChiCheckStatus,
		int isHuIsAllowed,
		int isPengIsAllowed,
		int isChiIsAllowed,
		int choiceForHu,
		int choiceForPeng,
		int choiceForChi
		){
		this.turn = checkNotNull(turn);
		this.playerIds = playerIds;
	    this.tiles = checkNotNull(tiles);
		this.E = checkNotNull(E);
		this.chiByE = chiByE;
	    this.pengByE = pengByE;
	    this.gangByE = gangByE;
	    this.S = checkNotNull(S);
	    this.chiByS = chiByS;
	    this.pengByS = pengByS;
	    this.gangByS = gangByS;
	    this.W = checkNotNull(W);
	    this.chiByW = chiByW;
	    this.pengByW = pengByW;
	    this.gangByW = gangByW;
	    this.N = checkNotNull(N);
	    this.chiByN = chiByN;
	    this.pengByN = pengByN;
	    this.gangByN = gangByN;
	    this.wallIndex = wallIndex;
	    this.wallEast = wallEast;
	    this.wallNorth = wallNorth;
	    this.wallSouth = wallSouth;
	    this.wallWest = wallWest;
	    this.specialTile = specialTile;
		this.cast = cast;
		this.nextTurnOfCastPlayer = nextTurnOfCastPlayer;
		this.playerWhoIssueCast = playerWhoIssueCast;
		this.isChi = isChi;
		this.chi = chi;
		this.isPeng = isPeng;
		this.peng = peng;
		this.isHu = isHu;
		this.middleTiles = middleTiles;
		this.isHuStatus = isHuStatus;
		this.isPengStatus = isPengStatus;
		this.isChiStatus = isChiStatus;
		this.isHuCheckStatus = isHuCheckStatus;
		this.isPengCheckStatus = isPengCheckStatus;
		this.isChiCheckStatus = isChiCheckStatus;
		this.isHuIsAllowed = isHuIsAllowed;
		this.isPengIsAllowed = isPengIsAllowed;
		this.isChiIsAllowed = isChiIsAllowed;
		this.choiceForHu = choiceForHu;
		this.choiceForPeng = choiceForPeng;
		this.choiceForChi = choiceForChi;
	}
	
	public int isHuIsAllowed() {
		return isHuIsAllowed;
	}
	
	public int isPengIsAllowed() {
		return isPengIsAllowed;
	}
	
	public int isChiIsAllowed() {
		return isChiIsAllowed;
	}
	
	public Position getTurn() {
		return turn;
	}
	
	public Position getNextTurnOfCastPlayer(){
		return nextTurnOfCastPlayer;
	}
	
	public Position getPlayerWhoIssueCast(){
		return playerWhoIssueCast;
	}
	
	public ImmutableList<Integer> getPlayerIds(){
		return playerIds;
	}
	
	public int getPlayerId(Position position){
		return playerIds.get(position.ordinal());
	}
	
	public ImmutableList<Optional<Tile>> getTiles() {
		return tiles;
	}
	
	public ImmutableList<Integer> getE() {
		return E;
	}
	
	public ImmutableList<Integer> getS() {
		return S;
	}
	
	public ImmutableList<Integer> getW() {
		return W;
	}
	
	public ImmutableList<Integer> getN() {
		return N;
	}
	
	public ImmutableList<Integer> getChiByE() {
		return chiByE;
	}
	
	public ImmutableList<Integer> getChiByS() {
		return chiByS;
	}
	
	public ImmutableList<Integer> getChiByN() {
		return chiByN;
	}
	
	public ImmutableList<Integer> getChiByW() {
		return chiByW;
	}
	
	public ImmutableList<Integer> getPengByE() {
		return pengByE;
	}
	
	public ImmutableList<Integer> getPengByS() {
		return pengByS;
	}
	
	public ImmutableList<Integer> getPengByN() {
		return pengByN;
	}
	
	public ImmutableList<Integer> getPengByW() {
		return pengByW;
	}
	
	public ImmutableList<Integer> getGangByE() {
		return gangByE;
	}
	
	public ImmutableList<Integer> getGangByS() {
		return gangByS;
	}
	
	public ImmutableList<Integer> getGangByW() {
		return gangByW;
	}
	
	public ImmutableList<Integer> getGangByN() {
		return gangByN;
	}
	
	public int getSpecialTile() {
		return specialTile;
	}
	
	public Optional<Chi> getChi() {
		return chi;
	}
	
	public boolean isChi() {
		return isChi;
	}
	
	public Optional<Peng> getPeng() {
		return peng;
	}
	
	public boolean isPeng() {
		return isPeng;
	}
	
	public boolean isHu(){
		return isHu;
	}
	
	public Optional<Cast> getCast() {
		return cast;
	}
	
	public ImmutableList<Integer> getWallEast(){
		return wallEast;
	}
	
	public ImmutableList<Integer> getWallSouth(){
		return wallSouth;
	}
	
	public ImmutableList<Integer> getWallNorth(){
		return wallNorth;
	}

	public ImmutableList<Integer> getWallWest(){
		return wallWest;
	}
	
	public int getWallIndex(){
		return wallIndex;
	}
	
	public ImmutableList<Integer> getOneOfFourWall(int index){
		switch(index){
		case 0: return wallEast;
		case 1: return wallNorth;
		case 2: return wallWest;
		case 3: return wallSouth;
		}
		return null;
	}
	
	public ImmutableList<Integer> getOneOfFourTile(Position position){
		if(position.isEast()) return E;
		else if(position.isNorth()) return N;
		else if(position.isWest()) return W;
		else return S;
	}
	
	public ImmutableList<Integer> getOneOfFourChi(Position position){
		if(position.isEast()) return chiByE;
		else if(position.isNorth()) return chiByN;
		else if(position.isWest()) return chiByW;
		else return chiByS;
	}
	
	public ImmutableList<Integer> getOneOfFourPeng(Position position){
		if(position.isEast()) return pengByE;
		else if(position.isNorth()) return pengByN;
		else if(position.isWest()) return pengByW;
		else return pengByS;
	}
	
	public ImmutableList<Integer> getOneOfFourGang(Position position){
		if(position.isEast()) return gangByE;
		else if(position.isNorth()) return gangByN;
		else if(position.isWest()) return gangByW;
		else return gangByS;
	}
	
	public ImmutableList<Integer> getMiddleTiles() {
		return middleTiles;
	}
	
	public boolean isHuStatus() {
		return isHuStatus;
	}
	
	public boolean isPengStatus() {
		return isPengStatus;
	}
	
	public boolean isChiStatus() {
		return isChiStatus;
	}
	
	public boolean isHuCheckStatus() {
		return isHuCheckStatus;
	}
	
	public boolean isPengCheckStatus() {
	    return isPengCheckStatus;
	}
	
	public boolean isChiCheckStatus() {
	    return isChiCheckStatus;
	}
}

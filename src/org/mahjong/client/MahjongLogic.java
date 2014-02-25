package org.mahjong.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.mahjong.client.GameApi.Delete;
import org.mahjong.client.GameApi.EndGame;
import org.mahjong.client.GameApi.Operation;
import org.mahjong.client.GameApi.Set;
import org.mahjong.client.GameApi.SetTurn;
import org.mahjong.client.GameApi.SetVisibility;
import org.mahjong.client.GameApi.Shuffle;
import org.mahjong.client.GameApi.VerifyMove;
import org.mahjong.client.GameApi.VerifyMoveDone;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MahjongLogic {
	private static final String E = "E";
	private static final String CHI_BY_E = "chiByE";
	private static final String PENG_BY_E = "pengByE"; 
	private static final String GANG_BY_E = "gangByE";
	private static final String S = "S";
	private static final String CHI_BY_S = "chiByS";
	private static final String PENG_BY_S = "pengByS"; 
	private static final String GANG_BY_S = "gangByS";
	private static final String W = "W";
	private static final String CHI_BY_W = "chiByW";
	private static final String PENG_BY_W = "pengByW"; 
	private static final String GANG_BY_W = "gangByW";
	private static final String N = "N";
	private static final String CHI_BY_N = "chiByN";
	private static final String PENG_BY_N = "pengByN"; 
	private static final String GANG_BY_N = "gangByN";
	
	private static final String MIDDLE_TILES = "middleTiles";
	
	private static final String CHI = "chi";
	private static final String PENG = "peng";
	//private static final String GANG = "gang";
	private static final String CAST = "cast";
	
	private static final String YES = "yes";
	
	private static final String WALL_EAST = "wallEast";
	private static final String WALL_SOUTH = "wallSouth";
	private static final String WALL_WEST = "wallWest";
	private static final String WALL_NORTH = "wallNorth";
	
	private static final String WALL_INDEX = "wallIndex";
	
	private static final String T = "T";
	
	private static final String SPECIAL_TILE = "specialTile";
	private static final String INI_CONTINUE = "iniContinue";
	
	private static final String NEXT_TURN_OF_CAST_PLAYER = "nextTurnOfCastPlayer";
	private static final String PLAYER_WHO_ISSUE_CAST = "playerWhoIssueCast";
	
	
	private static final String IS_CHI = "isChi";
	private static final String IS_PENG = "isPeng";
	private static final String IS_HU = "isHu";
	
	//表示用户点击了那个显示出来的按钮之后进入到了其对应操作的状态
    private static final String IS_HU_STATUS = "isHuStatus";
	private static final String IS_PENG_STATUS = "isPengStatus";
	private static final String IS_CHI_STATUS = "isChiStatus";
	//表示在进行自动胡/碰/吃检测的那一圈，一旦用户点击了显示出来的按钮，那么这个属性就消失了
	private static final String IS_HU_CHECK_STATUS = "isHuCheckStatus";
	private static final String IS_PENG_CHECK_STATUS = "isPengCheckStatus";
	private static final String IS_CHI_CHECK_STATUS = "isChiCheckStatus";
	
	private static final String IS_FETCH = "isFetch";
	
	private static final String HU_IS_ALLOWED = "huIsAllowed";
	private static final String PENG_IS_ALLOWED = "pengIsAllowed";
	private static final String CHI_IS_ALLOWED = "chiIsAllowed";
	
	private static final String CHOICE_FOR_HU = "choiceForHu";
	private static final String CHOICE_FOR_PENG = "choiceForPeng";
	private static final String CHOICE_FOR_CHI = "choiceForChi";
	
	private static final String IS_HU_IS_ALLOWED = "isHuIsAllowed";
	private static final String IS_PENG_IS_ALLOWED = "isPengIsAllowed"; 
	private static final String IS_CHI_IS_ALLOWED = "isChiIsAllowed";
	
	public VerifyMoveDone verify(VerifyMove verifyMove){
		try{
			checkMoveIsLegal(verifyMove);
			return new VerifyMoveDone();
		} catch (Exception e) {
			return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
		}
	}
	
	public void checkMoveIsLegal(VerifyMove verifyMove){
		Map<String, Object> lastState = verifyMove.getLastState();
		List<Operation> lastMove = verifyMove.getLastMove();
		List<Operation> expectedOperations = getExpectedOperations(lastState, lastMove, verifyMove.getPlayerIds(), verifyMove.getLastMovePlayerId());
		
		check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
	    
		if(verifyMove.getLastState().isEmpty()){
			check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
		}
	}

	List<Operation> getExpectedOperations(Map<String, Object> lastApiState, List<Operation> lastMove, List<Integer> playerIds, int lastMovePlayerId){
		if(lastApiState.isEmpty()){
			return getInitialMoveOne(playerIds);
		}else if(lastMove.contains(new Delete(INI_CONTINUE))){
			return getInitialMoveTwo(playerIds);
		}
		
		MahjongState lastState = gameApiStateToMahjongState(lastApiState, Position.values()[playerIds.indexOf(lastMovePlayerId)], playerIds);
		
		if(lastMove.contains(new Set(IS_FETCH, YES))){
			return declareFetchMove(lastState);
		}else if(lastMove.contains(new Delete(IS_HU_CHECK_STATUS)) && lastMove.contains(new Set(IS_PENG_CHECK_STATUS, YES))
	    || lastMove.contains(new Set(HU_IS_ALLOWED, true)) || lastMove.contains(new Set(HU_IS_ALLOWED, false))) {
			return declareAutoHuCheckMove(lastState, lastMove);
		}else if(lastMove.contains(new Set(CHOICE_FOR_HU, false))
	    || lastMove.contains(new Set(CHOICE_FOR_HU, true))) {
	        return waitForHuChoice(lastState, lastMove);
		}else if(lastMove.contains(new Set(IS_HU, YES))) {
	        return declareHuMove(lastState);
		}else if(lastMove.contains(new Delete(IS_HU))) {
	        return checkIfHuMove(lastState);
		}else if(lastMove.contains(new Delete(IS_PENG_CHECK_STATUS)) && lastMove.contains(new Set(IS_CHI_CHECK_STATUS, YES))
	        || lastMove.contains(new Set(PENG_IS_ALLOWED, true)) || lastMove.contains(new Set(PENG_IS_ALLOWED, false))) {
			return declareAutoPengCheckMove(lastState, lastMove);
		}else if(lastMove.contains(new Set(CHOICE_FOR_PENG, false))
	        || lastMove.contains(new Set(CHOICE_FOR_PENG, true))) {
	        return waitForPengChoice(lastState, lastMove);
		}else if(lastMove.contains(new Set(IS_PENG, YES))) {
			return declarePengMove(lastState, lastMove);
	    }else if(lastMove.contains(new Delete(IS_PENG))) {
	    	return checkIfPengMove(lastState);
	    }else if(lastMove.contains(new Set(CHI_IS_ALLOWED, true))
	       || lastMove.contains(new Set(CHI_IS_ALLOWED, false))) {
	    	return declareAutoChiCheckMove(lastState, lastMove);
	    }else if(lastMove.contains(new Set(CHOICE_FOR_CHI, false))
	       || lastMove.contains(new Set(CHOICE_FOR_CHI, true))) {
	    	return waitForChiChoice(lastState, lastMove);
	    }else if(lastMove.contains(new Set(IS_CHI, YES))) {
	    	return declareChiMove(lastState, lastMove);
	    }else if(lastMove.contains(new Delete(IS_CHI))) {
	    	return checkIfChiMove(lastState);
	    }else{
	    	return doCastMove(lastState, lastMove);
	    }
	}
	
	/**
	 * To initialize the game board
	 * @param playerIds
	 * @return correct lastMove
	 */
	public List<Operation> getInitialMoveOne(List<Integer> playerIds){
		int eastPlayerId = playerIds.get(0);
		List<Operation> operations = Lists.newArrayList();
	    operations.add(new SetTurn(eastPlayerId));
	    operations.add(new Set(WALL_EAST, getIndicesInRange(0, 33)));
	    operations.add(new Set(WALL_NORTH, getIndicesInRange(34, 67)));
	    operations.add(new Set(WALL_WEST, getIndicesInRange(68, 101)));
	    operations.add(new Set(WALL_SOUTH, getIndicesInRange(102, 135)));
	    operations.add(new Set(INI_CONTINUE, YES));
	    for(int i=0; i<136; i++){
	    	operations.add(new Set(T + i, tileIdToString(i)));
	    }
	    operations.add(new Shuffle(getTilesInRange(0, 135)));
	    return operations;
	}
	
	/**
	 * To assign tiles to four players
	 * @param playerIds
	 * @return correct lastMove
	 */
	public List<Operation> getInitialMoveTwo(List<Integer> playerIds){
		int eastPlayerId = playerIds.get(0);
		int northPlayerId = playerIds.get(1);
		int westPlayerId = playerIds.get(2);
		int southPlayerId = playerIds.get(3);
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Delete(INI_CONTINUE));
		operations.add(new SetTurn(eastPlayerId));
		operations.add(new Set(E, getIndicesInRange(0, 13)));
		operations.add(new Set(N, getIndicesInRange(14, 26)));
		operations.add(new Set(W, getIndicesInRange(27, 39)));
		operations.add(new Set(S, getIndicesInRange(40, 52)));
		operations.add(new Set(SPECIAL_TILE, new Integer(53)));
		operations.add(new Set(WALL_EAST, ImmutableList.of()));
		operations.add(new Set(WALL_NORTH, getIndicesInRange(54, 67)));
		operations.add(new Set(WALL_INDEX, new Integer(1)));
		operations.add(new Set(CHI_BY_E, ImmutableList.of()));
		operations.add(new Set(PENG_BY_E, ImmutableList.of()));
		operations.add(new Set(GANG_BY_E, ImmutableList.of()));
		operations.add(new Set(CHI_BY_S, ImmutableList.of()));
		operations.add(new Set(PENG_BY_S, ImmutableList.of()));
		operations.add(new Set(GANG_BY_S, ImmutableList.of()));
		operations.add(new Set(CHI_BY_W, ImmutableList.of()));
		operations.add(new Set(PENG_BY_W, ImmutableList.of()));
		operations.add(new Set(GANG_BY_W, ImmutableList.of()));
		operations.add(new Set(CHI_BY_N, ImmutableList.of()));
		operations.add(new Set(PENG_BY_N, ImmutableList.of()));
		operations.add(new Set(GANG_BY_N, ImmutableList.of()));
		operations.add(new Set(MIDDLE_TILES, ImmutableList.of()));
		for(int i=0;i<14;i++){
			operations.add(new SetVisibility(T + i, ImmutableList.of(eastPlayerId)));
		}
		for(int i=14;i<27;i++){
			operations.add(new SetVisibility(T + i, ImmutableList.of(northPlayerId)));
		}
		for(int i=27;i<40;i++){
			operations.add(new SetVisibility(T + i, ImmutableList.of(westPlayerId)));
		}
		for(int i=40;i<53;i++){
			operations.add(new SetVisibility(T + i, ImmutableList.of(southPlayerId)));
		}
		operations.add(new SetVisibility(T + 53, ImmutableList.of(eastPlayerId, northPlayerId, westPlayerId, southPlayerId)));
		return operations;
	}
	
	/**
	 * Helper method
	 * @param from
	 * @param to
	 * @return List[from...to]
	 */
	public List<Integer> getIndicesInRange(int from, int to){ 
		List<Integer> keys = Lists.newArrayList();
		for(int i = from; i<=to; i++){
			keys.add(i);
		}
		return keys;
	}
	
	/**
	 * Helper method:to get the string representation of tile
	 * @param tileId
	 * @return
	 */
	public String tileIdToString(int tileId) {
		checkArgument(tileId >= 0 && tileId < 136);
		int domain = tileId/36;
		int order = 0;
	    order = (tileId - domain*36)/4 + 1;
		switch (domain){
		case 0:return "Bam" + order;
		case 1:return "Cha" + order;
		case 2:return "Cir" + order;
		case 3:{
			switch(order){
			case 1: return "Eas0";
			case 2: return "Sou0";
			case 3: return "Wes0";
			case 4: return "Nor0";
			case 5: return "Red0";
			case 6: return "Gre0";
			case 7: return "Whi0";
			}
		}
		}
		return null;
	}
	
	/**
	 * Helper method
	 * @param from
	 * @param to
	 * @return List[T+from...T+i]
	 */
	List<String> getTilesInRange(int from, int to){
		List<String> keys = Lists.newArrayList();
		for(int i=from;i<=to;i++){
			keys.add(T + i);
		}
		return keys;
	}
	
	/**
	 * Convert gameApiState to mahjongState
	 * @param gameApiState
	 * @param turnOfPosition
	 * @param playerIds
	 * @return mahjongState
	 */
	@SuppressWarnings("unchecked")
	public MahjongState gameApiStateToMahjongState(Map<String, Object> gameApiState, Position turnOfPosition, List<Integer> playerIds){
		List<Optional<Tile>> tiles = Lists.newArrayList();
		int domain = 0;
		for(int i=0; i< 136; i++) {
			String tileString = (String) gameApiState.get(T + i);
			Tile tile;
			//if the tileString is null, it indicates this tile is invisible to the current player
			if(tileString == null) tile = null;
			else{
				String preString = tileString.substring(0,3);
				String postString = tileString.substring(3);
				if(preString.equals("Bam")) domain = 0;
				else if(preString.equals("Cha")) domain = 1;
				else if(preString.equals("Cir")) domain = 2;
				else domain = 3;
				tile = new Tile(domain, preString, postString);
			}
			tiles.add(Optional.fromNullable(tile));
		}
		List<Integer> E = (List<Integer>) gameApiState.get("E");
		List<Integer> chiByE = (List<Integer>)  gameApiState.get(CHI_BY_E);
		List<Integer> pengByE =(List<Integer>)  gameApiState.get(PENG_BY_E);
		List<Integer> gangByE = (List<Integer>)  gameApiState.get(GANG_BY_E);
		List<Integer> S = (List<Integer>)  gameApiState.get("S");
		List<Integer> chiByS = (List<Integer>)  gameApiState.get(CHI_BY_S);
		List<Integer> pengByS = (List<Integer>) gameApiState.get(PENG_BY_S);
		List<Integer> gangByS = (List<Integer>)  gameApiState.get(GANG_BY_S);
		List<Integer> W = (List<Integer>)  gameApiState.get("W");
		List<Integer> chiByW = (List<Integer>)  gameApiState.get(CHI_BY_W);
		List<Integer> pengByW = (List<Integer>)  gameApiState.get(PENG_BY_W);
		List<Integer> gangByW = (List<Integer>)  gameApiState.get(GANG_BY_W);
		List<Integer> N = (List<Integer>)  gameApiState.get("N");
		List<Integer> chiByN = (List<Integer>)  gameApiState.get(CHI_BY_N);
		List<Integer> pengByN = (List<Integer>)  gameApiState.get(PENG_BY_N);
		List<Integer> gangByN = (List<Integer>)  gameApiState.get(GANG_BY_N);
		
		List<Integer> wallEast = (List<Integer>)  gameApiState.get(WALL_EAST);
		List<Integer> wallSouth = (List<Integer>)  gameApiState.get(WALL_SOUTH);
		List<Integer> wallNorth = (List<Integer>)  gameApiState.get(WALL_NORTH);
		List<Integer> wallWest = (List<Integer>)  gameApiState.get(WALL_WEST);
		
		List<Integer> middleTiles = (List<Integer>) gameApiState.get(MIDDLE_TILES);
		
		Integer specialTile = (Integer) gameApiState.get(SPECIAL_TILE);
		Integer wallIndex = (Integer) gameApiState.get(WALL_INDEX);
		
		Integer nextTurnId = (Integer) gameApiState.get(NEXT_TURN_OF_CAST_PLAYER);
		Position nextTurnOfPosition;
		if(nextTurnId == null) 
			nextTurnOfPosition = null;
		else
			nextTurnOfPosition = Position.values()[playerIds.indexOf(nextTurnId.intValue())];
		
		Integer playerIssueId = (Integer) gameApiState.get(PLAYER_WHO_ISSUE_CAST);
		Position playerWhoIssueCast;
		if(playerIssueId == null)
			playerWhoIssueCast = null;
		else
			playerWhoIssueCast = Position.values()[playerIds.indexOf(playerIssueId.intValue())];
		
		return new MahjongState(
				turnOfPosition,
				ImmutableList.copyOf(playerIds),
				ImmutableList.copyOf(tiles),
				ImmutableList.copyOf(E),
				ImmutableList.copyOf(chiByE),
				ImmutableList.copyOf(pengByE),
				ImmutableList.copyOf(gangByE),
				ImmutableList.copyOf(S),
				ImmutableList.copyOf(chiByS),
				ImmutableList.copyOf(pengByS),
				ImmutableList.copyOf(gangByS),
				ImmutableList.copyOf(W),
				ImmutableList.copyOf(chiByW),
				ImmutableList.copyOf(pengByW),
				ImmutableList.copyOf(gangByW),
				ImmutableList.copyOf(N),
				ImmutableList.copyOf(chiByN),
				ImmutableList.copyOf(pengByN),
				ImmutableList.copyOf(gangByN),
	            wallIndex.intValue(),
				ImmutableList.copyOf(wallEast),
				ImmutableList.copyOf(wallSouth),
				ImmutableList.copyOf(wallNorth),
				ImmutableList.copyOf(wallWest),
				specialTile.intValue(),
				Optional.fromNullable(Cast.fromCastEntryInGameState((Integer) gameApiState.get(CAST))),
		        nextTurnOfPosition,
		        playerWhoIssueCast,
				gameApiState.containsKey(IS_CHI),
			    Optional.fromNullable(Chi.fromChiEntryInGameState((List<Integer>) gameApiState.get(CHI))),
				gameApiState.containsKey(IS_PENG),
			    Optional.fromNullable(Peng.fromPengEntryInGameState((List<Integer>) gameApiState.get(PENG))),
			    gameApiState.containsKey(IS_HU),
				ImmutableList.copyOf(middleTiles),
                gameApiState.containsKey(IS_HU_STATUS),
				gameApiState.containsKey(IS_PENG_STATUS),
				gameApiState.containsKey(IS_CHI_STATUS),
				gameApiState.containsKey(IS_HU_CHECK_STATUS),
				gameApiState.containsKey(IS_PENG_CHECK_STATUS),
				gameApiState.containsKey(IS_CHI_CHECK_STATUS),
				!gameApiState.containsKey(IS_HU_IS_ALLOWED)?-1:
					(boolean)gameApiState.get(IS_HU_IS_ALLOWED) ? 1 : 0,
				!gameApiState.containsKey(IS_PENG_IS_ALLOWED)?-1:
					(boolean)gameApiState.get(IS_PENG_IS_ALLOWED) ? 1 : 0,
				!gameApiState.containsKey(IS_CHI_IS_ALLOWED)?-1:
					(boolean)gameApiState.get(IS_CHI_IS_ALLOWED) ? 1 : 0,
				!gameApiState.containsKey(CHOICE_FOR_HU)?-1:
					(boolean)gameApiState.get(CHOICE_FOR_HU) ? 1 : 0,
				!gameApiState.containsKey(CHOICE_FOR_PENG)?-1:
					(boolean)gameApiState.get(CHOICE_FOR_PENG) ? 1 : 0,
				!gameApiState.containsKey(CHOICE_FOR_CHI)?-1:
					(boolean)gameApiState.get(CHOICE_FOR_CHI) ? 1 : 0
				);
	}
	
   
	/**
	 * Helper method:concat a and b
	 * @param a
	 * @param b
	 * @return a list connecting a and b
	 */
	public <T> List<T> concat(List<T> a, List<T> b){
		if(a==null) return b;
		if(b==null) return a;
		return Lists.newArrayList(Iterables.concat(a,b));
	}
	
	/**
	 * Helper method:remove the first element of a list
	 * @param a
	 * @return a List removing its first element
	 */
	<T> List<T> subtractHead(List<T> a){
		if(a == null || a.size() == 0) return a; 
		List<T> tmp = new ArrayList<T>();
		for(int i=1;i<a.size();i++) tmp.add(a.get(i));
	    return tmp;
	}
	
	/**
	 * Helper method: Delete the element of melds in List a
	 * @param a
	 * @param melds
	 * @return List
	 */
	List<Integer> substract(List<Integer> a, Melds melds){
		List<Integer> tmp = new ArrayList<Integer>();
		for(int i=0;i<a.size();i++){
			if(!melds.getFirst().equals(a.get(i)) && !melds.getSecond().equals(a.get(i)))
			  tmp.add(a.get(i));
		}
	    return Lists.newArrayList(tmp);
	}
	
	  <T> List<T> subtract(List<T> removeFrom, List<T> elementsToRemove) {
		    check(removeFrom.containsAll(elementsToRemove), removeFrom, elementsToRemove);
		    List<T> result = Lists.newArrayList(removeFrom);
		    result.removeAll(elementsToRemove);
		    check(removeFrom.size() == result.size() + elementsToRemove.size());
		    return result;
		  }

	
	/**
	 * Helper method: Delete the element of cast in List a
	 * @param a
	 * @param cast
	 * @return List
	 */
	List<Integer> substractCast(List<Integer> a, Cast cast){
		List<Integer> tmp = new ArrayList<Integer>();
		for(int i=0;i<a.size();i++){
			if(!cast.getValue().equals(a.get(i)))
			  tmp.add(a.get(i));
		}
	    return tmp;
	}
	
	/**
	 * Helper method
	 * @param index
	 * @return wall name
	 */
	String getWallName(int index){
		switch (index){
		case 0: return WALL_EAST;
		case 1: return WALL_NORTH;
		case 2: return WALL_WEST;
		case 3: return WALL_SOUTH;
		}
		return null;
	}
	
	/**
	 * Helper method: check whether the element of melds are in currentTile
	 * @param melds
	 * @param currentTile
	 * @return true if melds in currentTile, or false
	 */
    boolean checkInTheRange(Melds melds, List<Integer> currentTile){
    	boolean res1 = false;
    	boolean res2 = false;
    	for(int i=0;i<currentTile.size();i++){
    		if(melds.getFirst().equals(currentTile.get(i))) res1 = true;
    		if(melds.getSecond().equals(currentTile.get(i))) res2 = true;
    	}
    	return res1&&res2;
    }
    
    /**
     * Helper method: check whether the element of cast ate in currentTile
     * @param cast
     * @param currentTile
     * @return
     */
    boolean checkInTheRangeCast(Cast cast, List<Integer> currentTile){
    	boolean res = false;
    	for(int i=0;i<currentTile.size();i++){
    		if(cast.getValue().equals(currentTile.get(i))) return true;
    	}
    	return res;
    }
	
    /**
     * Helper method: to judge whether the three tiles can form Chi
     * @param castValue
     * @param specialTile
     * @param chiTiles
     * @return true if can form Chi, or false
     */
	public boolean canFormChi(Optional<Tile> castValue, Optional<Tile> specialTile, List<Optional<Tile>> chiTiles){
		boolean b1 = isSpecialTile(castValue, specialTile);
		boolean b2 = isSpecialTile(chiTiles.get(0), specialTile);
		boolean b3 = isSpecialTile(chiTiles.get(1), specialTile);
		Tile tile1 = castValue.get();
		Tile tile2 = chiTiles.get(0).get();
		Tile tile3 = chiTiles.get(1).get();
		if(b1&&b2&&b3) return true;
		if(b1&&b2&&(tile3.getDomain()!=3)) return true;
		if(b1&&b3&&(tile2.getDomain()!=3)) return true;
		if(b2&&b3&&(tile1.getDomain()!=3)) return true;
		if(b1 || b2 || b3){
			if(b1){
				return canFormSequenceTwo(tile2, tile3);
			}else if(b2){
				return canFormSequenceTwo(tile1, tile3);
			}else{
			    return canFormSequenceTwo(tile1, tile2);
			}
		}else{
			return canFormSequenceThree(tile1, tile2, tile3);
		}
	}
	
	/**
	 * Helper method
	 * @param tile1
	 * @param tile2
	 * @return true if tile1 and tile2 are in sequence or false
	 */
	boolean canFormSequenceTwo(Tile tile1, Tile tile2){
		if(tile1.getDomain() != tile2.getDomain()) return false;
		if(tile1.getDomain() == 3) return false;
		if((tile1.getPost() == (tile2.getPost() + 1)%9) || (tile2.getPost() == (tile1.getPost() + 1)%9)) return true;
		else return false;
	}
	
	/**
	 * Helper method
	 * @param tile1
	 * @param tile2
	 * @param tile3
	 * @return true if tile1,tile2,tile3 are in sequence or false
	 */
	boolean canFormSequenceThree(Tile tile1, Tile tile2, Tile tile3){
		if(tile1.getDomain() != tile2.getDomain() || tile1.getDomain() != tile3.getDomain() || tile2.getDomain() != tile3.getDomain()) return false;
		if(tile1.getDomain() == 3) return false;
		int[] postIndex = new int[3];
		postIndex[0] = tile1.getPost();
		postIndex[1] = tile2.getPost();
		postIndex[2] = tile3.getPost();
		Arrays.sort(postIndex);
		if(((postIndex[2]-postIndex[1])==1) && ((postIndex[1]-postIndex[0])==1)) return true;
		else return false;
 	}
	
	/**
	 * helper method:invoke the method of canFormHu 
	 * @param myPosition
	 * @return the result of canFormHu
	 */
	public boolean invokeHuCheckMethod(Position myPosition, MahjongState mahjongState) {
		Optional<Tile> castValue;
		if(mahjongState.getCast().isPresent())
	    castValue = mahjongState.getTiles().get(mahjongState.getCast().get().getValue());
		else
		castValue = null;
		Optional<Tile> specialTile = mahjongState.getTiles().get(mahjongState.getSpecialTile());
	    List<Optional<Tile>> huTiles = Lists.newArrayList();
	    for(int i=0;i<mahjongState.getOneOfFourTile(myPosition).size();i++) {
	    	huTiles.add(mahjongState.getTiles().get(mahjongState.getOneOfFourTile(myPosition).get(i)));
	    }
	    List<Optional<Tile>> chiTiles = Lists.newArrayList();
	    for(int i=0;i<mahjongState.getOneOfFourChi(myPosition).size();i++) {
			chiTiles.add(mahjongState.getTiles().get(mahjongState.getOneOfFourChi(myPosition).get(i)));
		}
	    List<Optional<Tile>> pengTiles = Lists.newArrayList();
	    for(int i=0;i<mahjongState.getOneOfFourPeng(myPosition).size();i++) {
			pengTiles.add(mahjongState.getTiles().get(mahjongState.getOneOfFourPeng(myPosition).get(i)));
		}
	    List<Optional<Tile>> gangTiles = Lists.newArrayList();
	    for(int i=0;i<mahjongState.getOneOfFourGang(myPosition).size();i++) {
			gangTiles.add(mahjongState.getTiles().get(mahjongState.getOneOfFourGang(myPosition).get(i)));
		}
		return canFormHu(castValue, specialTile, huTiles, chiTiles, pengTiles, gangTiles);
	}
	
	/**
	 * helper method:invoke the method of canFormChi
	 * @param myPosition
	 * @return the result of canFormChi
	 */
	public boolean invokeChiCheckMethod(Position myPosition, MahjongState mahjongState) {
		Optional<Tile> castValue;
		if(mahjongState.getCast().isPresent())
	    castValue = mahjongState.getTiles().get(mahjongState.getCast().get().getValue());
		else
		castValue = null;
		Optional<Tile> specialTile = mahjongState.getTiles().get(mahjongState.getSpecialTile());
		List<Optional<Tile>> chiTiles = Lists.newArrayList();
		int index = 0;
		for(int i=0;i<mahjongState.getOneOfFourChi(myPosition).size();i++) {
			index = mahjongState.getOneOfFourChi(myPosition).get(i);
			chiTiles.add(mahjongState.getTiles().get(index));
		}
		return canFormChi(castValue, specialTile, chiTiles);
	}
	
	/**
	 * helper method:invoke the method of canFormPeng
	 * @param myPosition
	 * @return the resutl of canFormPeng
	 */
	public boolean invokePengCheckMethod(Position myPosition, MahjongState mahjongState) {
		Optional<Tile> castValue;
		if(mahjongState.getCast().isPresent())
		castValue = mahjongState.getTiles().get(mahjongState.getCast().get().getValue());
		else
	    castValue = null;
		Optional<Tile> specialTile = mahjongState.getTiles().get(mahjongState.getSpecialTile());
		List<Optional<Tile>> pengTiles = Lists.newArrayList();
		int index = 0;
		for(int i=0;i<mahjongState.getOneOfFourPeng(myPosition).size();i++) {
			index = mahjongState.getOneOfFourPeng(myPosition).get(i);
			pengTiles.add(mahjongState.getTiles().get(index));
		}
		return canFormPeng(castValue, specialTile, pengTiles);
	}
	/**
	 * Helper method:Check whether the three tiles can form Peng
	 * @param castValue
	 * @param specialTile
	 * @param pengTiles
	 * @return true if can form Peng, or false
	 */
	 public boolean canFormPeng(Optional<Tile> castValue, Optional<Tile> specialTile, List<Optional<Tile>> pengTiles){
		boolean b1 = isSpecialTile(castValue, specialTile);
		boolean b2 = isSpecialTile(pengTiles.get(0), specialTile);
		boolean b3 = isSpecialTile(pengTiles.get(1), specialTile);
		Tile tile1 = castValue.get();
		Tile tile2 = pengTiles.get(0).get();
		Tile tile3 = pengTiles.get(1).get();
		if((b1&&b2) || (b2&&b3) || (b3&&b1)) return true;
		if(b1 || b2 || b3){
			if(b1){
				return tile2.equals(tile3);
			}else if(b2){
				return tile1.equals(tile3);
			}else{
			    return tile1.equals(tile2);
			}
		}else{
			return tile1.equals(tile2) && tile2.equals(tile3);
		}
	}
	
	/**
	 * Helper method: Check whether the tiles can form Hu
	 * @param castValue
	 * @param specialTile
	 * @param huTiles
	 * @param chiTiles
	 * @param pengTiles
	 * @param gangTiles
	 * @return true if can form Hu or false
	 */
    public boolean canFormHu(Optional<Tile> castValue, Optional<Tile> specialTile, 
	    List<Optional<Tile>> huTiles, List<Optional<Tile>> chiTiles,
	    List<Optional<Tile>> pengTiles, List<Optional<Tile>> gangTiles){
		int tileCount = 0;
		List<Optional<Tile>> validTiles = new ArrayList<Optional<Tile>>(huTiles);
		if(huTiles!=null && huTiles.size() != 0) tileCount = huTiles.size();
		if(!isValidNumber(tileCount)) {
			validTiles.add(castValue);
		}
		List<Optional<Tile>> speTiles = new ArrayList<Optional<Tile>>();
		List<Optional<Tile>> notSpeTiles = new ArrayList<Optional<Tile>>();
		for(Optional<Tile> ot : validTiles){
			if(isSpecialTile(ot,specialTile)){
				 speTiles.add(ot);
			}else{
				notSpeTiles.add(ot);
			}
		}
		for(int l=0;l<notSpeTiles.size();l++) {
			int speTileNum = speTiles.size();
			Optional<Tile> eyeTile = notSpeTiles.get(l);
			List<Optional<Tile>> notSpeEyeTiles = new ArrayList<Optional<Tile>>(notSpeTiles);
			notSpeEyeTiles.remove(l);
			boolean single = true;
			for(int k=l;k<notSpeEyeTiles.size();k++) {
				if(notSpeEyeTiles.get(k).get().equals(eyeTile.get())){
					notSpeEyeTiles.remove(k);
					single = false;
					break;
				}
			}
			if(single) -- speTileNum;
			if(speTileNum < 0) continue;
		    List<Optional<Tile>> bamTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> chaTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> cirTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> easTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> norTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> wesTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> souTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> redTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> greTiles = new ArrayList<Optional<Tile>>();
		    List<Optional<Tile>> whiTiles = new ArrayList<Optional<Tile>>();
		    for(int i=0;i<notSpeEyeTiles.size();i++){
			   switch (notSpeEyeTiles.get(i).get().getPre()){
			   case "Bam":bamTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Cha":chaTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Cir":cirTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Eas":easTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Nor":norTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Wes":wesTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Sou":souTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Red":redTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Gre":greTiles.add(notSpeEyeTiles.get(i)); break;
			   case "Whi":whiTiles.add(notSpeEyeTiles.get(i)); break;
			   }
		    }
	       Collections.sort(bamTiles,new TileComparator());
	       Collections.sort(chaTiles,new TileComparator());
	       Collections.sort(cirTiles,new TileComparator());
			int numForSpec = numOfSpecTilesTwo(bamTiles) + numOfSpecTilesTwo(chaTiles) + numOfSpecTilesTwo(cirTiles)
					+ numOfSpeTilesOne(easTiles) + numOfSpeTilesOne(norTiles) 
					+ numOfSpeTilesOne(wesTiles) + numOfSpeTilesOne(souTiles) 
					+ numOfSpeTilesOne(redTiles) + numOfSpeTilesOne(greTiles) 
					+ numOfSpeTilesOne(whiTiles);
			if(numForSpec == speTileNum) return true;
		}
		return false;
    }
	
	/**
	 * Helper method:Get the number of special tiles being cost to make the tiles consist of only Chi and Peng
	 * @param tiles(for Eas, Nor, Wes, Sou, Red, Gre, Whi)
	 * @return number of special tiles cost
	 */
	int numOfSpeTilesOne(List<Optional<Tile>> tiles) {
		int size = tiles.size();
		if(size%3 == 0) return 0;
		else {
			return 3 - size%3;
		}
	}
	
	/**
	 * Helper method:Get the number of special tiles being cost to make the tiles consist of only Chi and Peng
	 * @param tiles(for Bam, Cha, Cir)
	 * @return number of special tiles cost
	 */
	int numOfSpecTilesTwo(List<Optional<Tile>> tiles) {
		int size = tiles.size();
		int num = 0;
		if(size%3 == 0) {
		  num = 0;
		}else{
		  num = 3 - size % 3;
		}
		int count = num;
		if(!canFormHuUnderSpe(tiles, count)) {
			if(!canFormHuUnderSpe(tiles, count+3)){
				return count + 6;
			}else{
				return count + 3;
			}
		}else{
			return count;
		}
	}
	
	/**
	 * Helper method:Check whether tiles can become a correct form under the given number of special tiles
	 * @param tiles
	 * @param numOfSpec
	 * @return true if tiles can become a correct for which only consists of Chi and Peng under the given number of special tiles or false
	 */
	boolean canFormHuUnderSpe(List<Optional<Tile>> tiles, int numOfSpec) {
		if(numOfSpec < 0) return false;
		if(tiles.size() == 0 ) return true;
		List<Optional<Tile>> newTiles1 = new ArrayList<Optional<Tile>>(tiles);
		List<Optional<Tile>> newTiles2 = new ArrayList<Optional<Tile>>(tiles);
		Optional<Tile> tmpTile1 = newTiles1.get(0);
		Optional<Tile> tmpTile2 = newTiles2.get(0);
		int countPeng = 0;
		int countChi = 0;
		for(int i=1;i<newTiles1.size();i++){
			if(newTiles1.get(i).get().equals(tmpTile1.get())) {
				countPeng++;
				newTiles1.remove(i);
				i--;
				if(countPeng == 2) break;
			}
		}
		int diff = 1;
		for(int i=1;i<newTiles2.size();i++){
			if(newTiles2.get(i).get().getPost() == (tmpTile2.get().getPost() + diff)) {
				countChi++;
				newTiles2.remove(i);
				i--;
				diff++;
				if(countChi == 2) break;
			}
		}
		return canFormHuUnderSpe(newTiles1, numOfSpec-(2-countPeng)) || canFormHuUnderSpe(newTiles2, numOfSpec-(2-countChi));
	}
	
	/**
	 * A comparator to sort bam, cha, cir
	 * @author Xiaocong Wang
	 *
	 */
	public class TileComparator implements Comparator<Optional<Tile>> {
		public int compare(Optional<Tile> t1, Optional<Tile> t2) {
			return t1.get().getPost() - t2.get().getPost();
		}
	}
	
	/**
	 * Helper method:Check whether it has bonus in the pengTiles of the player
	 * @param pengTiles
	 * @param specialTile
	 * @return true if it has bonus in the pengTiles
	 */
	boolean hasBonusInPengList(List<Optional<Tile>> pengTiles, Optional<Tile> specialTile) {
		if(pengTiles == null || pengTiles.size() == 0) return false;
		int num = pengTiles.size() / 3;
		for(int i=0;i<num;i++){
			if(isBonusPeng(pengTiles.get(i*3),pengTiles.get(i*3+1), pengTiles.get(i*3+2),specialTile)) return true;
		}
		return false;
	}
	
	/**
	 * Helper method:Check whether the three tiles are the peng which can get bonus
	 * @param one
	 * @param two
	 * @param three
	 * @param specialTile
	 * @return true if one, two, three can form peng which can get bonus
	 */
	boolean isBonusPeng(Optional<Tile> one, Optional<Tile> two, Optional<Tile> three, Optional<Tile> specialTile) {
		boolean b1 = isSpecialTile(one, specialTile);
		boolean b2 = isSpecialTile(two, specialTile);
		boolean b3 = isSpecialTile(three, specialTile);
		if(b1&&b2&&b3) return true;
		if(b1&&b2&&(isBonusTile(three.get().getPre()))) return true;
		if(b1&&b3&&(isBonusTile(two.get().getPre()))) return true;
		if(b2&&b3&&(isBonusTile(one.get().getPre()))) return true;
		if(b1&&(isBonusTile(two.get().getPre()))&&(isBonusTile(three.get().getPre()))) return true;
		if(b2&&(isBonusTile(one.get().getPre()))&&(isBonusTile(three.get().getPre()))) return true;
		if(b3&&(isBonusTile(one.get().getPre()))&&(isBonusTile(two.get().getPre()))) return true;
		if((isBonusTile(one.get().getPre()))&&(isBonusTile(two.get().getPre()))&&(isBonusTile(three.get().getPre()))) return true;
		return false;
	}
	
	/**
	 * Helper method:Check whether pre is a bonus tile
	 * @param pre
	 * @return true if pre is bonus tile or false
	 */
	boolean isBonusTile(String pre){
		return pre.equals("Eas") || pre.equals("Red") || pre.equals("Gre") || pre.equals("Whi");
	}
	
	/**
	 * Helper method
	 * @param count
	 * @return true if count == 3*k + 2 or false
	 */
	boolean isValidNumber(int count){
		return ((count-2)%3 == 0);
	}
	
	/**
	 * Helper method:Check Whether tile is a special tile
	 * @param tile
	 * @param specialTile
	 * @return true if tile is speical tile or false
	 */
	boolean isSpecialTile(Optional<Tile> tile, Optional<Tile> specialTile) {
		Tile t = tile.get();
		Tile st = specialTile.get();
		if(t.equals(st)) return true;
		if(t.getDomain() != st.getDomain()) return false;
		if(t.getDomain() != 3){
			return (st.getPost()+1)%9 == t.getPost();
		}else{
			switch (st.getPre()){
			case "Eas": return t.getPre().equals("Sou"); 
			case "Nor": return t.getPre().equals("Eas");
			case "Wes": return t.getPre().equals("Nor");
			case "Sou": return t.getPre().equals("Wes");
			case "Red": return t.getPre().equals("Gre");
			case "Gre": return t.getPre().equals("Whi");
			case "Whi": return t.getPre().equals("Red");
			}
			return false;
		}
	}
	
	/**
	 * Get the correct version of lastMove for fetch a tile
	 * @param state
	 * @return lastMove of fetch
	 */
	public List<Operation> declareFetchMove(MahjongState state){
		Position turnOfPosition = state.getTurn();
		int lastWallIndex = state.getWallIndex();
		
		List<Integer> lastWall = state.getOneOfFourWall(lastWallIndex);
		List<Integer> currentWall = subtractHead(lastWall);
		
		List<Integer> lastTile = state.getOneOfFourTile(turnOfPosition);
		Integer fetchTile = lastWall.get(0);
		List<Integer> fetchTileList = ImmutableList.<Integer>of(fetchTile);
		List<Integer> currentTile = concat(fetchTileList, lastTile);
		
		List<Operation> operations = Lists.newArrayList();
		operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
		operations.add(new Set(IS_FETCH, YES));
		operations.add(new Set(turnOfPosition.name(),currentTile));
		operations.add(new Set(getWallName(lastWallIndex),currentWall));
		operations.add(new SetVisibility(T + fetchTile, ImmutableList.of(state.getPlayerId(turnOfPosition))));
		operations.add(new Set(WALL_INDEX,currentWall.size()==0?lastWallIndex+1:lastWallIndex));
		operations.add(new Set(IS_HU_CHECK_STATUS, YES));
		operations.add(new Delete(IS_FETCH));
		
		return operations;
	}

    /**
     * Get the correct version of lastMove for cast a tile
     * @param state
     * @param lastMove
     * @return lastMove of cast a tile
     */
    public List<Operation> doCastMove(MahjongState state, List<Operation> lastMove){
    	Position turnOfPosition = state.getTurn();
    	Position nextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
    	
    	Set setCast = (Set) lastMove.get(1);
    	Cast cast = checkNotNull(Cast.fromCastEntryInGameState((Integer) setCast.getValue()));
    	
    	List<Integer> currentTile = state.getOneOfFourTile(turnOfPosition);
    	
    	List<Integer> currentMiddleTile = state.getMiddleTiles();
    	
    	checkArgument(checkInTheRangeCast(cast,currentTile));
    	
    	List<Operation> operations = Lists.newArrayList();
    	operations.add(new SetTurn(state.getPlayerId(nextTurnOfPosition)));
    	operations.add(new Set(CAST, cast.getValue().intValue()));
    	operations.add(new Set(turnOfPosition.name(), substractCast(currentTile, cast)));
    	operations.add(new Set(NEXT_TURN_OF_CAST_PLAYER, state.getPlayerId(nextTurnOfPosition)));
    	operations.add(new Set(PLAYER_WHO_ISSUE_CAST, state.getPlayerId(turnOfPosition)));
    	operations.add(new Set(MIDDLE_TILES, concat(ImmutableList.of(cast.getValue()),currentMiddleTile)));
    	operations.add(new Set(IS_HU_CHECK_STATUS, YES));
    	operations.add(new SetVisibility(T+cast.getValue()));
    	
    	return operations;
    }
    
	/**
	 * Get a correct version of lastMove for Automatic Hu Check
	 * @param state
	 * @param lastMove
	 * @return lastMove of Automatic Hu Check
	 */
	public List<Operation> declareAutoHuCheckMove(MahjongState state, List<Operation> lastMove){
		check(state.isHuCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		Position playerWhoIssueCast = state.getPlayerWhoIssueCast();
		Set setHuIsAllowed = null;
		boolean huIsAllowed;
				
		List<Operation> operations = Lists.newArrayList();
		if(playerWhoIssueCast != null){
			if(turnOfPosition == playerWhoIssueCast) {
				operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			    operations.add(new Delete(IS_HU_CHECK_STATUS));
			    operations.add(new Set(IS_PENG_CHECK_STATUS, YES));
			    return operations;
			}
		
			setHuIsAllowed = (Set) lastMove.get(1);
			huIsAllowed = (Boolean) setHuIsAllowed.getValue();
		
			if(!huIsAllowed){
				operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			    operations.add(new Set(HU_IS_ALLOWED, huIsAllowed));
			    operations.add(new Delete(HU_IS_ALLOWED));
			}else{
				operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
				operations.add(new Set(HU_IS_ALLOWED, huIsAllowed));
		    }
			return operations;
		}else {
			setHuIsAllowed = (Set) lastMove.get(1);
			huIsAllowed = (Boolean) setHuIsAllowed.getValue();
			
			if(!huIsAllowed) {
				operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
				operations.add(new Set(HU_IS_ALLOWED, huIsAllowed));
				operations.add(new Delete(IS_HU_CHECK_STATUS));
				operations.add(new Delete(HU_IS_ALLOWED));
			}else {
				operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
				operations.add(new Set(HU_IS_ALLOWED, huIsAllowed));
			}
			return operations;
		}
	}
	
	/**
	 * Get a correct version of lastMove for hu choice making
	 * @param state
	 * @param lastMove
	 * @return
	 */
	public List<Operation> waitForHuChoice(MahjongState state, List<Operation> lastMove) {
		check(state.isHuCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		Position playerWhoIssueCast = state.getPlayerWhoIssueCast();
		
		List<Operation> operations = Lists.newArrayList();
		
		Set setChoiceForHu =(Set) lastMove.get(1);
		boolean choiceForHu = (Boolean) setChoiceForHu.getValue();
		
		if(playerWhoIssueCast != null){
			if(!choiceForHu) {
				operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			    operations.add(new Set(CHOICE_FOR_HU, choiceForHu));
			    operations.add(new Delete(CHOICE_FOR_HU));
			    operations.add(new Delete(HU_IS_ALLOWED));
		    }else {
			    operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			    operations.add(new Set(CHOICE_FOR_HU, choiceForHu));
			    operations.add(new Delete(IS_HU_CHECK_STATUS));
			    operations.add(new Set(IS_HU_STATUS, YES));
			    operations.add(new Delete(HU_IS_ALLOWED));
			    operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
			    operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
		    }
		    return operations;
		}else{
			if(!choiceForHu) {
				operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			    operations.add(new Set(CHOICE_FOR_HU, choiceForHu));
			    operations.add(new Delete(CHOICE_FOR_HU));
			    operations.add(new Delete(HU_IS_ALLOWED));
			    operations.add(new Delete(IS_HU_CHECK_STATUS));
		    }else {
			    operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			    operations.add(new Set(CHOICE_FOR_HU, choiceForHu));
			    operations.add(new Delete(IS_HU_CHECK_STATUS));
			    operations.add(new Set(IS_HU_STATUS, YES));
			    operations.add(new Delete(HU_IS_ALLOWED));
			    operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
			    operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
		    }
		    return operations;
		}
	}
	
	/**
	 * Get the correct version of lastMove for Hu declare
	 * @param state
	 * @return lastMove of Hu declare 
	 */
    public List<Operation> declareHuMove(MahjongState state){
    	check(!state.isHu());
    	check(state.isHuStatus());
    	
    	Position turnOfPosition = state.getTurn();
    	ImmutableList<Integer> currentTiles = state.getOneOfFourTile(turnOfPosition);
    	
    	List<Operation> operations = Lists.newArrayList();
    	
    	operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
    	operations.add(new Set(IS_HU,YES));
    	for(Integer iter : currentTiles)
    		operations.add(new SetVisibility(T+iter));
    	operations.add(new Delete(CHOICE_FOR_HU));
    	return operations;
    }
    
    /**
     * Get the correct version of lastMove for Hu
     * @param state
     * @return lastMove of Hu
     */
    public List<Operation> checkIfHuMove(MahjongState state){
    	check(state.isHu());
    	check(state.isHuStatus());
    	
    	Position turnOfPosition = state.getTurn();
    	ImmutableList<Integer> currentTiles = state.getOneOfFourTile(turnOfPosition);
    	
    	Cast cast = state.getCast().get();
    	
    	Optional<Tile>  castValue;
    	Optional<Tile>  specialTile = state.getTiles().get(state.getSpecialTile());
    	List<Optional<Tile>> huTiles = new ArrayList<Optional<Tile>>();
    	
    	for(int i=0;i<currentTiles.size();i++){
    		huTiles.add(state.getTiles().get(currentTiles.get(i).intValue()));
    	}
    	
    	if(cast != null){
    		castValue = state.getTiles().get(cast.getValue().intValue());
    	}else{
    		castValue = Optional.fromNullable(null);
    	}
    	
    	List<Integer> chi = state.getOneOfFourChi(turnOfPosition);
        List<Integer> peng = state.getOneOfFourPeng(turnOfPosition);
        List<Integer> gang = state.getOneOfFourGang(turnOfPosition);
        
    	List<Optional<Tile>> chiTiles = new ArrayList<Optional<Tile>>();
        if(chi != null){ 
    	  for(int i=0;i<chi.size();i++) {
    		  chiTiles.add(state.getTiles().get(chi.get(i).intValue()));
    	  }
        }
    	List<Optional<Tile>> pengTiles = new ArrayList<Optional<Tile>>();
    	if(peng != null){
    	  for(int i=0;i<peng.size();i++) {
    		  pengTiles.add(state.getTiles().get(peng.get(i).intValue()));
    	  }
    	}
    	List<Optional<Tile>> gangTiles = new ArrayList<Optional<Tile>>();
    	if(gang != null){
    	  for(int i=0;i<gang.size();i++) {
    		  gangTiles.add(state.getTiles().get(gang.get(i).intValue()));
    	  }
    	}
    	
    	List<Operation> operations = Lists.newArrayList();
    	operations.add(new Delete(IS_HU));
    	operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
    	operations.add(new Delete(IS_HU_STATUS));
    	
    	if(canFormHu(castValue, specialTile, huTiles, chiTiles, pengTiles, gangTiles)){
    		operations.add(new EndGame(state.getPlayerId(turnOfPosition)));
    	}
    	else{
    		operations.add(new EndGame(state.getPlayerId(turnOfPosition)-3));
    	}
    	return operations;
    }
	
	/**
	 * Get a correct version of lastMove for Automatic Hu Check
	 * @param state
	 * @param lastMove
	 * @return lastMove of Automatic Hu Check
	 */
	public List<Operation> declareAutoPengCheckMove(MahjongState state, List<Operation> lastMove){
		check(state.isPengCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		Position playerWhoIssueCast = state.getPlayerWhoIssueCast();
				
		List<Operation> operations = Lists.newArrayList();
		if(turnOfPosition == playerWhoIssueCast) {
			operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			operations.add(new Delete(IS_PENG_CHECK_STATUS));
			operations.add(new Set(IS_CHI_CHECK_STATUS, YES));
			return operations;
		}
		
		Set setPengIsAllowed = (Set) lastMove.get(1);
		boolean pengIsAllowed = (Boolean) setPengIsAllowed.getValue();
		
		if(!pengIsAllowed){
			operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			operations.add(new Set(PENG_IS_ALLOWED, pengIsAllowed));
			operations.add(new Delete(PENG_IS_ALLOWED));
		}else{
			operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			operations.add(new Set(PENG_IS_ALLOWED, pengIsAllowed));
		}
		return operations;
	}
	
	/**
	 * Get a correct version of lastMove for hu choice making
	 * @param state
	 * @param lastMove
	 * @return
	 */
	public List<Operation> waitForPengChoice(MahjongState state, List<Operation> lastMove) {
		check(state.isPengCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		
		List<Operation> operations = Lists.newArrayList();
		
		Set setChoiceForPeng =(Set) lastMove.get(1);
		boolean choiceForPeng = (Boolean) setChoiceForPeng.getValue();
		
		if(!choiceForPeng) {
			operations.add(new SetTurn(state.getPlayerId(myNextTurnOfPosition)));
			operations.add(new Set(CHOICE_FOR_PENG, choiceForPeng));
			operations.add(new Delete(CHOICE_FOR_PENG));
			operations.add(new Delete(PENG_IS_ALLOWED));
		}else {
			operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			operations.add(new Set(CHOICE_FOR_PENG, choiceForPeng));
			operations.add(new Delete(IS_PENG_CHECK_STATUS));
			operations.add(new Set(IS_PENG_STATUS, YES));
			operations.add(new Delete(PENG_IS_ALLOWED));
			operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
			operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
		}
		return operations;
	}
	
	/**
	 * Get a correct version of lastMove for peng Declare
	 * @param state
	 * @param lastMove
	 * @return lastMove of Peng declare
	 */
    public List<Operation> declarePengMove(MahjongState state, List<Operation> lastMove){
    	check(!state.isPeng());
    	check(state.isPengStatus());
    	
    	Position turnOfPosition = state.getTurn();
    	Set setPeng = (Set) lastMove.get(2);
    	List<Integer> currentTile = state.getOneOfFourTile(turnOfPosition);
    	Peng peng = checkNotNull(Peng.fromPengEntryInGameState((List<Integer>) setPeng.getValue()));
    	checkArgument(checkInTheRange(peng,currentTile));
    	
    	List<Operation> operations = Lists.newArrayList();
    	
    	operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
    	operations.add(new Set(IS_PENG,YES));
    	operations.add(new Set(PENG,ImmutableList.<Integer>of(peng.getFirst(),peng.getSecond())));
    	operations.add(new Delete(CHOICE_FOR_PENG));
    	operations.add(new SetVisibility(T+peng.getFirst()));
    	operations.add(new SetVisibility(T+peng.getSecond()));
    	
    	return operations;
    }
    
    /**
     * Get the correct version of lastMove for Peng
     * @param state
     * @param lastMove
     * @return lastMove of Peng
     */
	public List<Operation> checkIfPengMove(MahjongState state){
		check(state.isPeng());
		check(state.isPengStatus());
		
		Position turnOfPosition = state.getTurn();
		
		Peng peng = state.getPeng().get();
		List<Optional<Tile>> pengTiles = ImmutableList.<Optional<Tile>>of(state.getTiles().get(peng.getFirst().intValue()), state.getTiles().get(peng.getSecond().intValue()));
		
		int specialIndex = state.getSpecialTile();
		Optional<Tile> specialTile = state.getTiles().get(specialIndex);
				
		Cast castTile = state.getCast().get();
		Optional<Tile> castValue = state.getTiles().get(castTile.getValue().intValue());
		
		List<Operation> operations = Lists.newArrayList();
		
		operations.add(new Delete(IS_PENG));
		operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
		operations.add(new Delete(IS_PENG_STATUS));
		if(canFormPeng(castValue,specialTile,pengTiles))
		{
			operations.add(new Set(turnOfPosition.name(),substract(state.getOneOfFourTile(turnOfPosition),peng)));
			operations.add(new Set(turnOfPosition.nameOfPeng(),concat(state.getOneOfFourPeng(turnOfPosition),ImmutableList.<Integer>of(peng.getFirst(),peng.getSecond(),castTile.getValue()))));
			operations.add(new SetVisibility(T + peng.getFirst()));
			operations.add(new SetVisibility(T + peng.getSecond()));
		}
		
		return operations;
	}
	
	/**
	 * Get a correct version of lastMove for Automatic Hu Check
	 * @param state
	 * @param lastMove
	 * @return lastMove of Automatic Hu Check
	 */
	public List<Operation> declareAutoChiCheckMove(MahjongState state, List<Operation> lastMove){
		check(state.isChiCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		Position playerWhoIssueCast = state.getPlayerWhoIssueCast();
				
		List<Operation> operations = Lists.newArrayList();
		
		Set setChiIsAllowed = (Set) lastMove.get(1);
		boolean chiIsAllowed = (Boolean) setChiIsAllowed.getValue();
		
		operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
		if(!chiIsAllowed){
			operations.add(new Set(CHI_IS_ALLOWED, chiIsAllowed));
			operations.add(new Delete(CHI_IS_ALLOWED));
			operations.add(new Delete(IS_CHI_CHECK_STATUS));
			operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
			operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
		}else{
			operations.add(new Set(CHI_IS_ALLOWED, chiIsAllowed));
		}
		return operations;
	}
	
	/**
	 * Get a correct version of lastMove for hu choice making
	 * @param state
	 * @param lastMove
	 * @return
	 */
	public List<Operation> waitForChiChoice(MahjongState state, List<Operation> lastMove) {
		check(state.isChiCheckStatus());
		
		Position turnOfPosition = state.getTurn();
		Position myNextTurnOfPosition = turnOfPosition.getNextTurnOfPosition();
		
		List<Operation> operations = Lists.newArrayList();
		
		Set setChoiceForChi =(Set) lastMove.get(1);
		boolean choiceForChi = (Boolean) setChoiceForChi.getValue();
		
		if(!choiceForChi) {
			operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			operations.add(new Set(CHOICE_FOR_CHI, choiceForChi));
			operations.add(new Delete(CHOICE_FOR_PENG));
			operations.add(new Delete(CHI_IS_ALLOWED));
			operations.add(new Delete(IS_CHI_CHECK_STATUS));
			operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
			operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
		}else {
			operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
			operations.add(new Set(CHOICE_FOR_CHI, choiceForChi));
			operations.add(new Delete(IS_CHI_CHECK_STATUS));
			operations.add(new Set(IS_CHI_STATUS, YES));
			operations.add(new Delete(PLAYER_WHO_ISSUE_CAST));
			operations.add(new Delete(CHI_IS_ALLOWED));
			operations.add(new Delete(NEXT_TURN_OF_CAST_PLAYER));
		}
		return operations;
	}
    
	/**
	 * Get a correct version of lastMove for the Chi declare
	 * @param state
	 * @param lastMove
	 * @return lastMove of Chi declare
	 */
    public List<Operation> declareChiMove(MahjongState state, List<Operation> lastMove){
    	check(!state.isChi());
    	check(state.isChiStatus());
    	
    	Position turnOfPosition = state.getTurn();
    	Set setChi = (Set) lastMove.get(2);
    	List<Integer> currentTile = state.getOneOfFourTile(turnOfPosition);
    	Chi chi = checkNotNull(Chi.fromChiEntryInGameState((List<Integer>) setChi.getValue()));
    	checkArgument(checkInTheRange(chi,currentTile));
    	
    	List<Operation> operations = Lists.newArrayList();
    	
    	operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
    	operations.add(new Set(IS_CHI,YES));
    	operations.add(new Set(CHI,ImmutableList.<Integer>of(chi.getFirst(),chi.getSecond())));
    	operations.add(new Delete(CHOICE_FOR_CHI));
    	operations.add(new SetVisibility(T+chi.getFirst()));
    	operations.add(new SetVisibility(T+chi.getSecond()));
    	
    	return operations;
    }
    
    /**
     * Get a correct version of lastMove for Chi
     * @param state
     * @return lastMove of Chi
     */
	public List<Operation> checkIfChiMove(MahjongState state){
		check(state.isChi());
		
		Position turnOfPosition = state.getTurn();
		Chi chi = state.getChi().get();
		List<Optional<Tile>> chiTiles = ImmutableList.<Optional<Tile>>of(state.getTiles().get(chi.getFirst().intValue()), state.getTiles().get(chi.getSecond().intValue()));
		
		int specialIndex = state.getSpecialTile();
		Optional<Tile> specialTile = state.getTiles().get(specialIndex);
				
		Cast castTile = state.getCast().get();
		Optional<Tile> castValue = state.getTiles().get(castTile.getValue().intValue());
		
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Delete(IS_CHI));
		operations.add(new Delete(IS_CHI_STATUS));
		operations.add(new SetTurn(state.getPlayerId(turnOfPosition)));
		if(canFormChi(castValue,specialTile,chiTiles))
		{
			operations.add(new Set(turnOfPosition.name(),substract(state.getOneOfFourTile(turnOfPosition),chi)));
			operations.add(new Set(turnOfPosition.nameOfChi(),concat(state.getOneOfFourChi(turnOfPosition),ImmutableList.<Integer>of(chi.getFirst(),chi.getSecond(),castTile.getValue()))));
			operations.add(new SetVisibility(T + chi.getFirst()));
			operations.add(new SetVisibility(T + chi.getSecond()));
		}
		return operations;
	}
    
    /**
     * Check whether val is true
     * @param val
     * @param debugArguments
     */
    private void check(boolean val, Object... debugArguments) {
        if (!val) {
          throw new RuntimeException("We have a hacker! debugArguments="
              + Arrays.toString(debugArguments));
        }
      }
}


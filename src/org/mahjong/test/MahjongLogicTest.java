package org.mahjong.test;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.mahjong.client.GameApi.Delete;
import org.mahjong.client.GameApi.Operation;
import org.mahjong.client.GameApi.Set;
import org.mahjong.client.GameApi.SetVisibility;
import org.mahjong.client.GameApi.VerifyMove;
import org.mahjong.client.GameApi.VerifyMoveDone;
import org.mahjong.client.GameApi.SetTurn;
import org.mahjong.client.GameApi.EndGame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mahjong.client.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
@RunWith(JUnit4.class)
public class MahjongLogicTest{

	MahjongLogic mahjongLogic = new MahjongLogic();

	private void assertMoveOK(VerifyMove verifyMove){
		mahjongLogic.checkMoveIsLegal(verifyMove);
	}

	private void assertHacker(VerifyMove verifyMove){
		VerifyMoveDone verifyDone = mahjongLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),verifyDone.getHackerPlayerId());
	}

	private static final String PLAYER_ID = "playerId";
	
	private static final String E = "E";
	private static final String CHI_BY_E = "chiByE";
	private static final String PENG_BY_E = "pengByE"; 
	private static final String S = "S";
	private static final String W = "W";
	private static final String N = "N";
	
	private static final String CHI = "chi";
	private static final String PENG = "peng";
	private static final String CAST = "cast";
	
	private static final String YES = "yes";
	
	private static final String WALL_EAST = "wallEast";
	private static final String WALL_SOUTH = "wallSouth";
	private static final String WALL_WEST = "wallWest";
	private static final String WALL_NORTH = "wallNorth";
	
	private static final String WALL_INDEX = "wallIndex";
	
	private static final String T = "T";
	
	private static final String SPECIAL_TILE = "specialTile";
	
	private static final String NEXT_TURN_OF_CAST_PLAYER = "nextTurnOfCastPlayer";
	private static final String PLAYER_WHO_ISSUE_CAST = "playerWhoIssueCast";
	

	
	private static final String IS_AUTO_PENG_CHECK = "isAutoPengCheck";
	private static final String IS_AUTO_HU_CHECK = "isAutoHuCheck";
	
	private static final String CAN_FORM_HU = "canFormHu";
	private static final String CAN_FORM_PENG = "canFormPeng";
	
	private static final String IS_FETCH = "isFetch";
	private static final String IS_CHI = "isChi";
	private static final String IS_PENG = "isPeng";
	private static final String IS_HU = "isHu";
	
	private final int eId = 0;
	private final int nId = 1;
	private final int wId = 2;
	private final int sId = 3;
	private final List<Integer> visibleToE = ImmutableList.of(eId);
	private final List<Integer> visibleToW = ImmutableList.of(wId);
	private final List<Integer> visibleToS = ImmutableList.of(sId);
	private final List<Integer> visibleToN = ImmutableList.of(nId);
	private final Map<String,Object> eInfo = ImmutableMap.<String,Object>of(PLAYER_ID,eId);
	private final Map<String,Object> wInfo = ImmutableMap.<String,Object>of(PLAYER_ID,wId);
	private final Map<String,Object> sInfo = ImmutableMap.<String,Object>of(PLAYER_ID,sId);
	private final Map<String,Object> nInfo = ImmutableMap.<String,Object>of(PLAYER_ID,nId);
	private final List<Map<String,Object>> playersInfo = ImmutableList.of(eInfo,nInfo,wInfo,sInfo);

	private final Map<String,Object> emptyState = ImmutableMap.<String,Object>of();

	
	private final Map<String,Object> gameBeginState = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(54,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
			.build();
	
	private final List<Operation> fetchByEast = ImmutableList.<Operation>of(
			new SetTurn(eId),
			new Set(IS_FETCH, YES),
			new Set(E, concat(getIndicesInRange(54, 54), getIndicesInRange(0, 13))),
			new Set(WALL_NORTH, getIndicesInRange(55,67)),
			new SetVisibility(T + 54, visibleToE),
			new Set(WALL_INDEX, 1),
			new Delete(IS_FETCH)); 
	
	private final List<Operation> fetchByWest = ImmutableList.<Operation>of(
			new SetTurn(wId),
			new Set(IS_FETCH, YES),
			new Set(E, concat(getIndicesInRange(0, 13),getIndicesInRange(54, 54))),
			new Set(WALL_NORTH, getIndicesInRange(55,67)),
			new SetVisibility(T + 54, visibleToE),
			new Set(WALL_INDEX, 1),
			new Delete(IS_FETCH)); 
	
	
	
	private List<Integer> getIndicesInRange(int from, int to){
		return mahjongLogic.getIndicesInRange(from, to);
	}
	
	private <T> List<T> concat(List<T> a, List<T> b){
		return mahjongLogic.concat(a,b);
	}
	
	private VerifyMove move(int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
	    return new VerifyMove(playersInfo, emptyState, lastState, lastMove, lastMovePlayerId,ImmutableMap.<Integer, Integer>of());
    }
	
	@Test
	public void testNormalFetchByEast() {
		assertMoveOK(move(eId, gameBeginState, fetchByEast));
	}

	@Test
	public void testWrongFetchByEast() {
		assertHacker(move(sId, gameBeginState, fetchByEast));
		assertHacker(move(eId, gameBeginState, fetchByWest));
	}
	
	@Test
	public void testGetIndicesInRange() {
		assertEquals(ImmutableList.of(3, 4), mahjongLogic.getIndicesInRange(3, 4));
	}
	
	private final Map<String, Object> stateBeforeChiDeclareByEast = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(T + 53, "Cir1")
		    .put(CAST, new Integer(57))
		    .build();
			
	private final List<Operation> chiDeclareByEast = ImmutableList.<Operation>of(
			new SetTurn(eId),
			new Set(IS_CHI, YES),
			new Set(CHI, ImmutableList.of(5, 7)),
			new SetVisibility(T+5),
			new SetVisibility(T+7));
			
	private final List<Operation> chiDeclareByEastWithWrongTiles = ImmutableList.<Operation>of(
			new SetTurn(eId),
			new Set(IS_CHI, YES),
			new Set(CHI, ImmutableList.of(17,18)),
			new SetVisibility(T+17),
			new SetVisibility(T+18));
	
	private final List<Operation> chiDeclareByEastWithoutFlag = ImmutableList.<Operation>of(
			new SetTurn(eId),
			new Set(CHI, ImmutableList.of(4,5)),
			new SetVisibility(T+4),
			new SetVisibility(T+5));
	
	@Test
	public void testNormalChiDeclareByEast() {
		assertMoveOK(move(eId, stateBeforeChiDeclareByEast, chiDeclareByEast));
	}

	@Test
	public void testWrongChiDeclareByEast() {
		assertHacker(move(sId, stateBeforeChiDeclareByEast, chiDeclareByEast));
		assertHacker(move(wId, stateBeforeChiDeclareByEast, chiDeclareByEast));
		assertHacker(move(eId, stateBeforeChiDeclareByEast, chiDeclareByEastWithWrongTiles));
		assertHacker(move(eId, stateBeforeChiDeclareByEast, chiDeclareByEastWithoutFlag));
	}
	
	private final Map<String, Object> stateBeforeChiByEast1 = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(T+53, "Cir1")
		    .put(IS_CHI, YES)
		    .put(CHI, ImmutableList.of(4,5))
		    .put(T+57, "Bam1")
		    .put(T+4, "Bam2")
		    .put(T+5, "Bam3")
		    .build();
	
	private final Map<String, Object> stateBeforeChiByEast2 = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(IS_CHI, YES)
		    .put(T+53, "Cir1")
		    .put(CHI, ImmutableList.of(4,5))
		    .put(T+57, "Bam1")
		    .put(T+4, "Bam3")
		    .put(T+5, "Bam5")
		    .build();
	
	private final List<Operation> validChiByEast = ImmutableList.<Operation>of(
			new Delete(IS_CHI),
			new SetTurn(eId),
			new Set(E, ImmutableList.of(0,1,2,3,6,7,8,9,10,11,12,13)),
			new Set(CHI_BY_E, ImmutableList.of(4,5,57)),
			new SetVisibility(T+4),
			new SetVisibility(T+5));
	
	private final List<Operation> inValidChiByEast = ImmutableList.<Operation>of(
			new Delete(IS_CHI),
			new SetTurn(eId));
	
	private final List<Operation> wrongChiByEast = ImmutableList.<Operation>of(
			new Delete(IS_CHI),
			new SetTurn(eId),
			new Set(E, ImmutableList.of(0,1,2,3,4,5,6,7,8,9,10,13)),
			new Set(CHI_BY_E, ImmutableList.of(4,5,57)),
			new SetVisibility(T+4),
			new SetVisibility(T+5));
	
	@Test
	public void testNormalChiByEast() {
		assertMoveOK(move(eId, stateBeforeChiByEast1, validChiByEast));
		assertMoveOK(move(eId, stateBeforeChiByEast2, inValidChiByEast));
	}

	@Test
	public void testWrongChiByEast() {
		assertHacker(move(eId, stateBeforeChiByEast1, inValidChiByEast));
		assertHacker(move(eId, stateBeforeChiByEast2, validChiByEast));
		assertHacker(move(sId, stateBeforeChiByEast1, validChiByEast));
		assertHacker(move(wId, stateBeforeChiByEast2, inValidChiByEast));
		assertHacker(move(eId, stateBeforeChiByEast1, wrongChiByEast));
		assertHacker(move(eId, stateBeforeChiByEast2, wrongChiByEast));
		
	}	
	
	//Peng
	private Map<String, Object> stateBeforeAutoPengCheck = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(T+57, "Bam1")
		    .put(T+53, "Cir1")
		    .put(NEXT_TURN_OF_CAST_PLAYER, wId)
		    .put(PLAYER_WHO_ISSUE_CAST, nId)
		    .build();
	
	
    private Map<String, Object> stateBeforePengDeclare = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(T+57, "Bam1")
		    .put(T+53, "Cir1")
		    .build();
    
    private Map<String, Object> stateBeforePeng1 = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(IS_PENG, YES)
		    .put(T+57, "Bam1")
		    .put(T+53, "Cir1")
		    .put(PENG, ImmutableList.of(4,5))
		    .put(T+4, "Bam1")
		    .put(T+5, "Bam1")
		    .build();
    
    private Map<String, Object> stateBeforePeng2 = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(60,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
		    .put(CAST, new Integer(57))
		    .put(IS_PENG, YES)
		    .put(T+57, "Bam1")
		    .put(T+53, "Cir1")
		    .put(PENG, ImmutableList.of(4,5))
		    .put(T+4, "Bam2")
		    .put(T+5, "Bam2")
		    .build();
    
    private List<Operation> pengAutoCheckOfEast = ImmutableList.<Operation>of(
    		new Set(IS_AUTO_PENG_CHECK, YES),
    		new Set(CAN_FORM_PENG, 1),
    		new SetTurn(eId),
    		new Delete(CAN_FORM_PENG),
    		new Delete(IS_AUTO_PENG_CHECK));
    
    private List<Operation> pengDeclareByEast = ImmutableList.<Operation>of(
    		new SetTurn(eId),
        	new Set(IS_PENG,YES),
        	new Set(PENG,ImmutableList.of(4,5)),
        	new SetVisibility(T+4),
        	new SetVisibility(T+5));
    
    private List<Operation> validPengByEast = ImmutableList.<Operation>of(
    		new Delete(IS_PENG),
    		new SetTurn(eId),
    		new Set(E, ImmutableList.of(0,1,2,3,6,7,8,9,10,11,12,13)),
    		new Set(PENG_BY_E, ImmutableList.of(4,5,57)),
    		new SetVisibility(T+4),
    		new SetVisibility(T+5),
    		new Delete(NEXT_TURN_OF_CAST_PLAYER),
    		new Delete(PLAYER_WHO_ISSUE_CAST));
    		
    
    private List<Operation> inValidPengByEast = ImmutableList.<Operation>of(
    		new Delete(IS_PENG),
    		new SetTurn(eId));
    
    private List<Operation> wrongPengByEast = ImmutableList.<Operation>of(
    		new Delete(IS_PENG),
    		new SetTurn(eId),
    		new Set(E, ImmutableList.of(0,1,2,3,4,5,6,7,8,9,10,11)),
    		new Set(PENG_BY_E, ImmutableList.of(12,13,57)),
    		new SetVisibility(T+12),
    		new SetVisibility(T+13),
    		new Delete(NEXT_TURN_OF_CAST_PLAYER),
    		new Delete(PLAYER_WHO_ISSUE_CAST));
    
	@Test
	public void testNormalPengByEast() {
		assertMoveOK(move(eId, stateBeforeAutoPengCheck, pengAutoCheckOfEast));
		assertMoveOK(move(eId, stateBeforePengDeclare, pengDeclareByEast));
		assertMoveOK(move(eId, stateBeforePeng1,  validPengByEast));
		assertMoveOK(move(eId, stateBeforePeng2, inValidPengByEast));
	}

	@Test
	public void testWrongPengByEast() {
		assertHacker(move(eId, stateBeforeAutoPengCheck, pengDeclareByEast));
		assertHacker(move(eId, stateBeforeAutoPengCheck, validPengByEast));
		assertHacker(move(eId, stateBeforePeng1, pengAutoCheckOfEast));
		assertHacker(move(eId, stateBeforePeng1, pengDeclareByEast));
		assertHacker(move(eId, stateBeforePeng2, pengAutoCheckOfEast));
		assertHacker(move(eId, stateBeforePeng2, pengDeclareByEast));
		assertHacker(move(sId, stateBeforeAutoPengCheck, pengAutoCheckOfEast));
		assertHacker(move(wId, stateBeforePengDeclare, pengDeclareByEast));
		assertHacker(move(nId, stateBeforePeng1,  validPengByEast));
		assertHacker(move(sId, stateBeforePeng2, inValidPengByEast));
		assertHacker(move(eId, stateBeforePeng2, inValidPengByEast));
		assertHacker(move(eId, stateBeforePeng1,  wrongPengByEast));
		assertHacker(move(eId, stateBeforePeng2,  wrongPengByEast));
	}	
    
	
    //Hu
		private Map<String, Object> stateBeforeAutoHuCheck = ImmutableMap.<String, Object>builder()
				.put(WALL_EAST, ImmutableList.of())
				.put(WALL_NORTH, getIndicesInRange(60,67))
				.put(WALL_WEST, getIndicesInRange(68,101))
				.put(WALL_SOUTH, getIndicesInRange(102,135))
				.put(E, getIndicesInRange(0, 13))
			    .put(N, getIndicesInRange(14, 26))
			    .put(W, getIndicesInRange(27, 39))
			    .put(S, getIndicesInRange(40, 52))
			    .put(SPECIAL_TILE, new Integer(53))
			    .put(CAST, new Integer(57))
			    .put(T+57, "Bam1")
			    .put(T+53, "Cir1")
			    .put(NEXT_TURN_OF_CAST_PLAYER, wId)
			    .put(PLAYER_WHO_ISSUE_CAST, nId)
			    .build();
		
	    
	    private Map<String, Object> stateBeforeHu1 = ImmutableMap.<String, Object>builder()
				.put(WALL_EAST, ImmutableList.of())
				.put(WALL_NORTH, getIndicesInRange(60,67))
				.put(WALL_WEST, getIndicesInRange(68,101))
				.put(WALL_SOUTH, getIndicesInRange(102,135))
				.put(E, getIndicesInRange(0, 13))
			    .put(N, getIndicesInRange(14, 26))
			    .put(W, getIndicesInRange(27, 39))
			    .put(S, getIndicesInRange(40, 52))
			    .put(SPECIAL_TILE, new Integer(53))
			    .put(CAST, new Integer(57))
			    .put(IS_HU, YES)
			    .put(T+57, "Bam1")
			    .put(T+53, "Cir1")
			    .put(T+0, "Bam2")
			    .put(T+1, "Bam3")
			    .put(T+2, "Cir4")
			    .put(T+3, "Cir4")
			    .put(T+4, "Eas0")
			    .put(T+5, "Eas0")
			    .put(T+6, "Eas0")
			    .put(T+7, "Nor0")
			    .put(T+8, "Nor0")
			    .put(T+9, "Nor0")
			    .put(T+10, "Bam1")
			    .put(T+11, "Cha1")
			    .put(T+12, "Cha2")
			    .put(T+13, "Cha3")
			    .build();
	    
	    private Map<String, Object> stateBeforeHu2 = ImmutableMap.<String, Object>builder()
				.put(WALL_EAST, ImmutableList.of())
				.put(WALL_NORTH, getIndicesInRange(60,67))
				.put(WALL_WEST, getIndicesInRange(68,101))
				.put(WALL_SOUTH, getIndicesInRange(102,135))
				.put(E, getIndicesInRange(0, 13))
			    .put(N, getIndicesInRange(14, 26))
			    .put(W, getIndicesInRange(27, 39))
			    .put(S, getIndicesInRange(40, 52))
			    .put(SPECIAL_TILE, new Integer(53))
			    .put(CAST, new Integer(57))
			    .put(IS_HU, YES)
			    .put(T+57, "Bam1")
			    .put(T+53, "Cir1")
			    .put(T+0, "Bam2")
			    .put(T+1, "Bam3")
			    .put(T+2, "Cir4")
			    .put(T+3, "Cir4")
			    .put(T+4, "Eas0")
			    .put(T+5, "Eas0")
			    .put(T+6, "Eas0")
			    .put(T+7, "Nor0")
			    .put(T+8, "Nor0")
			    .put(T+9, "Nor0")
			    .put(T+10, "Bam1")
			    .put(T+11, "Cha1")
			    .put(T+12, "Cha4")
			    .put(T+13, "Cha6")
			    .build();
	    
	    private List<Operation> huAutoCheckOfEast = ImmutableList.<Operation>of(
	    		new Set(IS_AUTO_HU_CHECK, YES),
	    		new Set(CAN_FORM_HU, 1),
	    		new SetTurn(eId),
	    		new Delete(CAN_FORM_HU),
	    		new Delete(IS_AUTO_HU_CHECK));
	    
	    private List<Operation> validHuByEast = ImmutableList.<Operation>of(
	    		new Delete(IS_HU),
	    		new SetTurn(eId),
	    		new EndGame(eId));
	    		
	    private List<Operation> inValidHuByEast = ImmutableList.<Operation>of(
	    		new Delete(IS_HU),
	    		new SetTurn(eId),
	    		new EndGame(eId-3));
	    
		@Test
		public void testNormalHuByEast() {
			assertMoveOK(move(eId, stateBeforeAutoHuCheck, huAutoCheckOfEast));
			assertMoveOK(move(eId, stateBeforeHu1,  validHuByEast));
			assertMoveOK(move(eId, stateBeforeHu2, inValidHuByEast));
		}

		@Test
		public void testWrongHuByEast() {
			assertHacker(move(eId, stateBeforeAutoHuCheck, validHuByEast));
			assertHacker(move(eId, stateBeforeAutoHuCheck, inValidHuByEast));
			assertHacker(move(eId, stateBeforeHu1, huAutoCheckOfEast));
			assertHacker(move(eId, stateBeforeHu1, inValidHuByEast));
			assertHacker(move(eId, stateBeforeHu2, validHuByEast));
			assertHacker(move(eId, stateBeforeHu2, huAutoCheckOfEast));
			assertHacker(move(sId, stateBeforeAutoHuCheck, huAutoCheckOfEast));
			assertHacker(move(wId, stateBeforeHu1,  validHuByEast));
			assertHacker(move(nId, stateBeforeHu2, inValidHuByEast));
		}	
    
    //cast
    private Map<String, Object> stateBeforeCast = ImmutableMap.<String, Object>builder()
			.put(WALL_EAST, ImmutableList.of())
			.put(WALL_NORTH, getIndicesInRange(54,67))
			.put(WALL_WEST, getIndicesInRange(68,101))
			.put(WALL_SOUTH, getIndicesInRange(102,135))
			.put(E, getIndicesInRange(0, 13))
		    .put(N, getIndicesInRange(14, 26))
		    .put(W, getIndicesInRange(27, 39))
		    .put(S, getIndicesInRange(40, 52))
		    .put(SPECIAL_TILE, new Integer(53))
			.build();
    
    private List<Operation> castByEast = ImmutableList.<Operation>of(
    		new SetTurn(nId),
    		new Set(CAST, 13),
    		new Set(E,getIndicesInRange(0,12)),
    		new Set(NEXT_TURN_OF_CAST_PLAYER, nId),
    		new Set(PLAYER_WHO_ISSUE_CAST, eId),
    		new SetVisibility(T+13));
    
	@Test
	public void testNormalCastByEast() {
		assertMoveOK(move(eId, stateBeforeCast, castByEast));
	}

	@Test
	public void testWrongCastByEast() {
		assertHacker(move(sId, stateBeforeCast, castByEast));
		assertHacker(move(nId, stateBeforeCast, castByEast));
	}	
    
}
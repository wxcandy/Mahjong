package org.mahjong.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mahjong.client.GameApi;
import org.mahjong.client.MahjongPresenter.MahjongMessage;
import org.mahjong.client.MahjongPresenter.View;
import org.mahjong.client.GameApi.Container;
import org.mahjong.client.GameApi.Operation;
import org.mahjong.client.GameApi.SetTurn;
import org.mahjong.client.GameApi.UpdateUI;
import org.mahjong.client.GameApi.Set;
import org.mahjong.client.Cast;
import org.mahjong.client.Chi;
import org.mahjong.client.MahjongLogic;
import org.mahjong.client.MahjongPresenter;
import org.mahjong.client.Peng;
import org.mahjong.client.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mahjong.client.Tile;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(JUnit4.class)
public class MahjongPresenterTest {
	
	private MahjongPresenter mahjongPresenter;
	private final MahjongLogic mahjongLogic = new MahjongLogic();
	private View mockView;
	private Container mockContainer;
	
    private static final String PLAYER_ID = "playerId";
	
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
	
    private static final String IS_HU_STATUS = "isHuStatus";
	private static final String IS_PENG_STATUS = "isPengStatus";
	private static final String IS_CHI_STATUS = "isChiStatus";
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
	
	private final int eId = 0;
	private final int nId = 1;
	private final int wId = 2;
	private final int sId = 3;
    private final int viewerId = GameApi.VIEWER_ID;
	private final ImmutableList<Integer> playerIds = ImmutableList.of(eId, nId, wId, sId);
	private final Map<String,Object> eInfo = ImmutableMap.<String,Object>of(PLAYER_ID,eId);
	private final Map<String,Object> wInfo = ImmutableMap.<String,Object>of(PLAYER_ID,wId);
	private final Map<String,Object> sInfo = ImmutableMap.<String,Object>of(PLAYER_ID,sId);
	private final Map<String,Object> nInfo = ImmutableMap.<String,Object>of(PLAYER_ID,nId);
	private final List<Map<String,Object>> playersInfo = ImmutableList.of(eInfo,nInfo,wInfo,sInfo);
	
	private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
	private final ImmutableMap<String, Object> initialState = createInitialState();
	
	/**
	 * The state before fetch a tile
	 */
	private final ImmutableMap<String, Object> stateBeforeFetch = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(53, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.<Cast>fromNullable(null),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before cast a tile
	 */
	private final ImmutableMap<String, Object> stateBeforeCast = createState(
		    14,
			13,
			13,
			13,
			53,
			1,
			mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.<Cast>fromNullable(null),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
			
	/**
	 * The state before the auto check of hu by fetch 
	 */
	private final ImmutableMap<String, Object> stateBeforeAutoHuCheckByFetch = createState(
			14,
			13,
			13,
			13,
			53,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.<Cast>fromNullable(null),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before the auto check of hu by cast
	 */
	private final ImmutableMap<String, Object> stateBeforeAutoHuCheckByCast = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before hu choice wait
	 */
	private final ImmutableMap<String, Object> stateBeforeWaitHuChoice = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			 1,
			-1,
			-1,
			-1,
			-1,
			-1);
			
	/**
	 * The state before hu declare
	 */
	private final ImmutableMap<String, Object> stateBeforeHuDeclare = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			 1,
			-1,
			-1);
			
	/** 
	 * The state before hu check
	 */
	private final ImmutableMap<String, Object> stateBeforeHuCheck = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before auto check of peng
	 */
	private final ImmutableMap<String, Object> stateBeforeAutoPengCheck = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before wait peng choice
	 */
	private final ImmutableMap<String, Object> stateBeforeWaitPengChoice = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			-1,
			 1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before peng declare
	 */
	private final ImmutableMap<String, Object> stateBeforePengDeclare = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			 1,
			-1);
	
	/**
	 * The state before peng check
	 */
	private final ImmutableMap<String, Object> stateBeforePengCheck = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.of(new Peng(ImmutableList.of(7,8))),
			-1,
			-1,
			false,
			false,
			true,
			false,
			false,
			true,
			false,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before auto check of chi
	 */
	private final ImmutableMap<String, Object> stateBeforeAutoChiCheck = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before wait chi choice
	 */
	private final ImmutableMap<String, Object> stateBeforeWaitChiChoice = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			2,
			1,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			-1,
			-1,
			 1,
			-1,
			-1,
			-1);
	
	/**
	 * The state before chi declare
	 */
	private final ImmutableMap<String, Object> stateBeforeChiDeclare = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.<Chi>fromNullable(null),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			 1);
		
	/**
	 * The state before chi check
	 */
	private final ImmutableMap<String, Object> stateBeforeChiCheck = createState(
			13,
			13,
			13,
			13,
			52,
			1,
		    mahjongLogic.getIndicesInRange(0, -1),
			mahjongLogic.getIndicesInRange(54, 67),
			mahjongLogic.getIndicesInRange(68, 101),
			mahjongLogic.getIndicesInRange(102, 135),
			Optional.of(new Cast(53)),
			Optional.of(new Chi(ImmutableList.of(7,8))),
			Optional.<Peng>fromNullable(null),
			-1,
			-1,
			false,
			true,
			false,
			false,
			false,
			false,
			true,
			false,
			false,
			false,
			-1,
			-1,
			-1,
			-1,
			-1,
			-1);
	
	/**
	 * Create the initial State
	 * @return
	 */
	private ImmutableMap<String, Object> createInitialState()
		{
		   Map<String, Object> state = Maps.newHashMap();
		   state.put(WALL_EAST, mahjongLogic.getIndicesInRange(0, 33));
		   state.put(WALL_NORTH, mahjongLogic.getIndicesInRange(34, 67));
		   state.put(WALL_WEST, mahjongLogic.getIndicesInRange(68, 101));
		   state.put(WALL_SOUTH, mahjongLogic.getIndicesInRange(102, 135));
		   state.put(INI_CONTINUE, YES);
		   for(int i=0;i<136;i++) {
			   state.put(T+i, mahjongLogic.tileIdToString(i));
		   }
		   return ImmutableMap.copyOf(state);
		}
			
	/**
	 * Create a normal state
	 */
	private ImmutableMap<String, Object> createState(
			int numberOfEast,
			int numberOfNorth,
			int numberOfWest,
			int numberOfSouth,
			int indexOfSpecialTile,
			int wallIndex,
			List<Integer> wallEast,
			List<Integer> wallNorth,
			List<Integer> wallWest,
			List<Integer> wallSouth,
			Optional<Cast> cast,
			Optional<Chi> chi,
			Optional<Peng> peng,
			int nextTurnOfCastPlayer,
			int playerWhoIssueCast,
			boolean isFetch,
			boolean isChi,
			boolean isPeng,
			boolean isHu,
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
			)
		{
			Map<String, Object> state = Maps.newHashMap();
			state.put(E, mahjongLogic.getIndicesInRange(0, numberOfEast-1));
			state.put(N, mahjongLogic.getIndicesInRange(numberOfEast, numberOfEast+numberOfNorth-1));
			state.put(W, mahjongLogic.getIndicesInRange(numberOfEast+numberOfNorth, numberOfEast+numberOfNorth+numberOfWest-1));
			state.put(S, mahjongLogic.getIndicesInRange(numberOfEast+numberOfNorth+numberOfWest, numberOfEast+numberOfNorth+numberOfWest+numberOfSouth-1));
			state.put(SPECIAL_TILE, indexOfSpecialTile);
			state.put(CHI_BY_E, ImmutableList.of());
			state.put(PENG_BY_E, ImmutableList.of());
			state.put(GANG_BY_E, ImmutableList.of());
			state.put(CHI_BY_N, ImmutableList.of());
			state.put(PENG_BY_N, ImmutableList.of());
			state.put(GANG_BY_N, ImmutableList.of());
			state.put(CHI_BY_W, ImmutableList.of());
			state.put(PENG_BY_W, ImmutableList.of());
			state.put(GANG_BY_W, ImmutableList.of());
			state.put(CHI_BY_S, ImmutableList.of());
			state.put(PENG_BY_S, ImmutableList.of());
			state.put(GANG_BY_S, ImmutableList.of());
			state.put(WALL_INDEX, wallIndex);
			state.put(WALL_EAST, wallEast);
			state.put(WALL_NORTH, wallNorth);
			state.put(WALL_WEST, wallWest);
			state.put(WALL_SOUTH, wallSouth);
			state.put(MIDDLE_TILES, ImmutableList.of());
			int i = 0;
			for(Tile tile : getTiles(0,136)) {
				state.put(T + (i++),
					tile.getValue());
			}
			if(isFetch) {
				state.put(IS_FETCH, YES);
			}
			if(cast.isPresent()) {
				state.put(CAST, cast.get().getValue());
			}
			if(chi.isPresent()) {
				state.put(CHI, ImmutableList.of(chi.get().getFirst(),chi.get().getSecond()));
			}
			if(peng.isPresent()) {
				state.put(PENG, ImmutableList.of(peng.get().getFirst(),peng.get().getSecond()));
			}
			if(nextTurnOfCastPlayer >= 0 )
			state.put(NEXT_TURN_OF_CAST_PLAYER, nextTurnOfCastPlayer);
			if(playerWhoIssueCast >= 0)
			state.put(PLAYER_WHO_ISSUE_CAST, playerWhoIssueCast);
			if(isChi){
				state.put(IS_CHI, YES);
			}
			if(isPeng){
				state.put(IS_PENG, YES);
			}
			if(isHu){
				state.put(IS_HU, YES);
			}
			if(isHuStatus){
				state.put(IS_HU_STATUS, YES);
			}
			if(isPengStatus){
				state.put(IS_PENG_STATUS, YES);
			}
			if(isChiStatus){
				state.put(IS_CHI_STATUS, YES);
			}
			if(isHuCheckStatus){
				state.put(IS_HU_CHECK_STATUS, YES);
			}
			if(isPengCheckStatus){
				state.put(IS_PENG_CHECK_STATUS, YES);
			}
			if(isChiCheckStatus){
				state.put(IS_CHI_CHECK_STATUS, YES);
			}
			
			if(isHuIsAllowed == 1){
				state.put(IS_HU_IS_ALLOWED, true);
			}else if(isHuIsAllowed == 0){
				state.put(IS_HU_IS_ALLOWED, false);
			}
			
			if(isPengIsAllowed == 1){
				state.put(IS_PENG_IS_ALLOWED, true);
			}else if(isPengIsAllowed == 0){
				state.put(IS_PENG_IS_ALLOWED, false);
			}
			
			if(isChiIsAllowed == 1){
				state.put(IS_CHI_IS_ALLOWED, true);
			}else if(isChiIsAllowed == 0){
				state.put(IS_CHI_IS_ALLOWED, false);
			}
			
			if(choiceForHu == 1){
				state.put(CHOICE_FOR_HU, true);
			}else if(choiceForHu == 0){
				state.put(CHOICE_FOR_HU, false);
			}
			
			if(choiceForPeng == 1){
				state.put(CHOICE_FOR_PENG, true);
			}else if(choiceForPeng == 0){
				state.put(CHOICE_FOR_PENG, false);
			}
			
			if(choiceForChi == 1){
				state.put(CHOICE_FOR_CHI, true);
			}else if(choiceForChi == 0){
				state.put(CHOICE_FOR_CHI, false);
			}
			
			return ImmutableMap.copyOf(state);
		}
	
	private List<Tile> getTiles(int fromInclusive, int toExclusive) {
		List<Tile> tiles = Lists.newArrayList();
		String tileStr = null;
		String pre = null;
		String post = null;
		int domain = -1;
		for(int i=fromInclusive; i<toExclusive;i++) {
			tileStr = mahjongLogic.tileIdToString(i);
			pre = tileStr.substring(0,3);
			post = tileStr.substring(3);
			if(pre.equals("Bam")) domain = 0;
			else if(pre.equals("Cha")) domain = 1;
			else if(pre.equals("Cir")) domain = 2;
			else domain = 3;
			tiles.add(new Tile(domain, pre, post));
		}
		return tiles;
	}
	
	@Before
	public void runBefore() {
		mockView = Mockito.mock(View.class);
		mockContainer = Mockito.mock(Container.class);
		mahjongPresenter = new MahjongPresenter(mockView, mockContainer);
		verify(mockView).setPresenter(mahjongPresenter);
	}
	
	@After
	public void runAfter() {
		verifyNoMoreInteractions(mockContainer);
		verifyNoMoreInteractions(mockView);
	}
	
	/**
	 * Test for empty state
	 */
	@Test
	public void testEmptyStateForE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, emptyState));
		verify(mockContainer).sendMakeMove(mahjongLogic.getInitialMoveOne(playerIds));
	}
	
	@Test
	public void testEmptyStateForN() {
		mahjongPresenter.updateUI(createUpdateUI(nId, eId, emptyState));
	}
	
	@Test
	public void testEmptyStateForW() {
		mahjongPresenter.updateUI(createUpdateUI(wId, eId, emptyState));
	}
	
	@Test
	public void testEmptyStateForS() {
		mahjongPresenter.updateUI(createUpdateUI(sId, eId, emptyState));
	}
	
	@Test
	public void testEmptyStateForViewer() {
		mahjongPresenter.updateUI(createUpdateUI(viewerId, eId, emptyState));
	}
	
	/**
	 * Test for initial state
	 */
	@Test
	public void testInitialStateForE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, initialState));
		verify(mockView).setPlayerState(
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   mahjongLogic.getIndicesInRange(0, 33),
				   mahjongLogic.getIndicesInRange(34, 67),
				   mahjongLogic.getIndicesInRange(68, 101),
				   mahjongLogic.getIndicesInRange(102, 135),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   null,
				   MahjongMessage.INI_CONTINUE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.getInitialMoveTwo(playerIds));
	}
	
	@Test
	public void testInitialStateForN() {
		mahjongPresenter.updateUI(createUpdateUI(nId, eId, initialState));
		verify(mockView).setPlayerState(
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   mahjongLogic.getIndicesInRange(0, 33),
				   mahjongLogic.getIndicesInRange(34, 67),
				   mahjongLogic.getIndicesInRange(68, 101),
				   mahjongLogic.getIndicesInRange(102, 135),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   null,
				   MahjongMessage.INI_CONTINUE_);
	}
	
	@Test
	public void testInitialStateForW() {
		mahjongPresenter.updateUI(createUpdateUI(wId, eId, initialState));
		verify(mockView).setPlayerState(
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   mahjongLogic.getIndicesInRange(0, 33),
				   mahjongLogic.getIndicesInRange(34, 67),
				   mahjongLogic.getIndicesInRange(68, 101),
				   mahjongLogic.getIndicesInRange(102, 135),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   null,
				   MahjongMessage.INI_CONTINUE_);
	}
	
	@Test
	public void testInitialStateForS() {
		mahjongPresenter.updateUI(createUpdateUI(sId, eId, initialState));
		verify(mockView).setPlayerState(
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   0,
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   mahjongLogic.getIndicesInRange(0, 33),
				   mahjongLogic.getIndicesInRange(34, 67),
				   mahjongLogic.getIndicesInRange(68, 101),
				   mahjongLogic.getIndicesInRange(102, 135),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   null,
				   MahjongMessage.INI_CONTINUE_);
	}
	
	@Test
	public void testInitialStateForViewer() {
		mahjongPresenter.updateUI(createUpdateUI(GameApi.VIEWER_ID, eId, initialState));
		verify(mockView).setViewerState(
				   0,
				   0,
				   0,
				   0,
				   mahjongLogic.getIndicesInRange(0, 33),
				   mahjongLogic.getIndicesInRange(34, 67),
				   mahjongLogic.getIndicesInRange(68, 101),
				   mahjongLogic.getIndicesInRange(102, 135),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   ImmutableList.<Tile>of(),
				   null,
				   ImmutableList.<Tile>of(),
				   MahjongMessage.INVISIBLE_);
	}
	
	/**
	 * Test for the state before fetch
	 */
	@Test
	public void testFetchStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeFetch));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(53, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.declareFetchMove(mahjongPresenter.getMahjongState()));
	}
	
	@Test
	public void testFetchStateForETurnOfN() {
	    mahjongPresenter.updateUI(createUpdateUI(nId, eId, stateBeforeFetch));
	    verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(53, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(13,25)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
	}
	
	@Test
	public void testFetchStateForETurnOfViewer() {
		mahjongPresenter.updateUI(createUpdateUI(GameApi.VIEWER_ID, eId, stateBeforeFetch));
	    verify(mockView).setViewerState(
	    		13,
	    		13,
	    		13,
				13,
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(53, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				ImmutableList.<Tile>of(),
				MahjongMessage.INVISIBLE_);
	}
	
	/**
	 * Test the state before cast
	 */
	@Test
	public void testCastStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeCast));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,13)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(53).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockView).chooseCastTile(ImmutableList.<Tile>of(), ImmutableList.<Integer>of());
	}
	
	@Test
	public void testCastStateForETurnOfN() {
		mahjongPresenter.updateUI(createUpdateUI(nId, eId, stateBeforeCast));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				14,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(14,26)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(53).get(),
				MahjongMessage.NO_MOVE_);
	}
	
	@Test
	public void testCastStateForETurnOfViewer() {
		mahjongPresenter.updateUI(createUpdateUI(GameApi.VIEWER_ID, eId, stateBeforeCast));
		verify(mockView).setViewerState(
				14,
				13,
				13,
				13,
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(53).get(),
				ImmutableList.<Tile>of(),
				MahjongMessage.INVISIBLE_);
	}
	
	/**
	 * Test the five states during Hu operation
	 */
	@Test
	public void testAutoHuCheckByFetchStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeAutoHuCheckByFetch));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,13)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(53).get(),
				MahjongMessage.AUTO_HU_CHECK_);
	}
	
	@Test
	public void testAutoHuCheckByCastStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeAutoHuCheckByCast));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.AUTO_HU_CHECK_);
	}
	
	@Test
	public void testWaitHuChoiceStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeWaitHuChoice));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.WAIT_HU_CHOICE_);
	}
	
	@Test
	public void testHuDeclareStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeHuDeclare));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.declareHuMove(mahjongPresenter.getMahjongState()));
	}
	
	@Test
	public void testHuCheckStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeHuCheck));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.checkIfHuMove(mahjongPresenter.getMahjongState()));
	}
	
	/**
	 * Test the four states during peng operation
	 */
	@Test
	public void testAutoPengCheckStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeAutoPengCheck));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.AUTO_PENG_CHECK_);
	}
	
	@Test
	public void testWaitPengChoiceStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeWaitPengChoice));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.WAIT_PENG_CHOICE_);
	}
	
	@Test
	public void testPengDeclareStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforePengDeclare));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockView).choosePengTiles(ImmutableList.<Tile>of(), mahjongPresenter.getMyTiles(), 
				ImmutableList.<Integer>of(), mahjongPresenter.getMahjongState().getE());
	}
	
	@Test
	public void testPengCheckStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforePengCheck));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.checkIfPengMove(mahjongPresenter.getMahjongState()));
	}
	
	/**
	 * Test the four states during chi operation
	 */
	@Test
	public void testAutoChiCheckStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeAutoChiCheck));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.AUTO_CHI_CHECK_);
	}
	
	@Test
	public void testWaitChiChoiceStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeWaitChiChoice));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.WAIT_CHI_CHOICE_);
	}
	
	@Test
	public void testChiDeclareStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeChiDeclare));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockView).chooseChiTiles(ImmutableList.<Tile>of(), mahjongPresenter.getMyTiles(), 
				ImmutableList.<Integer>of(), mahjongPresenter.getMahjongState().getE());
	}
	
	@Test
	public void testChiCheckStateForETurnOfE() {
		mahjongPresenter.updateUI(createUpdateUI(eId, eId, stateBeforeChiCheck));
		verify(mockView).setPlayerState(
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				13,
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongLogic.getIndicesInRange(0, -1),
				mahjongLogic.getIndicesInRange(54, 67),
				mahjongLogic.getIndicesInRange(68, 101),
				mahjongLogic.getIndicesInRange(102, 135),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getTileListFromIndexList(mahjongLogic.getIndicesInRange(0,12)),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				ImmutableList.<Tile>of(),
				mahjongPresenter.getMahjongState().getTiles().get(52).get(),
				MahjongMessage.NO_MOVE_);
		verify(mockContainer).sendMakeMove(mahjongLogic.checkIfChiMove(mahjongPresenter.getMahjongState()));
	}
	
	private UpdateUI createUpdateUI(
		      int yourPlayerId, int turnOfPlayerId, Map<String, Object> state) {
		    return new UpdateUI(
		    	yourPlayerId, 
		    	playersInfo, state,
		        emptyState, 
		        ImmutableList.<Operation>of(new SetTurn(turnOfPlayerId)),
		        0,
		        ImmutableMap.<Integer, Integer>of());
		  }
	
	
}


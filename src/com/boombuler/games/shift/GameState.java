package com.boombuler.games.shift;

public class GameState {

	public int Bonus = 0;
	public long TotalScore = 0;
	public int LastMoveScore = 0;
	public final Game.Difficulty Difficulty;
	public byte[][] Board;
	
	public GameState(Game.Difficulty difficulty) {
		Difficulty = difficulty;
		Board = new byte[Game.BOARD_SIZE_WITH_CACHE][Game.BOARD_SIZE_WITH_CACHE];
		FreeBoard();
	}
		
	public void FreeBoard() {
		for(int row = 0; row < Game.BOARD_SIZE_WITH_CACHE; row++) {
			for (int col = 0; col < Game.BOARD_SIZE_WITH_CACHE; col++) {
				Board[row][col] = Game.BLOCK_TYPE_FREE;
			}
		}
	}
}

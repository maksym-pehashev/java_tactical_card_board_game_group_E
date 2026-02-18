package structures.basic;

public class Board {
    private final int width;
	private final int height;

	private final Tile[][] tiles;
	private final Unit[][] occupants;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		this.occupants = new Unit[width][height];
	}

	public int getWidth() { return width; }
	public int getHeight() { return height; }

	public boolean inBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}

	public void setTile(int x, int y, Tile tile) {
		tiles[x][y] = tile;
	}

	public Unit getUnitAt(int x, int y) {
		return occupants[x][y];
	}

	public void setUnitAt(int x, int y, Unit unit) {
		occupants[x][y] = unit;
	}

	public void clearUnitAt(int x, int y) {
		occupants[x][y] = null;
	}

	public boolean isOccupied(int x, int y) {
		return getUnitAt(x, y) != null;
	}
}

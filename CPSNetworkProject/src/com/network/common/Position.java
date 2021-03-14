package com.network.common;

import com.network.interfaces.PositionI;

/**
 * Class for position
 * @author Softwarkers
 *
 */
public class Position implements PositionI{
	
	/**
	 * Coordinate
	 */
	private int x, y;
	/**
	 * Create a position
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Position(int x, int y) {
		this.x = x; this.y = y;
	}
	
	@Override
	public double distance(PositionI other) {
		Position o = (Position) other;
		return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
	}

}

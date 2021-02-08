package com.network.common;

import com.network.interfaces.PositionI;

public class Position implements PositionI{
	
	private int x, y;
	public Position(int x, int y) {
		this.x = x; this.y = y;
	}
	@Override
	public double distance(PositionI other) {
		Position o = (Position) other;
		return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
	}

}


public class Point implements Compare2D<Point>
{
	private long xCord;
	private long yCord;
	
	public Point(long x, long y)
	{
		xCord = x;
		yCord = y;
	}
	
	public void set(long x, long y)
	{
		xCord = x;
		yCord = y;
	}
	
	@Override
	public long getX()
	{
		return xCord;
	}

	@Override
	public long getY()
	{
		return yCord;
	}

	@Override
	public Direction directionFrom(long X, long Y)
	{
		long length = xCord - X;
		long height = yCord - Y;
		
		if(length == 0 && height == 0)
		{
			return Direction.NOQUADRANT;
		}
		else if(length > 0 && height >= 0)
		{
			return Direction.NE;
		}
		else if(length <= 0 && height > 0)
		{
			return Direction.NW;
		}
		else if(length < 0 && height <= 0)
		{
			return Direction.SW;
		}
		else
		{
			return Direction.SE;
		}		
	}

	@Override
	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi)
	{
		long originX = (long) ((xHi + xLo) / 2);
		long originY = (long) ((yHi + yLo) / 2);
		
		if(xCord > xHi || xCord < xLo || yCord > yHi || yCord < yLo)
		{
			return Direction.NOQUADRANT;
		}
		else if((xCord > originX && yCord >= originY) || (xCord == originX && yCord == originY))
		{
			return Direction.NE;
		}
		else if(xCord <= originX && yCord > originY)
		{
			return Direction.NW;
		}
		else if(xCord < originX && yCord <= originY)
		{
			return Direction.SW;
		}
		else
		{
			return Direction.SE;
		}	
	}

	@Override
	public boolean inBox(double xLo, double xHi, double yLo, double yHi)
	{
		return  ((xLo <= xCord && xCord <= xHi) && (yLo <= yCord && yCord <= yHi));
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Point)
		{
			return (((Point) o).getX() == xCord) && (((Point) o).getY() == yCord);
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		return "(" + xCord + "," + yCord + ")";
	}

}

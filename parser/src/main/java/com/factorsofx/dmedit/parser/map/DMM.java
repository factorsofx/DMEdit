package com.factorsofx.dmedit.parser.map;

public class DMM
{
    private TileState[][] map;

    public DMM(int width, int height, TileState defaultState)
    {
        map = new TileState[width][height];

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                map[x][y] = defaultState;
            }
        }
    }
}

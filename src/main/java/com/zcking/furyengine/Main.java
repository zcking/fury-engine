package com.zcking.furyengine;

import com.zcking.furyengine.engine.GameEngine;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.game.examples.simple.NormalsDemo;
import com.zcking.furyengine.game.examples.simple.TerrainDemo;

public class Main
{
    public static void main( String[] args )
    {
        try {
//            IGameLogic gameLogic = new TerrainDemo();
            IGameLogic gameLogic = new NormalsDemo();
            GameEngine engine = new GameEngine(
                    "Fury Demo 1",
                    600, 480, true,
                    gameLogic
            );
            engine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}

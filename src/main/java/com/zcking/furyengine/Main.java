package com.zcking.furyengine;

import com.zcking.furyengine.engine.GameEngine;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.game.examples.simple.DummyGame;

public class Main
{
    public static void main( String[] args )
    {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine engine = new GameEngine(
                    "Fury Demo 1",
                    600, 480, vSync,
                    gameLogic
            );
            engine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}

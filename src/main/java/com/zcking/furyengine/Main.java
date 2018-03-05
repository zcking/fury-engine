package com.zcking.furyengine;

import com.zcking.furyengine.engine.GameEngine;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.engine.WindowSettings;
import com.zcking.furyengine.game.examples.simple.NormalsDemo;
import com.zcking.furyengine.game.examples.simple.TerrainDemo;

public class Main
{
    public static void main( String[] args )
    {
        try {
            // Instantiate any demo here
            IGameLogic gameLogic = new NormalsDemo();

            // Settings for the game windoow
            WindowSettings windowSettings = WindowSettings.create()
                    .withInitialHeight(480)
                    .withInitialWidth(600)
                    .withVSyncEnabled(true)
                    .withInitialTitle("Fury Engine Demo");
            GameEngine engine = new GameEngine(
                    windowSettings,
                    gameLogic
            );
            engine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}

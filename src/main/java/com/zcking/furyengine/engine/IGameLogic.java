package com.zcking.furyengine.engine;

import com.zcking.furyengine.input.MouseInput;

/**
 * The required logic for a game to be runnable by the engine.
 * See {@link com.zcking.furyengine.game.examples.simple} for examples.
 */
public interface IGameLogic {

    /**
     * Perform any necessary initialization for the game.
     * @param window The window instance which contains the window configuration.
     * @throws Exception If initialization fails.
     */
    void init(Window window) throws Exception;

    /**
     * Check for input from the window or mouse.
     * @param window The window instance to check for window state.
     * @param mouseInput The {@link MouseInput} instance for checking mouse state.
     */
    void input(Window window, MouseInput mouseInput);

    /**
     * The main update block for the game. Called once per frame.
     * @param interval The interval at which to update at.
     * @param mouseInput The {@link MouseInput} instance for checking mouse state.
     */
    void update(float interval, MouseInput mouseInput);

    /**
     * Renders objects to the display.
     * @param window The {@link Window} instance to render to.
     */
    void render(Window window);

    /**
     * Called by the game engine for the game to perform any
     * necessary garbage collection.
     */
    void cleanUp();

}

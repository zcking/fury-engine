package com.zcking.furyengine.engine;

import com.zcking.furyengine.input.MouseInput;

/**
 * The core of the fury engine. This class should only be instantiated once, and is used to "play"
 * an instance of a game, where a game is an implementation of the {@link com.zcking.furyengine.engine.IGameLogic}.
 *
 * The core engine managed the main game loop, timing, window management, and game runtime.
 */
public class GameEngine implements Runnable {

    private static final int TARGET_FPS = 75;
    private static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;

    /**
     * Instantiate the game engine.
     * @param windowSettings Configuration settings for the window.
     * @param gameLogic The implemented game logic to run.
     * @throws Exception If the engine initialization fails.
     */
    public GameEngine(WindowSettings windowSettings, IGameLogic gameLogic) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowSettings);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    /**
     * Instantiates the game engine.
     * @param windowTitle The text to display for the window title bar.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param vSync Whether or not V-sync should be used for the display.
     * @param gameLogic The implemented game logic to run.
     * @throws Exception If the game engine initialization fails.
     */
    @Deprecated
    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic) throws Exception {
        this(WindowSettings.create()
                .withInitialWidth(width)
                .withInitialHeight(height)
                .withVSyncEnabled(vSync)
                .withInitialTitle(windowTitle),
            gameLogic);
    }

    /**
     * Starts the main game loop thread.
     */
    public void start() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    /**
     * Runs the game engine. Should be called after {@link GameEngine#start()}.
     * Automatically performs game engine cleanup on the termination of the engine.
     */
    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanUp();
        }
    }

    /**
     * Initializes the display/window, the game timer, mouse input, and game logic.
     * @throws Exception If any of the initialization steps fails.
     */
    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    /**
     * The main game loop. This starts when {@link GameEngine#run()} is called.
     * Automatically handles synchronization of FPS.
     */
    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    /**
     * Helper for the main game loop ({@link GameEngine#gameLoop()}) to synchronize FPS.
     */
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
//                ex.printStackTrace();
            }
        }
    }

    /**
     * Checks for input from the mouse and game logic.
     */
    private void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    /**
     * Calls the {@link GameEngine#gameLogic} update.
     * @param interval The interval at which to update.
     */
    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    /**
     * Renders to the {@link GameEngine#gameLogic} and updates the {@link GameEngine#window}.
     */
    protected void render() {
        gameLogic.render(window);
        window.update();
    }

    /**
     * Performs necessary garbage collection of resources.
     */
    protected void cleanUp() {
        gameLogic.cleanUp();
    }
}

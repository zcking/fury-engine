package com.zcking.furyengine.engine;

import com.zcking.furyengine.input.MouseInput;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);

    void cleanUp();

}

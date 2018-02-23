# The Fury Engine
[![Build Status](https://travis-ci.org/zcking/fury-engine.svg?branch=master)](https://travis-ci.org/zcking/fury-engine)  

A lightweight OpenGL game engine built for JVM developers.

**Note**: this project is (initially) very-heavily influenced by the _book_, 
"[3D Game Development with LWJGL 3](https://www.gitbook.com/book/lwjglgamedev/3d-game-development-with-lwjgl/details)". 
I, this project, nor any future contributors to this project are in no way responsible for any misuse, modification, 
or distribution of the original source code available from this _book_. The Fury Engine should be interpreted as 
a incubating library, which uses this _book_'s content--as well as several other resources--to help construct 
its systems and organization. **For the time being, please consider this project to be 
a personal following of the aforementioned _book_.**

---

## Usage
To package the engine from source:
```bash
mvn package
```
This will produce the packaged JAR at `target/fury-engine-0.1.jar` and the "fat" JAR, which includes the project 
dependencies at `target/fury-engine-0.1-jar-with-dependencies.jar`.

---

To run tests:
```bash
mvn test -V -e
```
The `-V` option outputs your version info, and the `-e` flag will output error stacktraces if any occur.

---

## LICENSE
This project is licensed under the Apache License 2, which is available [here](./LICENSE).
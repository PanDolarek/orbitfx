# OrbitFX

OrbitFX is a 2D gravity simulator, developed in Java using JavaFX. The project allows users to observe planetary systems, track individual celestial bodies, and manipulate their physical properties to see how they affect the gravitational environment.

## Features

* **Real-Time Physics:** Smooth trajectory calculations utilizing the Euler method with a fixed timestep to ensure deterministic orbits.
* **Dynamic Time Scale:** Adjustable simulation speed ranging from real-time (1s/s) up to extreme acceleration (1 Year/s) without sacrificing orbital stability or performance.
* **Inelastic Collisions:** Objects colliding at high relative speeds result in the smaller object being absorbed by the larger one, strictly conserving the total momentum of the system.

## Controls & Usage

Upon launching the application, the system is paused. To interact with the simulation:
1. Click the **Start/Reset** button to initialize the planetary system.
2. Left-click on any celestial body to focus the camera on it. Its current mass, radius, and velocity will appear in the right-side panel.
3. You can modify these values by typing new numbers and pressing `Enter`. **Note:** Parameter editing is locked while the planets are moving. You can only change these values when the simulation is paused.
4. Once your setup is ready, click the **Pause/Resume** button to unpause the system and watch the physics unfold.

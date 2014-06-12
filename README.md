# Gravity Simulator

This n-body simulator models groups of celestial bodies under the action of gravitational forces. Several presets are included to provide a qualitative understanding of the mechanics of motion in various gravitational conditions.

The simulator uses the velocity verlet algorithm to approximate the positions of celestial bodies from the initial conditions and interaction potentials.

### How to Run

Compile and run the `src/GravitySimulator.java` file to launch the simulator.

### Adding New Configurations

New configurations can easily be added to the simulator by modifying the `src/ClusterConfigurations.xml` file. A configuration is indicated by `<configuration></configuration>` tags containing at least two `<body></body>` tags. Each of these must provide fields for the `diameter`, `mass`, `position`, `velocity`, and `color` of the body.

### Academic Applications

Academic users can take a look at the `Body`, `Cluster`, and `SpaceVector` classes. The `Body` class models a celestial body, containing the properties required to describe the object’s current state. The `Cluster` class models a series of `Body` objects, calculating interaction potentials and updating positions. The `SpaceVector` class models a mathematical vector, providing methods for scalar multiplication, addition, and normalization.

While the `double` primitive type is accurate enough for this visual representation, academic users may want to modify the classes to use a type that is less susceptible to the errors inherent in floating-point representations.

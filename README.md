# Camera_Measure
An Android app that measures the distance and height of an object using live camera
feed and sensors. Also, it can measure various objects in a captured image using Image
Processing techniques.
## Online Mode
In this mode the user uses the camera to adjust the phone at different angles. The app uses the accelerometer and gravity sensors to 
measure the angles required to measure the object. 
### How to use
- Keep the phone at eye level and enter the height of the phone above the ground (usually your height - 10cm).

- Point the crosshair at the bottom of the object and tap it.

- point crosshair at the top of the object and tap it.

- It should show the height of the object and the distance from the object.
Note: the object must be on the ground. If not check the full documentation for futher instructions.

## Offline Mode
In this mode the user can load an image from his phone, or capture one himself of the object to be measured. If a reference object
out of the ones supported (coin and A4 paper) exists in the same image, he can use it to measure the desired object. If not, he can 
enter the measurements himself of another object to act as a reference.

### How to use
#### Region Growing Mode
- Choose the reference object in the modes pop-up.

- Place the bullet on the reference object.

- Press on Take Reference Length button.

- The object will be colored to demonstrate that the region growing was successful. Sometimes, the object might not be correctly colored 
under different conditions (bad lighting, equal colored surroundings). You can adjust the threshold slider to fix it.

- Move the bullets around the object to measure it.


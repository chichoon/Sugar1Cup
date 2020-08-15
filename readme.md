# code assembled for personal projects (graduation)
- [x] 0715 Meeting
    - housing design
    - keras organization
    - export converted data to csv (Excel) file
- [x] 0729 Meeting
    - organize data
    - create 3-axis graph for RGB (Matlab)
    - create 3-axis graph for Gyro (Matlab)
    - housing design
    - Compare RGB data under different light condition
- [x] 0812 Meeting
    - add IR sensor for sugar measure
    - (effective detection for those who has similar color)
    - add multiplexer for more pins (RGB LED, IR sensor, etc)
- [ ] 0826 Meeting

### Hardware
- [x] Finish soldering Censors (RGB / Gyro I2C, Resistance Sensor, RGB LED, Battery)
- [ ] Add multiplexer & more sensors (IR, RGBLED, ...)
- [ ] Finish coding for arduino / attiny1614 chip
- [ ] Coat board for moisture / dust resistance
- [x] First modeling for 3D Printer
- [ ] Finish modeling until fit to the cup
- [ ] Finish printing actual model

### RGB
- [x] Collect data (5+)
- [ ] Collect data (10+)
- [ ] Collect data (20+)
- [ ] Collect Infrared Data & Compare similar color beverage
- [x] Compare RGB data under different light condition
- [x] Create function for binary data to RGB 8bit Integer
- [x] Create Matlab file for 3-axis scatter graph (RGB)
- [x] Create 3-axis scatter graph
- [x] Keras neural network initialize
- [x] Keras neural network tuning (~ 90%↑ accuracy)
- [ ] finish tuning + export for android

### GyroSensor
- [x] Collect data (not need to use keras ?) 
- [x] Create function for binary data to Pitch/Yaw/Roll Integer
- [x] Create Matlab file for 3-axis scatter graph
- [x] Create 3-axis scatter graph
- [ ] ~~Keras neural network initialize~~
- [ ] ~~Keras neural network tuning (~ 90%↑ accuracy)~~
- [ ] finish tuning + export for android

### Android
- [ ] Finish organizing UI (XML)
- [ ] Finish importing Keras layer data for android
- [ ] Function : collect bin data from sensor & detect beverage
- [ ] Function : collect bin data from sensor & detect whether drank or not
- [ ] Function : calculate sugar from beverage
- [ ] Function : Record the amount of sugar & water 
- [ ] Function : show the graph of the sugar & water per day
- [ ] Function : send an alert by how much the sugar user ate

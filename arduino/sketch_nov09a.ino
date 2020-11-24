#include <SoftwareSerial.h>
#include <Wire.h>
#include "1_.h"
//Final version

const int MPU = 0x68, TCS = 0x29;
void getMPU(), getTCS();
int16_t m_AcX = 0, m_AcY = 0, m_AcZ = 0, tmp = 0, m_GyX =0, m_GyY = 0, m_GyZ = 0, ir = 0;

boolean startflag;
int LEDR = 1, LEDG = 2, LEDB = 3, rx = 5, tx = 4, irpin = 10;
//6 : SDA (I2C)
//7: SCL (I2C)
//11 : updi

#define TCS_CDATAL           0x14 
#define TCS_RDATAL           0x16
#define TCS_GDATAL           0x18
#define TCS_BDATAL           0x1A
#define TCS_CMD_Read_Word    0x20
#define calibration_factor -7050.0

SoftwareSerial mySerial(tx, rx);

void setup() {
  mySerial.begin(9600);
  pinMode(LEDR, OUTPUT);
  pinMode(LEDG, OUTPUT);
  pinMode(LEDB, OUTPUT);
  pinMode(irpin, INPUT);

  Wire.begin();
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission();
  startflag = true;
  
  if(TCS_Init() != 0){
      return; 
  } 
}

void loop() {
  LED_on();
  RGB temp = TCS_Get_RGBData();
  getMPU();
  ir = analogRead(irpin);
  byte buffer1[26];
  
  buffer1[0] = 0x02;
  buffer1[1] = m_AcX & 0xFF;
  buffer1[2] = (m_AcX >> 8) & 0xFF;
  buffer1[3] = m_AcY & 0xFF;
  buffer1[4] = (m_AcY >> 8) & 0xFF;
  buffer1[5] = m_AcZ & 0xFF;
  buffer1[6] = (m_AcZ >> 8) & 0xFF;
  buffer1[7] = tmp & 0xFF;
  buffer1[8] = (tmp >> 8) & 0xFF;
  buffer1[9] = m_GyX & 0xFF;
  buffer1[10] = (m_GyX >> 8) & 0xFF;
  buffer1[11] = m_GyY & 0xFF;
  buffer1[12] = (m_GyY >> 8) & 0xFF;
  buffer1[13] = m_GyZ & 0xFF;
  buffer1[14] = (m_GyZ >> 8) & 0xFF;
  buffer1[15] = temp.C & 0xFF;
  buffer1[16] = (temp.C >> 8) & 0xFF;
  buffer1[17] = temp.R & 0xFF;
  buffer1[18] = (temp.R >> 8) & 0xFF;
  buffer1[19] = temp.G & 0xFF;
  buffer1[20] = (temp.G >> 8) & 0xFF;
  buffer1[21] = temp.B & 0xFF;
  buffer1[22] = (temp.B >> 8) & 0xFF;
  buffer1[23] = ir & 0xFF;
  buffer1[24] = (ir >> 8) & 0xFF;
  buffer1[25] = 0x03;
  
  mySerial.write(buffer1, 26);
  delay(100);
}

void LED_on(){
  int value = 0;
  if(mySerial.available()){
      value = mySerial.read();
    }
  if(value == 0){ //GREEN
    digitalWrite(LEDR, HIGH);
    digitalWrite(LEDG, LOW);
    digitalWrite(LEDB, HIGH);
  }
  else if(value == 1){ //YELLOW
    digitalWrite(LEDR, LOW);
    digitalWrite(LEDG, HIGH);
    digitalWrite(LEDB, HIGH);   
  }
  else if(value == 2){ //RED
    digitalWrite(LEDR, HIGH);
    digitalWrite(LEDG, HIGH);
    digitalWrite(LEDB, LOW);
  }
  else{ //error
    digitalWrite(LEDR, LOW);
    digitalWrite(LEDG, LOW);
    digitalWrite(LEDB, LOW);   
  }
}

void getMPU(){
  Wire.beginTransmission(MPU);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 0x0E);
  m_AcX=Wire.read()<<8|Wire.read();
  m_AcY=Wire.read()<<8|Wire.read();
  m_AcZ=Wire.read()<<8|Wire.read();
  tmp=Wire.read()<<8|Wire.read();
  m_GyX=Wire.read()<<8|Wire.read();
  m_GyY=Wire.read()<<8|Wire.read();
  m_GyZ=Wire.read()<<8|Wire.read();
}

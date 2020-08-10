#include <SoftwareSerial.h>
#include <Wire.h>
#include <avr/sleep.h>
#include "1_.h"

const int MPU = 0x68, TCS = 0x29;
void getMPU(), getTCS();
int m_AcX, m_AcY, m_AcZ, tmp, m_GyX, m_GyY, m_GyZ;

int swpin = 0;
int ECPin = 1;
int ECGND = 2;
int ECPOW = 3;
int rx = 4;
int tx = 5;
//6 : SDA (I2C)
//7: SCL (I2C)
int LED = 10;
//11 : updi
int ppm = 0;
float raw = 0;
float Vdrop = 0;
float Rc = 0;
float K = 0;
int i = 0;
float buff = 0;

#define TCS_CDATAL           0x14 
#define TCS_RDATAL           0x16
#define TCS_GDATAL           0x18
#define TCS_BDATAL           0x1A
#define TCS_CMD_Read_Word    0x20
#define calibration_factor -7050.0

SoftwareSerial mySerial(tx, rx);

void setup() {
  mySerial.begin(9600);
  pinMode(swpin, INPUT_PULLUP);
  pinMode(LED, OUTPUT);
  pinMode(ECPin, INPUT);
  pinMode(ECPOW, OUTPUT);
  pinMode(ECGND, OUTPUT);
  digitalWrite(ECGND, LOW); //gnd
  
  Wire.pins(6,7);
  Wire.begin();
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  
  if(TCS_Init() != 0){
      return; 
  } 
}

void enterSleep(){
  attachInterrupt(swpin, wakeUp, LOW);
  delay(100);
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  sleep_enable();
  sleep_mode();
}

void wakeUp(){
  sleep_disable();
  detachInterrupt(swpin);
}

void loop() {
  digitalWrite(LED,HIGH);
  RGB temp = TCS_Get_RGBData();
  getMPU();
  byte buffer1[38];
  buffer1[0] = 0x02;
  buffer1[1] = m_AcX & 0xFF;
  buffer1[2] = (m_AcX >> 8) & 0xFF;
  buffer1[3] = (m_AcX >> 16) & 0xFF;
  buffer1[4] = (m_AcX >> 24) & 0xFF;
  buffer1[5] = m_AcY & 0xFF;
  buffer1[6] = (m_AcY >> 8) & 0xFF;
  buffer1[7] = (m_AcX >> 16) & 0xFF;
  buffer1[8] = (m_AcX >> 24) & 0xFF;
  buffer1[9] = m_AcZ & 0xFF;
  buffer1[10] = (m_AcZ >> 8) & 0xFF;
  buffer1[11] = (m_AcZ >> 16) & 0xFF;
  buffer1[12] = (m_AcZ >> 24) & 0xFF;
  buffer1[13] = tmp & 0xFF;
  buffer1[14] = (tmp >> 8) & 0xFF;
  buffer1[15] = (tmp >> 16) & 0xFF;
  buffer1[16] = (tmp >> 24) & 0xFF;
  buffer1[17] = m_GyX & 0xFF;
  buffer1[18] = (m_GyX >> 8) & 0xFF;
  buffer1[19] = (m_GyX >> 16) & 0xFF;
  buffer1[20] = (m_GyX >> 24) & 0xFF;
  buffer1[21] = m_GyY & 0xFF;
  buffer1[22] = (m_GyY >> 8) & 0xFF;
  buffer1[23] = (m_GyY >> 16) & 0xFF;
  buffer1[24] = (m_GyY >> 24) & 0xFF;
  buffer1[25] = m_GyZ & 0xFF;
  buffer1[26] = (m_GyZ >> 8) & 0xFF;
  buffer1[27] = (m_GyZ >> 16) & 0xFF;
  buffer1[28] = (m_GyZ >> 24) & 0xFF;
  buffer1[29] = temp.C & 0xFF;
  buffer1[30] = (temp.C >> 8) & 0xFF;
  buffer1[31] = temp.R & 0xFF;
  buffer1[32] = (temp.R >> 8) & 0xFF;
  buffer1[33] = temp.G & 0xFF;
  buffer1[34] = (temp.G >> 8) & 0xFF;
  buffer1[35] = temp.B & 0xFF;
  buffer1[36] = (temp.B >> 8) & 0xFF;
  buffer1[37] = 0x03;
  
  mySerial.write(buffer1, 38);
 
  delay(50);

  if(digitalRead(swpin) == LOW){
    delay(100);
    enterSleep();
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

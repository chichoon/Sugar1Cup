#include <SoftwareSerial.h>
#include <Wire.h>
#include <avr/sleep.h>
#include "1_.h"

const int MPU = 0x68, TCS = 0x29;
void getMPU(), getTCS(), getPPM();
int16_t m_AcX = 0, m_AcY = 0, m_AcZ = 0, tmp = 0, m_GyX =0, m_GyY = 0, m_GyZ = 0;

int swpin = 0, ECPin = 3, ECGND = 2, ECPOW = 1, rx = 4, tx = 5, LED = 10, ppm = 0;
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
  digitalWrite(LED,LOW);
  attachInterrupt(swpin, wakeUp, LOW);
  delay(100);
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  sleep_enable();
  sleep_mode();
  sleep_disable();
  detachInterrupt(swpin);
}

void wakeUp(){
}

void loop() {
  digitalWrite(LED,HIGH);
  RGB temp = TCS_Get_RGBData();
  getMPU();
  getPPM();
  byte buffer1[28];
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
  buffer1[23] = ppm & 0xFF;
  buffer1[24] = (ppm >> 8) & 0xFF;
  buffer1[25] = (ppm >> 16) & 0xFF;
  buffer1[26] = (ppm >> 24) & 0xFF;
  buffer1[27] = 0x03;
  
  mySerial.write(buffer1, 28);
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

void getPPM(){
  float raw = 0, EC = 0, Vdrop = 0, Rc = 0, K = 0, buff = 0;
  int i = 0;
  while(i <= 10){
  digitalWrite(ECPOW, HIGH);
  raw = analogRead(ECPin);
  raw = analogRead(ECPin);
  digitalWrite(ECPin, LOW);
  buff = buff + raw;
  i++;
  delay(10);
  }
  raw = buff / 10;

  EC = 1.38*(1 + 0.019*(10 - 25)); //10 celcius
  Vdrop = ((5 * (raw)) / 1024.0); //Vin = 5
  Rc = ((Vdrop * 1000) / (5 - Vdrop)) - 25;
  K = 1000 / (Rc * EC);

  EC = 1000 / (Rc * K);
  EC = EC / (1 + 0.019*(10-25));
  ppm = EC * 0.7 * 1000;
}

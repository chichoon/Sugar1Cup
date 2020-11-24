#include "1_.h"
const int TCS = 0x29;

TCSIntegrationTime_t IntegrationTime_t = TCS_INTEGRATIONTIME_700MS;
TCSGain_t  Gain_t = TCS_GAIN_60X;

uint16_t  TCS_Init(void)
{
  uint8_t ID = 0;
  ID = TCS_ReadByte(0x12);
  if(ID != 0x44 && ID != 0x4D){
      return 1;
  }
    TCS_WriteByte(0x01, TCS_INTEGRATIONTIME_154MS);
    IntegrationTime_t = TCS_INTEGRATIONTIME_154MS;

    TCS_WriteByte(0x0F, TCS_GAIN_60X); 
    Gain_t = TCS_GAIN_60X;

  IntegrationTime_t = TCS_INTEGRATIONTIME_154MS;
  Gain_t = TCS_GAIN_60X;
  //Set Interrupt
    
    TCS_WriteByte(0x04, 0x00ff & 0xff);
    TCS_WriteByte(0x05, 0x00ff >> 8);
    TCS_WriteByte(0x06, 0xff00 & 0xff);
    TCS_WriteByte(0x07, 0xff00 >> 8);

    if(TCS_PERS_2_CYCLE < 0x10)
        TCS_WriteByte(TCS_PERS, TCS_PERS_2_CYCLE);
    else 
        TCS_WriteByte(TCS_PERS, TCS_PERS_60_CYCLE);

  TCS_Enable();
  TCS_Interrupt_Enable();
  //Set the LCD brightness

  return 0;
}

static void TCS_Enable(void)
{
    TCS_WriteByte(TCS_ENABLE, TCS_ENABLE_PON);
    DEV_Delay_ms(3);
    TCS_WriteByte(TCS_ENABLE, TCS_ENABLE_PON | TCS_ENABLE_AEN);
    DEV_Delay_ms(3);  
}

void TCS_Disable(void)
{
    uint8_t reg = 0;
    reg = TCS_ReadByte(TCS_ENABLE);
    TCS_WriteByte(TCS_ENABLE, reg & ~(TCS_ENABLE_PON | TCS_ENABLE_AEN));
}

static void TCS_Interrupt_Enable()
{
    uint8_t data = 0;
    data = TCS_ReadByte(TCS_ENABLE);
    TCS_WriteByte(TCS_ENABLE, data | TCS_ENABLE_AIEN);
}

void TCS_Interrupt_Disable()
{
    uint8_t data = 0;
    data = TCS_ReadByte(TCS_ENABLE);
    TCS_WriteByte(TCS_ENABLE, data & (~TCS_ENABLE_AIEN));
}


static void TCS_WriteByte(uint8_t add, uint8_t data)
{
    add = add | 0x80;
    DEV_I2C_WriteByte(add, data);
}

static uint8_t TCS_ReadByte(uint8_t add)
{
    add = add | 0x80;
    return DEV_I2C_ReadByte(add);
}

static uint16_t TCS_ReadWord(uint8_t add)
{
    add = add | 0x80;
    return DEV_I2C_ReadWord(add);
}

uint8_t DEV_I2C_ReadByte(uint8_t add_)
{
  Wire.beginTransmission(TCS);
  Wire.write(add_);
  Wire.endTransmission();
  Wire.requestFrom(TCS, 1);
  if (Wire.available()) {
   return Wire.read();
  }
  return 0;;
}

uint16_t DEV_I2C_ReadWord(uint8_t add_)
{
 uint16_t x; uint16_t t;

  Wire.beginTransmission(TCS);
  Wire.write(add_); 
  Wire.endTransmission();
  Wire.requestFrom(TCS, 2);
  t = Wire.read();
  x = Wire.read();
  x <<= 8;
  x |= t;
  return x;
}

void DEV_I2C_WriteByte(uint8_t add_, uint8_t data_)
{
  Wire.beginTransmission(TCS);

  Wire.write(add_);
  Wire.write(data_ & 0xFF);
  Wire.endTransmission();
}

RGB TCS_Get_RGBData()
{
    RGB temp;
    temp.C = TCS_ReadWord(TCS_CDATAL | TCS_CMD_Read_Word);
    temp.R = TCS_ReadWord(TCS_RDATAL | TCS_CMD_Read_Word);
    temp.G = TCS_ReadWord(TCS_GDATAL | TCS_CMD_Read_Word);
    temp.B = TCS_ReadWord(TCS_BDATAL | TCS_CMD_Read_Word);
    DEV_Delay_ms(154);
    return temp;
}

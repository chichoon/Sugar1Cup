#include "Arduino.h"
#include <Wire.h>

#define TCS_PERS             0x0C 
#define TCS_PERS_NONE        0x00 
#define TCS_PERS_1_CYCLE     0x01 
#define TCS_PERS_2_CYCLE     0x02 
#define TCS_PERS_60_CYCLE    0x0f 

#define TCS_ENABLE           0x00     
#define TCS_ENABLE_AIEN      0x10
#define TCS_ENABLE_WEN       0x08 
#define TCS_ENABLE_AEN       0x02
#define TCS_ENABLE_PON       0x01 

#define TCS_CDATAL           0x14 
#define TCS_RDATAL           0x16
#define TCS_GDATAL           0x18
#define TCS_BDATAL           0x1A
#define TCS_CMD_Read_Word    0x20

#define DEV_Delay_ms(__xms)    delay(__xms)

typedef enum
{
  TCS_INTEGRATIONTIME_2_4MS  = 0xFF, 
  TCS_INTEGRATIONTIME_24MS   = 0xF6, 
  TCS_INTEGRATIONTIME_50MS   = 0xEB, 
  TCS_INTEGRATIONTIME_101MS  = 0xD5, 
  TCS_INTEGRATIONTIME_154MS  = 0xC0, 
  TCS_INTEGRATIONTIME_700MS  = 0x00  
}
TCSIntegrationTime_t;

typedef enum
{
  TCS_GAIN_1X                = 0x00,
  TCS_GAIN_4X                = 0x01,
  TCS_GAIN_16X               = 0x02,
  TCS_GAIN_60X               = 0x03 
}
TCSGain_t;

typedef struct{
   uint16_t R;
   uint16_t G;
   uint16_t B;
   uint16_t C;
}RGB;

static void TCS_Enable(void);
void TCS_Disable(void);
static void TCS_Interrupt_Enable();
void TCS_Interrupt_Disable();
static void TCS_WriteByte(uint8_t add, uint8_t data);
static uint8_t TCS_ReadByte(uint8_t add);
static uint16_t TCS_ReadWord(uint8_t add);
uint8_t DEV_I2C_ReadByte(uint8_t add_);
uint16_t DEV_I2C_ReadWord(uint8_t add_);
void DEV_I2C_WriteByte(uint8_t add_, uint8_t data_);
uint16_t  TCS_Init(void);
RGB TCS_Get_RGBData();

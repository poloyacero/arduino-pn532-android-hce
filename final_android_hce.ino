#include <Wire.h>
#include <PN532_I2C.h>
#include <PN532.h>

PN532_I2C pn532i2c(Wire);
PN532 nfc(pn532i2c);

#define SELECT_RESPONSE_OK "900"

void setup(void) {
  Serial.begin(9600);
  setupNFC();
}

void loop() {  
  bool success;
  uint8_t responseLength = 32;
  String statusCode = "";
  String otp = "";
  String pcResponse = "";

  Serial.println("Waiting for an ISO14443A card");

  // set shield to inListPassiveTarget
  success = nfc.inListPassiveTarget();

  if (success) {
    Serial.println("Found something!");

    uint8_t selectApdu[] = {0x00,                                     /* CLA */
                            0xA4,                                     /* INS */
                            0x04,                                     /* P1  */
                            0x00,                                     /* P2  */
                            0x06,                                     /* Length of AID  */
                            0xF0, 0xAB, 0xCD, 0xEF, 0x12, 0x34, /* AID defined on Android App */
                            /*0x00*/ /* Le  */};

    uint8_t response[32];

    success = nfc.inDataExchange(selectApdu, sizeof(selectApdu), response, &responseLength);
    
    if (success) {
      //Serial.print("responseLength: ");
      //Serial.println(responseLength);

      processResponse(response, responseLength, statusCode, otp);

      if(statusCode == SELECT_RESPONSE_OK) {
        //Serial.print("OTP: ");
        //Serial.println(otp.toInt());
        Serial.println(otp.toInt());
        
        Serial.println(Serial.readString());

      }
      // TODO: check result from PC
      // TODO: setup protocol here (eg. 0x00) depending
          uint8_t apdu[] = {0x01};
          uint8_t back[32];
          uint8_t apduLength = 32;
          success = nfc.inDataExchange(apdu, sizeof(apdu), back, &apduLength);
          processResponse(apdu, apduLength, statusCode, otp);
      
      if (success) {
        Serial.println("Done!");
      }else {
        Serial.println("Broken connection?");
      }
 
    }else {
      Serial.println("Failed sending SELECT AID");
    }
    
  }
  else {
    Serial.println("Didn't find anything!");
  }

  delay(3000);
}

// process response to HEX
void processResponse(uint8_t *response, uint8_t responseLength, String &statusCode, String &otp){
  String respBuffer;

  for (int i = 0; i < responseLength; i++) {
    if (response[i] < 0x10)
      respBuffer = respBuffer + "0"; //Adds leading zeros if hex value is smaller than 0x10
    if(i == 0 || i == 1) {
      statusCode = statusCode + String(response[i], HEX);
    }
    if(i > 1 && !(i < 1)) {
      // convert HEX then to ascii for otp
      otp = otp + convertToASCII(String(response[i], HEX));
    }
    respBuffer = respBuffer + String(response[i], HEX) + " ";
  }
  
  /*Serial.print("Response: ");
  Serial.println(respBuffer);
  Serial.print("OTP: ");
  Serial.println(otp);*/
}

String convertToASCII(String value) {
  return String(value.toInt() % 10);
}

void setupNFC() {
  nfc.begin();

  uint32_t versiondata = nfc.getFirmwareVersion();
  
  if (!versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1)
      ; // halt
  }

  // Got ok data, print it out!
  Serial.print("Found chip PN5");
  Serial.println((versiondata >> 24) & 0xFF, HEX);
  Serial.print("Firmware ver. ");
  Serial.print((versiondata >> 16) & 0xFF, DEC);
  Serial.print('.');
  Serial.println((versiondata >> 8) & 0xFF, DEC);

  // configure board to read RFID tags
  nfc.SAMConfig();
}

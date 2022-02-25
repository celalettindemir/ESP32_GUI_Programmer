
float DataArray[122];
float sayi=1181050440;
void setup() {
  // put your setup code here, to run once:
  
  Serial.begin(115200);  delay(100);          // seri port ac
  
  DataArray[94]=18021;
  
  DataArray[95]=26181;
  
  Serial.print(String(((int)DataArray[94])<<16|((int)DataArray[95])));
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(1000);
  Serial.println(String(((int)DataArray[94])<<16|((int)DataArray[95])));
}

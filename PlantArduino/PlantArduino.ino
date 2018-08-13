int humedad, temperatura, luminosidad, caracter;
int led = 13;
boolean r_aut=false,arduino_regando=false,notificaciones=false,notificado=false;
long previousMillis = 0;

void setup(){
  Serial.begin(9600);
  pinMode(led,OUTPUT); 
  digitalWrite(led,LOW);
}

void loop(){
  humedad=analogRead(0);
  temperatura=analogRead(1);
  luminosidad=analogRead(2);  
  temperatura = 5.0*temperatura*100.0/1024.0;
  String c = "-"; 
  String sl = humedad+c+luminosidad+c+temperatura;
  Serial.println(sl);
  
  //0-20 me falta agua
  //20-60 regular
  //+60 humedad satistactoria
  
  //Cuando falte agua, android acepte notf, y no haya notificado
  if(humedad<20 && notificaciones && !notificado){
      notificado=true;
      Serial.println("f");
      //Autoregar
      if(r_aut){
        digitalWrite(led, HIGH);
        arduino_regando = true; 
      }
  }
  
  //Cuando la humedad sea 50 y el arduino este regando, apaga la bomba
  if(humedad >=50  && arduino_regando){
    notificado=false;
    arduino_regando=false;
    digitalWrite(led,LOW);
    Serial.println("h");
  }
  
 
  if(Serial.available()>0){
    caracter=Serial.read();
    /*
    a - Señal de apertura para la bomba
    b - Preferencia del usuario, riego automatico
    c - Preferencia del usuario, no regar autimaticamente
    */       
    if(caracter=='a'){
      digitalWrite(led,HIGH);
      arduino_regando=true;      
    }
    if(caracter=='b'){
      r_aut=true;
    }
    if(caracter=='c'){
      r_aut=false;
    }
    if(caracter=='d'){
      notificaciones=true;
    }
    if(caracter=='e'){
      notificaciones=false;
    }    
  }
  delay(500);
}

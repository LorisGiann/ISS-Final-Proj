/////////////////////////////////////////////////////////////////
// Repeating Wifi Web Client                                   //
//                                                             //
// This sketch connects to a a web server and makes a request  //
// using an Arduino Wifi shield.                               //
//                                                             //
// Circuit:                                                    //
//   None                                                      //
//                                                             //
// created 23 April 2012                                       //
// modified 31 May 2012 by Tom Igoe                            //
// modified 13 Jan 2014 by Federico Vanzati                    //
// http://arduino.cc/en/Tutorial/WifiWebClientRepeating        //
// This code is in the public domain.                          //
//                                                             //
// adapted to Fishino 16 Ago 2015 by Massimo Del Fedele        //
/////////////////////////////////////////////////////////////////
#include <Fishino.h>
#include <SPI.h>
#include <Pushbutton.h>
#include <LiquidCrystal.h>

enum class MATERIAL {PLASTIC, GLASS};

//pushbuttons definitions
Pushbutton dec(14); //decrease
Pushbutton inc(15); //increase
Pushbutton changeMat(16); //change material
Pushbutton sendreq(17); //send


LiquidCrystal lcd(18,19,5,4,3,2);

#define OK_PIN_LED 7
#define REJECTED_PIN_LED 6
void initLedPins(){
  pinMode(OK_PIN_LED, OUTPUT);
  pinMode(REJECTED_PIN_LED, OUTPUT);
  digitalWrite(OK_PIN_LED, HIGH);
  digitalWrite(REJECTED_PIN_LED, HIGH);
}


//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
// CONFIGURATION DATA		-- ADAPT TO YOUR NETWORK !!!
// DATI DI CONFIGURAZIONE	-- ADATTARE ALLA PROPRIA RETE WiFi !!!
#ifndef __MY_NETWORK_H

// here pur SSID of your network
// inserire qui lo SSID della rete WiFi
#define MY_SSID	"AndroidAP-LG"

// here put PASSWORD of your network. Use "" if none
// inserire qui la PASSWORD della rete WiFi -- Usare "" se la rete non ￨ protetta
#define MY_PASS	"1111111111"

// here put required IP address (and maybe gateway and netmask!) of your Fishino
// comment out this lines if you want AUTO IP (dhcp)
// NOTE : if you use auto IP you must find it somehow !
// inserire qui l'IP desiderato ed eventualmente gateway e netmask per il fishino
// commentare le linee sotto se si vuole l'IP automatico
// nota : se si utilizza l'IP automatico, occorre un metodo per trovarlo !
//#define IPADDR	192, 168,   43, 150
//#define GATEWAY	192, 168,   43, 1
//#define NETMASK	255, 255, 255, 0

#endif
//                    END OF CONFIGURATION DATA                      //
//                       FINE CONFIGURAZIONE                         //
///////////////////////////////////////////////////////////////////////

// define ip address if required
// NOTE : if your network is not of type 255.255.255.0 or your gateway is not xx.xx.xx.1
// you should set also both netmask and gateway
#ifdef IPADDR
	IPAddress ip(IPADDR);
	#ifdef GATEWAY
		IPAddress gw(GATEWAY);
	#else
		IPAddress gw(ip[0], ip[1], ip[2], 1);
	#endif
	#ifdef NETMASK
		IPAddress nm(NETMASK);
	#else
		IPAddress nm(255, 255, 255, 0);
	#endif
#endif

// Initialize the Fishino client library
FishinoClient client;

// server address:
char server[] = "192.168.43.169";

// last time you connected to the server, in milliseconds
// l'ultima volta che vi siete connessi al server, in millisecondi
unsigned long lastConnectionTime = 0;

// delay between updates, in milliseconds
// ritardo tra gli aggiornamenti, in millisecondi
const unsigned long postingInterval = 2L * 1000L;

void error(){
  pinMode(12, OUTPUT);
  lcd.clear();
  lcd.print(F("error"));
  while(true){
    digitalWrite(12, HIGH);   // turn the LED on (HIGH is the voltage level)
    delay(1000);                       // wait for a second
    digitalWrite(12, LOW);    // turn the LED off by making the voltage LOW
    delay(1000);  
  }
}

void makeRequest(MATERIAL mat, byte qta){
	// close any connection before send a new request.
	// This will free the socket on the WiFi module
  client.stop();

	// if there's a successful connection:
	// se la connessione è riuscita:
	if (client.connect(server, 8095))
	{
		Serial << F("sending...\n");
		
		client 
		  << F("msg(depositrequest, request,python,wasteservice,depositrequest(") 
		  << ( (mat==MATERIAL::PLASTIC) ? F("PLASTIC") : F("GLASS") )
		  << F(",") 
		  << qta 
		  << F("),1)");
		client.println();
	}
	else
	{
		// if you couldn't make a connection:
		// se la connessione non è riuscita:
		Serial << F("connection failed\n");
    error();
	}

	// note the time that the connection was made:
	// registra il tempo in cui è stata fatta la connessione
	lastConnectionTime = millis();
}

void printWifiStatus()
{
	// print the SSID of the network you're attached to:
	// stampa lo SSID della rete:
	Serial.print("SSID: ");
	Serial.println(Fishino.SSID());

	// print your WiFi shield's IP address:
	// stampa l'indirizzo IP della rete:
	IPAddress ip = Fishino.localIP();
	Serial << F("IP Address: ");
	Serial.println(ip);

	// print the received signal strength:
	// stampa la potenza del segnale di rete:
	long rssi = Fishino.RSSI();
	Serial << F("signal strength (RSSI):");
	Serial.print(rssi);
	Serial << F(" dBm\n");
}


void setup()
{
  initLedPins();
  lcd.begin(16, 2);

  
	// Initialize serial and wait for port to open
	// Inizializza la porta seriale e ne attende l'apertura
	Serial.begin(115200);
	
	// only for Leonardo needed
	// necessario solo per la Leonardo
	while (!Serial)
		;

	// initialize SPI
	// inizializza il modulo SPI
//	SPI.begin();
//	SPI.setClockDivider(SPI_CLOCK_DIV2);
	
	// reset and test WiFi module
	// resetta e testa il modulo WiFi
	while(!Fishino.reset())
		Serial << F("Fishino RESET FAILED, RETRYING...\n");
	Serial << F("Fishino WiFi RESET OK\n");
	
	Fishino.setPhyMode(PHY_MODE_11N);

	// go into station mode
	// imposta la modalità stazione
	Fishino.setMode(STATION_MODE);

	// try forever to connect to AP
	// tenta la connessione finchè non riesce
	Serial << F("Connecting to AP...");
  lcd << F("Connecting to AP...");
	while(!Fishino.begin(MY_SSID, MY_PASS))
	{
		Serial << ".";
		delay(2000);
	}
	Serial << "OK\n";
	
	
	// setup IP or start DHCP client
	// imposta l'IP statico oppure avvia il client DHCP
#ifdef IPADDR
	Fishino.config(ip, gw, nm);
//	Fishino.setDNS(IPAddress(208, 67, 222, 222));
//	Fishino.setDNS(gw);
#else
	Fishino.staStartDHCP();
#endif

	// wait till connection is established
	Serial << F("Waiting for IP...");
	while(Fishino.status() != STATION_GOT_IP)
	{
		Serial << ".";
		delay(500);
	}
	Serial << "OK\n";

	
	// print connection status on serial port
	// stampa lo stato della connessione sulla porta seriale
	printWifiStatus();


	// if there's incoming data from the net connection.
	// send it out the serial port.  This is for debugging purposes only
	// se ci sono dati provenienti dalla rete
	// li invia alla porta seriale. Questo solo a scopo di debugging
	/*int n;
	while ( (n = client.available()) > 0)
	{
		char c = client.read();
		Serial.write(c);
		if(c == -1)
		{
			Serial.print("\nn:");
			Serial.println(n);
			Serial.print("_bufCount:");
			Serial.println(client.ref->_bufCount);
			Serial.print("RemAvail:");
			Serial.println(client.ref->remoteAvail());
		}
	}*/

	// if ten seconds have passed since your last connection,
	// then connect again and send data
	/*if (millis() - lastConnectionTime > postingInterval)
	{
		makeRequest();
		uint32_t tim = millis() + 500;
		while(!client.available() && millis() < tim)
			;
	}*/

  digitalWrite(OK_PIN_LED, LOW);
  digitalWrite(REJECTED_PIN_LED, LOW);
  updateScreen();
}


unsigned int val = 1;
MATERIAL mat = MATERIAL::PLASTIC;
String acc = "";
int n;
bool waitingRepl = false;
bool lastAcpted = false;
bool firstReq = true;

void updateScreen(){
  lcd.clear();
  lcd.setCursor(0, 0);
    lcd << val << F(" Kg of ") << ((mat==MATERIAL::PLASTIC) ? F("PLASTIC") : F("GLASS"));
  if(!firstReq){
    lcd.setCursor(0, 1);
    if(waitingRepl)
      lcd.print(F("Waiting reply..."));
    else{
      if(lastAcpted)
        lcd.print(F("Accepted - GO!"));
      else{
        lcd.print(F("Rejected - GO!"));
      }
    }
  }
}

void loop(){
  if (inc.getSingleDebouncedRelease()) {
      val++;
      updateScreen();
  }
  if (dec.getSingleDebouncedRelease()) {
      if(val>1) val--;
      updateScreen();
  }
  if (changeMat.getSingleDebouncedRelease()) {
      mat = (mat==MATERIAL::PLASTIC) ? MATERIAL::GLASS : MATERIAL::PLASTIC;
      updateScreen();
  }
  if (sendreq.getSingleDebouncedRelease()) {
      waitingRepl=true;
      firstReq=false;
      updateScreen();
      makeRequest(mat,val);
      digitalWrite(OK_PIN_LED, LOW);
      digitalWrite(REJECTED_PIN_LED, LOW);
  }
  while ( (n = client.available()) > 0){
    char c = client.read();
    Serial.write(c);
    acc.concat(c);
    if(c == -1 || c =='\n'){
      Serial.print("\nn:");
      Serial.println(n);
      Serial.print("_bufCount:");
      Serial.println(client.ref->_bufCount);
      Serial.print("RemAvail:");
      Serial.println(client.ref->remoteAvail());
      waitingRepl=false;
      firstReq=false;
      client.stop();
      if(acc.indexOf(F("loadaccept"))>=0){
        digitalWrite(OK_PIN_LED, HIGH);
        Serial.println(F("loadaccept found"));
        lastAcpted=true;
      }
      else if(acc.indexOf(F("loadrejected"))>=0){
        digitalWrite(REJECTED_PIN_LED, HIGH);
        Serial.println(F("loadrejected found"));
        lastAcpted=false;
      }
      updateScreen();
      acc="";
      break;
    }
  }
}

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

enum class MATERIAL {PLASTIC, GLASS};

//pushbuttons definitions
Pushbutton plastic(14); //select - increase plastic
Pushbutton glass(15); //select - increase glass
Pushbutton sendreq(16); //send

#define PLASTIC_PIN_LED 9
#define GLASS_PIN_LED 8
#define OK_PIN_LED 7
#define REJECTED_PIN_LED 6
void initLedPins(){
  pinMode(PLASTIC_PIN_LED, OUTPUT);
  pinMode(GLASS_PIN_LED, OUTPUT);
  pinMode(OK_PIN_LED, OUTPUT);
  pinMode(REJECTED_PIN_LED, OUTPUT);
  digitalWrite(PLASTIC_PIN_LED, HIGH);
  digitalWrite(GLASS_PIN_LED, HIGH);
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
  pinMode(13, OUTPUT);
  while(true){
    digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
    delay(1000);                       // wait for a second
    digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
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
  digitalWrite(PLASTIC_PIN_LED, LOW);
  digitalWrite(GLASS_PIN_LED, LOW);
}


unsigned int plasticVal = 0;
unsigned int glassVal = 0;
String acc = "";
int n;

void loop(){
  if (plastic.getSingleDebouncedRelease()) {
      glassVal = 0;
      plasticVal++;
      for(byte i=0; i<plasticVal; i++){
        digitalWrite(PLASTIC_PIN_LED, HIGH);
        delay(250);
        digitalWrite(PLASTIC_PIN_LED, LOW);
        delay(250);
      }
      delay(250);
  }
  if (glass.getSingleDebouncedRelease()) {
      plasticVal = 0;
      glassVal++;
      for(byte i=0; i<glassVal; i++){
        digitalWrite(GLASS_PIN_LED, HIGH);
        delay(250);
        digitalWrite(GLASS_PIN_LED, LOW);
        delay(250);
      }
      delay(250);
  }
  if (sendreq.getSingleDebouncedRelease()) {
      makeRequest(
        (plasticVal>0) ? MATERIAL::PLASTIC : MATERIAL::GLASS,
        (plasticVal>0) ? plasticVal : glassVal
      );
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
      client.stop();
      if(acc.indexOf(F("loadaccept"))>=0){
        digitalWrite(OK_PIN_LED, HIGH);
        Serial.println(F("loadaccept found"));
      }
      else if(acc.indexOf(F("loadrejected"))>=0){
        digitalWrite(REJECTED_PIN_LED, HIGH);
        Serial.println(F("loadrejected found"));
      }
      acc="";
      break;
    }
  }
}

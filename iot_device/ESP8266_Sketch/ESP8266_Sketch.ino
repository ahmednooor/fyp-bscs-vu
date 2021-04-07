// CS619 VU FYP Spring 2020
// Useful links and credits:
// https://robojax.com/learn/arduino/?vid=robojax_ESP8266_NodeMCU_DHT
// https://github.com/sarful/Interface-raindrop-sensor-to-NodeMcu-For-Beginner/blob/master/rain_sensor.ino.ino
// https://cdn.instructables.com/FJC/J4AP/JH8JBTHA/FJCJ4APJH8JBTHA.LARGE.jpg
// https://lastminuteengineers.com/wp-content/uploads/arduino/ESP-12E-Development-Board-ESP8266-NodeMCU-Pinout.png

#include <math.h>
#include <DHT.h>
#include <Adafruit_Sensor.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <ESP8266HTTPClient.h>

#include <TinyGPS++.h>
#include <SoftwareSerial.h>

#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// --- Digital (Dx) GPIO Pin Mappings ---
#define PIN_D0 16
#define PIN_D1 5
#define PIN_D2 4
#define PIN_D3 0
#define PIN_D4 2
#define PIN_D5 14
#define PIN_D6 12
#define PIN_D7 13
#define PIN_D8 15

// --- DHT settings ---
#define DHTPIN PIN_D5 // what digital pin we're connected to
// Uncomment "DHTTYPE" the one you're using!
// #define DHTTYPE DHT11   // DHT 11
#define DHTTYPE DHT22 // DHT 22  (AM2302), AM2321
// #define DHTTYPE DHT21   // DHT 21 (AM2301)
DHT dht(DHTPIN, DHTTYPE);
float temperatureC = 0.0; // temperature in Celsius
float humidity = 0.0;     // humidity in %

// --- Rain Drop Sensor Settings ---
#define RAIN_ANALOGUE_PIN A0    // input for LDR and rain sensor
#define RAIN_DIGITAL_PIN PIN_D6 // enable reading Rain sensor
int rainAnalogueValue = 0;      // variable to store the value coming from Rain sensor
int rainDigitalValue = 0;       // variable to store the value coming from Rain sensor
double rainMeasure = 0.0;       // variable to store rain measurement in mm

// --- gps settings ---
TinyGPSPlus gps;
#define RXPin PIN_D7             // TX from gps goes into RX of board
#define TXPin PIN_D8             // RX from gps goes into TX of board
SoftwareSerial gpsSerial(RXPin, TXPin); // (RX, TX) The serial connection to the GPS device
float latitude, longitude;
int year, month, date, hour, minute, second;
String date_str, time_str, lat_str, lng_str;
int pm;
uint32_t sats = 0;
// NOTE: also see https://www.esp8266.com/viewtopic.php?f=32&t=3829

// --- lcd settings ---
// set LCD address, number of columns and rows
// if you don't know your display address, run an I2C scanner sketch
#define LCD_ADDR 0x27 // I2C LCD Address: 0x27
#define LCD_COL 16
#define LCD_ROW 2
LiquidCrystal_I2C lcd(LCD_ADDR, LCD_COL, LCD_ROW);
bool shouldShowGpsCoordinates = false;

// --- wifi settings ---
#define SSID "..."    // Your WiFi name
#define PASS "..."    // Your WiFi password
ESP8266WebServer server(80); // on-board web server on port 80

// --- rest api http web server settings ---
#define REST_SERVER_IP "http://<your_username>.pythonanywhere.com"       // ip/domain of server
#define REST_SERVER_PORT ":80"                      // port at which api is running
#define REST_SERVER_ROUTE "/api/v1/device/data/update"     // route that handles put request from device

// --- device settings ---
#define DEVICE_ID "d001"            // device id added by admin
#define DEVICE_PASSWORD "p001"      // device password added by admin

void setup(void)
{
    // set baud rate for seriial monitor
    Serial.begin(115200);

    // setup dht
    dht.begin();

    // setup rain sensor pins
    pinMode(RAIN_DIGITAL_PIN, INPUT);
    pinMode(RAIN_ANALOGUE_PIN, INPUT);

    // set baud rate for serial conn to gps
    gpsSerial.begin(9600);

    // initialize LCD
    // lcd.begin();
    lcd.begin(PIN_D2, PIN_D1);
    lcd.backlight(); // turn on LCD backlight

    connectToWiFi();

    // setup web server routes
    server.on("/", indexRouteHandler);
    // server.on("/inline", // demo for other routes
    //           []() {
    //               server.send(200, "text/plain", "this works as well");
    //           });
    server.onNotFound(routeNotFound);

    server.begin();
    Serial.println("HTTP server started");
}

void loop(void)
{
    server.handleClient();
    MDNS.update();


    // read temperature and humidity
    float newTemperatureC = dht.readTemperature();     // Read temperature as Celsius (the default)
    float newHumidity = dht.readHumidity();            // Reading humidity

    temperatureC = !isnan(newTemperatureC) ? newTemperatureC : -1000.0;
    humidity = !isnan(newHumidity) ? newHumidity : -1000.0;


    // read rain sensor
    rainAnalogueValue = analogRead(RAIN_ANALOGUE_PIN);
    rainDigitalValue = digitalRead(RAIN_DIGITAL_PIN);

    // the rain measure is based on the ratio of change in analogue value when water is present and 
    // total analogue value when no water is pressent. 0.5mm is the height of water if the whole sensor
    // is covered with water. This measure is instantaneous rather than time oriented.
    rainMeasure = rainDigitalValue == 0
                      ? ((double)(1024 - rainAnalogueValue) / 1024.00) * 0.5
                      : 0.00;


    // read GPS coordinates
    readDataFromGPS();


    // print data on serial monitor
    Serial.println("");
    Serial.println("--- Sensors Data ---");

    Serial.print("TMP: ");
    Serial.print(temperatureC != -1000.0 ? String(temperatureC, 2) : "--");
    Serial.println(" °C");
    
    Serial.print("HMD: ");
    Serial.print(humidity != -1000.0 ? String(humidity, 2) : "--");
    Serial.println(" %");
    
    Serial.print("RAN: ");
    Serial.print(rainMeasure);
    Serial.println(" mm");

    Serial.println("--- GPS Data ---");
    
    Serial.print("SAT: ");
    Serial.println(sats);
    Serial.print("LAT: ");
    Serial.println(lat_str != "" ? lat_str : "--");
    Serial.print("LNG: ");
    Serial.println(lng_str != "" ? lng_str : "--");
    Serial.println("");


    // print data on 16*2 LCD
    if (shouldShowGpsCoordinates)
    {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("LAT ");
        lcd.print(lat_str != "" ? lat_str : "--");
        lcd.setCursor(0, 1);
        lcd.print("LNG ");
        lcd.print(lng_str != "" ? lng_str : "--");
        shouldShowGpsCoordinates = false;
    }
    else
    {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("T");
        lcd.print(temperatureC != -1000.0 ? String(temperatureC, 2) : "--");
        lcd.print("C ");
        lcd.print("H");
        lcd.print(humidity != -1000.0 ? String(humidity, 2) : "--");
        lcd.print("% ");
        lcd.setCursor(0, 1);
        lcd.print("R");
        lcd.print(rainMeasure);
        lcd.print("mm ");
        lcd.print("G*");
        lcd.print(sats);
        shouldShowGpsCoordinates = true;
    }

    // send data to server
    sendJSONPayloadToServer(REST_SERVER_IP, REST_SERVER_PORT, REST_SERVER_ROUTE);

    lat_str = "";
    lng_str = "";
    
    // standard advised delay is 2000 for dht22
    // smartDelay helps to read data from gps while delaying
    smartDelay(2000);
}

void sendJSONPayloadToServer(String ip, String port, String route)
{
    // wait for WiFi connection
    if ((WiFi.status() == WL_CONNECTED))
    {

        WiFiClient client;
        HTTPClient http;

        Serial.print("[HTTP] begin...\n");
        // configure target server and url
        String url = ip + port + route;
        http.begin(client, url);
        http.addHeader("Content-Type", "application/json");

        Serial.print("[HTTP] POST...\n");
        // start connection and send HTTP header and body
        int httpCode = http.POST(generateJSONPayload());

        // httpCode will be negative on error
        if (httpCode > 0)
        {
            // HTTP header has been sent and Server response header has been handled
            Serial.printf("[HTTP] POST... code: %d\n", httpCode);

            const String &payload = http.getString();
            Serial.println("received payload:\n<<");
            Serial.println(payload);
            Serial.println(">>");
        }
        else
        {
            Serial.printf("[HTTP] POST... failed, error: %s\n", http.errorToString(httpCode).c_str());
        }

        http.end();
    }
    else
    {
        connectToWiFi();
    }
}

void readDataFromGPS()
{
    sats = gps.satellites.value(); // read num of conneceted satellites

    while (gpsSerial.available() > 0) //while data is available
    {
        char c = gpsSerial.read();
        // Serial.print(c);
        
        gps.encode(c);
        if (gps.location.isValid()) //check whether gps location is valid
        {
            latitude = gps.location.lat();
            lat_str = String(latitude, 6); // latitude location is stored in a string
            longitude = gps.location.lng();
            lng_str = String(longitude, 6); //longitude location is stored in a string
        }

        if (gps.date.isValid()) //check whether gps date is valid
        {
            date_str = "";
            date = gps.date.day();
            month = gps.date.month();
            year = gps.date.year();
            if (date < 10)
                date_str = '0';
            date_str += String(date); // values of date,month and year are stored in a string
            date_str += " / ";

            if (month < 10)
                date_str += '0';
            date_str += String(month); // values of date,month and year are stored in a string
            date_str += " / ";
            if (year < 10)
                date_str += '0';
            date_str += String(year); // values of date,month and year are stored in a string
        }
        if (gps.time.isValid()) //check whether gps time is valid
        {
            time_str = "";
            hour = gps.time.hour();
            minute = gps.time.minute();
            second = gps.time.second();
            minute = (minute + 30); // converting to IST
            if (minute > 59)
            {
                minute = minute - 60;
                hour = hour + 1;
            }
            hour = (hour + 5);
            if (hour > 23)
                hour = hour - 24; // converting to IST
            if (hour >= 12)       // checking whether AM or PM
                pm = 1;
            else
                pm = 0;
            hour = hour % 12;
            if (hour < 10)
                time_str = '0';
            time_str += String(hour); //values of hour,minute and time are stored in a string
            time_str += " : ";
            if (minute < 10)
                time_str += '0';
            time_str += String(minute); //values of hour,minute and time are stored in a string
            time_str += " : ";
            if (second < 10)
                time_str += '0';
            time_str += String(second); //values of hour,minute and time are stored in a string
            if (pm == 1)
                time_str += " PM ";
            else
                time_str += " AM ";
        }
    }
}

// This custom version of delay() ensures that the gps object is being "fed".
static void smartDelay(unsigned long ms)
{
    unsigned long start = millis();
    do
    {
        readDataFromGPS();
    } while (millis() - start < ms);
}

void connectToWiFi()
{
    // connect to wifi
    WiFi.mode(WIFI_STA);
    WiFi.begin(SSID, PASS);
    Serial.println("");

    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Connecting...");
    delay(1000);

    // Wait for connection
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
    }
    if (WiFi.status() == WL_CONNECTED)
    {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Connected!");
        delay(1000);

        uint8_t macAddr[6];
        WiFi.macAddress(macAddr);
        Serial.printf("Connected, mac address: %02x:%02x:%02x:%02x:%02x:%02x",
                      macAddr[0], macAddr[1], macAddr[2], macAddr[3], macAddr[4], macAddr[5]);
    }
    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(SSID);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    if (MDNS.begin("esp8266_mdns"))
    {
        Serial.println("MDNS responder started");
    }
}

// --- generate json payload ---
String generateJSONPayload()
{
    String json = "{";
    json += "\"device_id\": ";
    json += "\"";
    json += DEVICE_ID;
    json += "\"";
    json += ", ";
    json += "\"device_password\": ";
    json += "\"";
    json += DEVICE_PASSWORD;
    json += "\"";
    json += ", ";
    json += "\"temperature_c\": ";
    json += temperatureC != -1000.0 ? String(temperatureC, 2) : "null";
    json += ", ";
    json += "\"humidity\": ";
    json += humidity != -1000.0 ? String(humidity, 2) : "null";
    json += ", ";
    json += "\"rain\": ";
    json += String(rainMeasure, 2);
    json += ", ";
    json += "\"is_raining\": ";
    json += rainDigitalValue == 0 ? "true" : "false";
    json += ", ";
    json += "\"gps_lat\": ";
    json += lat_str != "" ? lat_str : "null";
    json += ", ";
    json += "\"gps_lng\": ";
    json += lng_str != "" ? lng_str : "null";
    json += "}";
    return json;
}

// --- On-Board Web Server Route Handlers ---
void indexRouteHandler()
{
    String jsonPayload = generateJSONPayload();
    Serial.println(jsonPayload);
    server.send(200, "application/json", jsonPayload);
}
void routeNotFound()
{
    String message = "404! Not Found";
    message += "URI";
    message += server.uri();
    message += "Method ";
    message += (server.method() == HTTP_GET) ? "GET" : "POST";
    message += "Arguments ";
    message += server.args();
    message += "";
    for (uint8_t i = 0; i < server.args(); i++)
    {
        message += " " + server.argName(i) + ": " + server.arg(i) + "";
    }
    server.send(404, "text/plain", message);
}

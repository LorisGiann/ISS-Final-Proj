%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "192.168.1.138",  "TCP", "8095").
context(ctxalarm, "192.168.1.115",  "TCP", "8097").
 qactor( sonarsimulator, ctxalarm, "alarmSonar.sonarSimulator").
  qactor( sonardatasource, ctxalarm, "alarmSonar.sonarHCSR04Support2021").
  qactor( datacleaner, ctxalarm, "alarmSonar.dataCleaner").
  qactor( robotalarmcontrol, ctxalarm, "alarmSonar.robotAlarmControl").
  qactor( ledalarmcontrol, ctxalarm, "alarmLed.ledAlarmControl").
  qactor( ledcommander, ctxserver, "it.unibo.ledcommander.Ledcommander").
  qactor( sonardisplay, ctxserver, "it.unibo.sonardisplay.Sonardisplay").
  qactor( sonar, ctxalarm, "it.unibo.sonar.Sonar").
  qactor( led, ctxalarm, "it.unibo.led.Led").

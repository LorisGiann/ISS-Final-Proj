%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
context(ctxrobot, "127.0.0.1",  "TCP", "8096").
context(ctxalarm, "127.0.0.1",  "TCP", "8097").
 qactor( distancefilter, ctxserver, "rx.distanceFilter").
  qactor( sonarsimulator, ctxalarm, "sonarSimulator").
  qactor( sonarsimulatortesting, ctxalarm, "sonarSimulatorTesting").
  qactor( sonardatasource, ctxalarm, "sonarHCSR04Support2021").
  qactor( datacleaner, ctxalarm, "dataCleaner").
  qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( alarmcontrol, ctxalarm, "it.unibo.alarmcontrol.Alarmcontrol").
  qactor( led, ctxalarm, "it.unibo.led.Led").

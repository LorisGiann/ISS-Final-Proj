%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
context(ctxrobot, "127.0.0.1",  "TCP", "8096").
context(ctxalarm, "127.0.0.1",  "TCP", "8097").
context(ctxgui, "127.0.0.1",  "TCP", "8098").
 qactor( distancefilter, ctxrobot, "rx.distanceFilter").
  qactor( sonarsimulator, ctxalarm, "alarmSonar.sonarSimulator").
  qactor( sonardatasource, ctxalarm, "alarmSonar.sonarHCSR04Support2021").
  qactor( datacleaner, ctxalarm, "alarmSonar.dataCleaner").
  qactor( robotalarmcontrol, ctxalarm, "alarmSonar.robotAlarmControl").
  qactor( ledalarmcontrol, ctxalarm, "alarmLed.ledAlarmControl").
  qactor( ledgui, ctxgui, "gui.updateLedGui").
  qactor( containergui, ctxgui, "gui.updateContainerGui").
  qactor( positiongui, ctxgui, "gui.updatePositionGui").
  qactor( statettgui, ctxgui, "gui.updateStateTransprtTrolleyGui").
  qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( depositaction, ctxserver, "it.unibo.depositaction.Depositaction").
  qactor( transporttrolley, ctxrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( alarmreceiverpickupdropdown, ctxrobot, "it.unibo.alarmreceiverpickupdropdown.Alarmreceiverpickupdropdown").
  qactor( pickupdropouthandler, ctxrobot, "it.unibo.pickupdropouthandler.Pickupdropouthandler").
  qactor( mover, ctxrobot, "it.unibo.mover.Mover").
  qactor( moveruturn, ctxrobot, "it.unibo.moveruturn.Moveruturn").
  qactor( alarmreceiverbasicrobot, ctxrobot, "it.unibo.alarmreceiverbasicrobot.Alarmreceiverbasicrobot").
  qactor( basicrobotwrapper, ctxrobot, "it.unibo.basicrobotwrapper.Basicrobotwrapper").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( sonar, ctxalarm, "it.unibo.sonar.Sonar").
  qactor( alarmemitter, ctxalarm, "it.unibo.alarmemitter.Alarmemitter").
  qactor( led, ctxalarm, "it.unibo.led.Led").

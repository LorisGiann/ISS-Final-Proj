%====================================================================================
% demo0 description   
%====================================================================================
context(ctxdriver, "localhost",  "TCP", "8094").
context(ctxserver, "localhost",  "TCP", "8095").
context(ctxrobot, "127.0.0.1",  "TCP", "8096").
context(ctxalarm, "127.0.0.1",  "TCP", "8097").
 qactor( distancefilter, ctxrobot, "rx.distanceFilter").
  qactor( smartdevice, ctxdriver, "it.unibo.smartdevice.Smartdevice").
  qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( depositaction, ctxserver, "it.unibo.depositaction.Depositaction").
  qactor( transporttrolley, ctxrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( pickupdropouthandler, ctxrobot, "it.unibo.pickupdropouthandler.Pickupdropouthandler").
  qactor( mover, ctxrobot, "it.unibo.mover.Mover").
  qactor( moveruturn, ctxrobot, "it.unibo.moveruturn.Moveruturn").
  qactor( basicrobotwrapper, ctxrobot, "it.unibo.basicrobotwrapper.Basicrobotwrapper").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( led, ctxalarm, "it.unibo.led.Led").
  qactor( alarm_emitter, ctxalarm, "it.unibo.alarm_emitter.Alarm_emitter").
  qactor( sonar, ctxalarm, "it.unibo.sonar.Sonar").
